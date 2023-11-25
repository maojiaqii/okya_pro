package top.okya.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.okya.component.config.OkyaConfig;
import top.okya.component.domain.vo.ChunkVo;
import top.okya.system.service.ChunkService;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2023/11/24 11:04
 * @describe: 文件上传Service实现类
 */
@Service
@Slf4j
public class ChunkServiceImpl implements ChunkService {
    @Override
    public Map<String, Object> checkChunk(ChunkVo chunkVo) {
        return null;
    }

    @Override
    public String upload(ChunkVo chunkVo) {
        log.debug(chunkVo.toString());
        String identifier = chunkVo.getIdentifier();
        MultipartFile file = chunkVo.getFile();
        String path = OkyaConfig.getFileFolder() + identifier;
        File outDir = new File(path);
        if(!outDir.exists()){
            outDir.mkdirs();
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(generatePath(uploadFolder, chunk));
            //文件写入指定路径
            Files.write(path, bytes);
            log.debug("文件 {} 写入成功, uuid:{}", chunk.getFilename(), chunk.getIdentifier());
            chunkService.saveChunk(chunk);

            return "文件上传成功";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
