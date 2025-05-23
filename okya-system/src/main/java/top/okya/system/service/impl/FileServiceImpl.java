package top.okya.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import top.okya.component.config.OkyaConfig;
import top.okya.component.constants.CharacterConstants;
import top.okya.component.domain.LoginUser;
import top.okya.component.domain.vo.ChunkVo;
import top.okya.component.enums.UseStatus;
import top.okya.component.enums.exception.ServiceExceptionType;
import top.okya.component.exception.ServiceException;
import top.okya.component.global.Global;
import top.okya.component.utils.common.DateFormatUtil;
import top.okya.component.utils.common.IdUtil;
import top.okya.system.dao.AsUploaderFileChunkMapper;
import top.okya.system.dao.AsUploaderFileMapper;
import top.okya.system.domain.AsUploaderFile;
import top.okya.system.domain.AsUploaderFileChunk;
import top.okya.system.service.FileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author: maojiaqi
 * @Date: 2023/11/24 11:04
 * @describe: 文件上传Service实现类
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {
    /**
     * 分块文件未合并
     */
    private static final int CHUNK_UNMERGED = 0;
    /**
     * 分块文件已合并
     */
    private static final int CHUNK_MERGED = 1;
    /**
     * 每次下载的文件大小2M
     */
    private static final long ONCE_DOWNLOAD_SIZE = 1024 * 1024 * 2;
    /**
     * 分块文件存放文件夹名
     */
    private static final String CHUNK = "/chunk";

    /**
     * 上传锁
     */
    private static final ConcurrentHashMap<String, Object> uploadLock = new ConcurrentHashMap<>();

    /**
     * 合并锁
     */
    private static final ConcurrentHashMap<String, Object> mergeLock = new ConcurrentHashMap<>();

    @Autowired
    AsUploaderFileChunkMapper asUploaderFileChunkMapper;

    @Autowired
    AsUploaderFileMapper asUploaderFileMapper;

    @Override
    public List<Integer> checkChunk(ChunkVo chunkVo) {
        return asUploaderFileChunkMapper.queryByFileIdentifier(chunkVo.getIdentifier())
                .stream()
                .map(AsUploaderFileChunk::getChunkNum)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(ChunkVo chunkVo) {
        String identifier = chunkVo.getIdentifier();
        // 使用 ConcurrentHashMap 来确保同一 identifier 合并操作的互斥
        synchronized (uploadLock.computeIfAbsent(identifier, k -> new Object())) {
            try {
                LoginUser loginUser = Objects.requireNonNull(Global.getLoginUser());
                MultipartFile file = chunkVo.getFile();
                String fileName = chunkVo.getFilename();
                String chunkName = fileName + CharacterConstants.MINUS + chunkVo.getChunkNumber();
                Path chunkFolder = Paths.get(OkyaConfig.getFileFolder(), identifier + CHUNK);
                Path targetPath = chunkFolder.resolve(chunkName);

                // 先写入数据库
                AsUploaderFileChunk asUploaderFileChunk = new AsUploaderFileChunk()
                        .setChunkId(IdUtil.randomUUID())
                        .setChunkNum(chunkVo.getChunkNumber())
                        .setChunkName(chunkName)
                        .setChunkSize(chunkVo.getCurrentChunkSize())
                        .setTotalSize(chunkVo.getTotalSize())
                        .setTotalChunks(chunkVo.getTotalChunks())
                        .setFilePath(chunkFolder.toString())
                        .setFileName(fileName)
                        .setFileIdentifier(identifier)
                        .setMerged(CHUNK_UNMERGED)
                        .setUploadBy(loginUser.getUserCode())
                        .setUploadTime(DateFormatUtil.nowDate());
                asUploaderFileChunkMapper.insert(asUploaderFileChunk);
                try {
                    // 文件路径不存在
                    if (!Files.isWritable(chunkFolder)) {
                        Files.createDirectories(chunkFolder);
                    }
                    byte[] bytes = file.getBytes();
                    Files.deleteIfExists(targetPath);
                    //文件写入指定路径
                    Files.write(targetPath, bytes);

                    // 注册事务同步处理
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                        }

                        @Override
                        public void afterCompletion(int status) {
                            if (status == STATUS_ROLLED_BACK) {
                                deleteQuietly(targetPath);
                            }
                        }
                    });
                } catch (IOException e) {
                    log.error("文件生成失败", e);
                    throw new ServiceException(ServiceExceptionType.GENERATE_FILE_FAILED, targetPath.toString());
                }
            } finally {
                uploadLock.remove(identifier);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void merge(String identifier) {
        // 使用 ConcurrentHashMap 来确保同一 identifier 合并操作的互斥
        synchronized (mergeLock.computeIfAbsent(identifier, k -> new Object())) {
            try {
                LoginUser loginUser = Objects.requireNonNull(Global.getLoginUser());
                List<AsUploaderFileChunk> unmergedChunks = asUploaderFileChunkMapper.queryByFileIdentifier(identifier)
                        .stream()
                        .filter(i -> Objects.equals(i.getMerged(), CHUNK_UNMERGED))
                        .collect(Collectors.toList());
                if (unmergedChunks.isEmpty()) {
                    return;
                }
                AsUploaderFileChunk chunk = unmergedChunks.get(0);
                String fileName = chunk.getFileName();
                Path chunkFolder = Paths.get(OkyaConfig.getFileFolder(), identifier + CHUNK);
                Path targetDir = Paths.get(OkyaConfig.getFileFolder(), identifier);
                Path targetPath = targetDir.resolve(fileName);

                // 更新分片状态为已合并并插入文件记录
                asUploaderFileChunkMapper.updateMerge(identifier, CHUNK_MERGED);
                AsUploaderFile asUploaderFile = new AsUploaderFile()
                        .setFileId(IdUtil.randomUUID())
                        .setFileName(fileName)
                        .setFileIdentifier(identifier)
                        .setFilePath(targetPath.toString())
                        .setFileSize(chunk.getTotalSize())
                        .setUploadBy(loginUser.getUserCode())
                        .setUploadTime(DateFormatUtil.nowDate());
                asUploaderFileMapper.insert(asUploaderFile);

                // 文件合并操作
                List<Path> chunksToDelete = new ArrayList<>();
                try {
                    // 确保目标目录存在
                    Files.createDirectories(targetDir);
                    Files.deleteIfExists(targetPath);
                    Files.createFile(targetPath);
                    // 获取并排序分片文件
                    List<Path> sortedChunks = Files.list(chunkFolder)
                            .filter(path -> path.getFileName().toString().contains("-"))
                            .sorted(this::compareChunkPaths)
                            .collect(Collectors.toList());

                    // 流式合并分片
                    try (OutputStream os = Files.newOutputStream(targetPath, StandardOpenOption.APPEND)) {
                        for (Path chunkPath : sortedChunks) {
                            Files.copy(chunkPath, os);
                            chunksToDelete.add(chunkPath);
                        }
                    }

                    // 注册事务同步处理
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            deleteChunksAndFolder(chunksToDelete, chunkFolder);
                        }

                        @Override
                        public void afterCompletion(int status) {
                            if (status == STATUS_ROLLED_BACK) {
                                deleteQuietly(targetPath);
                            }
                        }
                    });
                } catch (IOException e) {
                    log.error("文件合并失败", e);
                    throw new ServiceException(ServiceExceptionType.MERGE_FILE_FAILED, targetPath.toString());
                }
            } finally {
                mergeLock.remove(identifier);
            }
        }
    }

    @Override
    public AsUploaderFile getInfo(String fileIdentifier) {
        AsUploaderFile asUploaderFile = asUploaderFileMapper.queryByIdentifier(fileIdentifier);
        if (Objects.isNull(asUploaderFile)) {
            throw new ServiceException(ServiceExceptionType.GET_FILE_INFO_FAILED);
        }
        Path path = Paths.get(asUploaderFile.getFilePath());
        if (!Files.exists(path)) {
            throw new ServiceException(ServiceExceptionType.FILE_NOT_EXISTS);
        }
        return asUploaderFile;
    }

    @Override
    public int downLoadCount(String fileIdentifier) {
        AsUploaderFile asUploaderFile = getInfo(fileIdentifier);
        Path path = Paths.get(asUploaderFile.getFilePath());
        try {
            return (int) Math.ceil(Files.size(path) * 1.0 / ONCE_DOWNLOAD_SIZE);
        } catch (IOException e) {
            throw new ServiceException(ServiceExceptionType.FILE_IO_ERROR);
        }
    }

    @Override
    public void downLoad(String fileIdentifier, int no, HttpServletRequest request, HttpServletResponse response) {
        AsUploaderFile asUploaderFile = getInfo(fileIdentifier);
        Path path = Paths.get(asUploaderFile.getFilePath());
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
             BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream(), 8192)) {

            String fileName = path.getFileName().toString();
            // http状态码要为206：表示获取部分内容
            response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");

            long fileLength = fileChannel.size();
            // 开始下载位置
            long firstBytePos;
            // 结束下载位置
            long lastBytePos;
            final int groups = (int) Math.ceil(fileLength * 1.0 / ONCE_DOWNLOAD_SIZE);

            // 计算下载范围
            if (no < 0) {
                firstBytePos = 0;
                lastBytePos = fileLength - 1;
            } else {
                if (no > groups) {
                    return;
                } else {
                    firstBytePos = no * ONCE_DOWNLOAD_SIZE + (no == 0 ? 0 : 1);
                    if (firstBytePos > fileLength) {
                        return;
                    }
                    lastBytePos = ((no + 1) * ONCE_DOWNLOAD_SIZE > fileLength) ? (fileLength - 1) : (no + 1) * ONCE_DOWNLOAD_SIZE;
                }
            }

            // Content-Length: 本次内容的大小
            long downLoadedRealLength = lastBytePos - firstBytePos + 1;
            response.setContentLengthLong(downLoadedRealLength);
            log.info("本次下载文件：{}，起始位置：{}，结束位置：{}，下载长度：{}，总长度：{}", fileName, firstBytePos, lastBytePos, downLoadedRealLength, fileLength);

            // 设置文件位置
            fileChannel.position(firstBytePos);

            // 使用固定大小的缓冲区分块读取4KB的缓冲区
            int bufferSize = 4096;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

            long bytesRemaining = downLoadedRealLength;
            int bytesRead;

            // 分块读取并写入响应
            while (bytesRemaining > 0 && (bytesRead = fileChannel.read(buffer)) != -1) {
                buffer.flip(); // 切换到读模式

                int bytesToWrite = (int) Math.min(bytesRead, bytesRemaining);
                outputStream.write(buffer.array(), 0, bytesToWrite);

                bytesRemaining -= bytesToWrite;
                buffer.clear(); // 清空缓冲区，准备下一次读取
            }

            outputStream.flush();
        } catch (FileNotFoundException e) {
            throw new ServiceException(ServiceExceptionType.FILE_NOT_EXISTS);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(ServiceExceptionType.SERVER_EXCEPTION);
        } catch (IOException e) {
            throw new ServiceException(ServiceExceptionType.FILE_IO_ERROR);
        }
    }

    // 分片文件比较逻辑
    private int compareChunkPaths(Path p1, Path p2) {
        String filename1 = p1.getFileName().toString();
        String filename2 = p2.getFileName().toString();
        int num1 = extractChunkNumber(filename1);
        int num2 = extractChunkNumber(filename2);
        return Integer.compare(num1, num2);
    }

    // 提取分片编号
    private int extractChunkNumber(String filename) {
        int lastDash = filename.lastIndexOf("-");
        if (lastDash == -1) {
            throw new ServiceException(ServiceExceptionType.INVALID_CHUNK_NAME, filename);
        }
        try {
            return Integer.parseInt(filename.substring(lastDash + 1));
        } catch (NumberFormatException e) {
            throw new ServiceException(ServiceExceptionType.INVALID_CHUNK_NAME, filename);
        }
    }

    // 安全删除分片文件和文件夹
    private void deleteChunksAndFolder(List<Path> chunks, Path folder) {
        chunks.forEach(this::deleteQuietly);
        try {
            Files.deleteIfExists(folder);
        } catch (IOException e) {
            log.error("无法删除分片目录，可能不为空: {}", folder, e);
        }
    }

    // 静默删除文件
    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("文件删除失败: {}", path, e);
        }
    }

}
