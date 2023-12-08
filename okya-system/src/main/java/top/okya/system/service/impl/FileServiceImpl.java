package top.okya.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
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
    private static final long ONECE_DOWNLOAD_SIZE = 1024 * 1024 * 2;
    /**
     * 分块文件存放文件夹名
     */
    private static final String CHUNK = "/chunk";

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
    @Transactional
    public void upload(ChunkVo chunkVo) {
        LoginUser loginUser = Global.getLoginUser();
        String identifier = chunkVo.getIdentifier();
        MultipartFile file = chunkVo.getFile();
        String fileName = chunkVo.getFilename();
        String chunkName = fileName + CharacterConstants.MINUS + chunkVo.getChunkNumber();
        String folderPathString = OkyaConfig.getFileFolder() + identifier + CHUNK;
        String filePathString = folderPathString + CharacterConstants.FORWARD_SLASH + chunkName;
        // 先写入数据库
        AsUploaderFileChunk asUploaderFileChunk = new AsUploaderFileChunk()
                .setChunkNum(chunkVo.getChunkNumber())
                .setChunkName(chunkName)
                .setChunkSize(chunkVo.getCurrentChunkSize())
                .setTotalSize(chunkVo.getTotalSize())
                .setTotalChunks(chunkVo.getTotalChunks())
                .setFilePath(folderPathString)
                .setFileName(fileName)
                .setFileIdentifier(identifier)
                .setMerged(CHUNK_UNMERGED)
                .setUploadBy(loginUser.getUserCode())
                .setUploadTime(DateFormatUtil.nowDate());
        asUploaderFileChunkMapper.insert(asUploaderFileChunk);
        try {
            Path folder = Paths.get(folderPathString);
            // 文件路径不存在
            if (!Files.isWritable(folder)) {
                Files.createDirectories(folder);
            }
            byte[] bytes = file.getBytes();
            Path path = Paths.get(filePathString);
            Files.deleteIfExists(path);
            //文件写入指定路径
            Files.write(path, bytes);
        } catch (IOException e) {
            log.error("文件生成失败", e);
            throw new ServiceException(ServiceExceptionType.GENERATE_FILE_FAILED, filePathString);
        }
    }

    @Override
    @Transactional
    public void merge(String identifier) {
        LoginUser loginUser = Global.getLoginUser();
        List<AsUploaderFileChunk> asUploaderFileChunkList = asUploaderFileChunkMapper.queryByFileIdentifier(identifier)
                .stream()
                .filter(i -> Objects.equals(i.getMerged(), CHUNK_UNMERGED))
                .collect(Collectors.toList());
        if (asUploaderFileChunkList.isEmpty()) {
            return;
        }
        AsUploaderFileChunk asUploaderFileChunk = asUploaderFileChunkList.get(0);
        String fileName = asUploaderFileChunk.getFileName();
        String chunkFileFolder = OkyaConfig.getFileFolder() + identifier + CHUNK;
        String filePath = OkyaConfig.getFileFolder() + identifier + CharacterConstants.FORWARD_SLASH + fileName;
        asUploaderFileChunkMapper.updateMerge(identifier, CHUNK_MERGED);
        AsUploaderFile asUploaderFile = new AsUploaderFile()
                .setFileName(fileName)
                .setFileIdentifier(identifier)
                .setFilePath(filePath)
                .setFileSize(asUploaderFileChunk.getTotalSize())
                .setStatus(UseStatus.OK.getCode())
                .setUploadBy(loginUser.getUserCode())
                .setUploadTime(DateFormatUtil.nowDate());
        asUploaderFileMapper.insert(asUploaderFile);
        try {
            Path pas = Paths.get(filePath);
            Files.deleteIfExists(pas);
            Files.createFile(pas);
            Files.list(Paths.get(chunkFileFolder))
                    .filter(path -> path.getFileName().toString().contains("-"))
                    .sorted((o1, o2) -> {
                        String p1 = o1.getFileName().toString();
                        String p2 = o2.getFileName().toString();
                        int i1 = p1.lastIndexOf("-");
                        int i2 = p2.lastIndexOf("-");
                        return Integer.valueOf(p2.substring(i2)).compareTo(Integer.valueOf(p1.substring(i1)));
                    })
                    .forEach(path -> {
                        try {
                            //以追加的形式写入文件
                            Files.write(Paths.get(filePath), Files.readAllBytes(path), StandardOpenOption.APPEND);
                            //合并后删除该块
                            Files.delete(path);
                        } catch (IOException e) {
                            log.error("文件合并失败", e);
                            throw new ServiceException(ServiceExceptionType.MERGE_FILE_FAILED, filePath);
                        }
                    });
        } catch (IOException e) {
            log.error("文件合并失败", e);
            throw new ServiceException(ServiceExceptionType.MERGE_FILE_FAILED, filePath);
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
        AsUploaderFile asUploaderFile = asUploaderFileMapper.queryByIdentifier(fileIdentifier);
        if (Objects.isNull(asUploaderFile)) {
            throw new ServiceException(ServiceExceptionType.GET_FILE_INFO_FAILED);
        }
        Path path = Paths.get(asUploaderFile.getFilePath());
        if (!Files.exists(path)) {
            throw new ServiceException(ServiceExceptionType.FILE_NOT_EXISTS);
        }
        try {
            return (int) Math.ceil(Files.size(path) * 1.0 / ONECE_DOWNLOAD_SIZE);
        } catch (IOException e) {
            throw new ServiceException(ServiceExceptionType.FILE_IO_ERROR);
        }
    }

    @Override
    public void downLoad(String fileIdentifier, int no, HttpServletRequest request, HttpServletResponse response) throws IOException {
        AsUploaderFile asUploaderFile = asUploaderFileMapper.queryByIdentifier(fileIdentifier);
        if (Objects.isNull(asUploaderFile)) {
            throw new ServiceException(ServiceExceptionType.GET_FILE_INFO_FAILED);
        }
        Path path = Paths.get(asUploaderFile.getFilePath());
        if (!Files.exists(path)) {
            throw new ServiceException(ServiceExceptionType.FILE_NOT_EXISTS);
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            String fileName = path.getFileName().toString();
            // http状态码要为206：表示获取部分内容
            response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");

            long fileLength = Files.size(path);
            // 开始下载位置
            long firstBytePos;
            // 结束下载位置
            long lastBytePos;
            final int groups = (int) Math.ceil(fileLength * 1.0 / ONECE_DOWNLOAD_SIZE);
            if (no < 0) {
                firstBytePos = 0;
                lastBytePos = fileLength - 1;
            } else {
                if (no > groups) {
                    return;
                } else {
                    firstBytePos = no * ONECE_DOWNLOAD_SIZE + (no == 0 ? 0 : 1);
                    if (firstBytePos > fileLength) {
                        return;
                    }
                    lastBytePos = ((no + 1) * ONECE_DOWNLOAD_SIZE > fileLength) ? (fileLength - 1) : (no + 1) * ONECE_DOWNLOAD_SIZE;
                }
            }
            // Content-Length: 11，本次内容的大小
            long downLoadedRealLength = lastBytePos - firstBytePos + 1;
            response.setContentLengthLong(downLoadedRealLength);
            log.info("本次下载文件：{}，起始位置：{}，结束位置：{}，下载长度：{}，总长度：{}", fileName, firstBytePos, lastBytePos, downLoadedRealLength, fileLength);
            // Open for reading only
            final String fileOpenMode = "r";
            RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), fileOpenMode);
            randomAccessFile.seek(firstBytePos);
            // 开始读取文件
            inputStream = Channels.newInputStream(randomAccessFile.getChannel());
            outputStream = new BufferedOutputStream(response.getOutputStream());
            byte[] buffer = new byte[(int) downLoadedRealLength];
            inputStream.read(buffer);
            outputStream.write(buffer);
        } catch (FileNotFoundException e) {
            throw new ServiceException(ServiceExceptionType.FILE_NOT_EXISTS);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(ServiceExceptionType.SERVER_EXCEPTION);
        } catch (IOException e) {
            throw new ServiceException(ServiceExceptionType.FILE_IO_ERROR);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

}
