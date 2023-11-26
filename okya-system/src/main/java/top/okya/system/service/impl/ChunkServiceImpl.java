package top.okya.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.okya.component.config.OkyaConfig;
import top.okya.component.constants.CharacterConstants;
import top.okya.component.domain.LoginUser;
import top.okya.component.domain.vo.ChunkVo;
import top.okya.component.enums.exception.ServiceExceptionType;
import top.okya.component.exception.ServiceException;
import top.okya.component.utils.common.DateFormatUtil;
import top.okya.component.utils.spring.SecurityContextUtil;
import top.okya.system.dao.AsUploaderFileChunkMapper;
import top.okya.system.domain.AsUploaderFileChunk;
import top.okya.system.service.ChunkService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2023/11/24 11:04
 * @describe: 文件上传Service实现类
 */
@Service
@Slf4j
public class ChunkServiceImpl implements ChunkService {
    /**
     * 分块文件未合并
     */
    private static final int CHUNK_UNMERGED = 0;
    /**
     * 分块文件已合并
     */
    private static final int CHUNK_MERGED = 1;

    @Autowired
    AsUploaderFileChunkMapper asUploaderFileChunkMapper;

    @Override
    public Map<String, Object> checkChunk(ChunkVo chunkVo) {
        return null;
    }

    @Override
    @Transactional
    public void upload(ChunkVo chunkVo) {
        LoginUser loginUser = SecurityContextUtil.getLoginUser();
        log.debug(chunkVo.toString());
        String identifier = chunkVo.getIdentifier();
        MultipartFile file = chunkVo.getFile();
        String fileName = chunkVo.getFilename();
        String chunkName = fileName + CharacterConstants.MINUS + chunkVo.getChunkNumber();
        String folderPathString = OkyaConfig.getFileFolder() + identifier;
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
}
