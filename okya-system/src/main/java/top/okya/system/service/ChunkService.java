package top.okya.system.service;

import top.okya.component.domain.vo.ChunkVo;

import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2023/11/24 14:38
 * @describe: 文件上传Service
 */
public interface ChunkService {

    /**
     * 校验文件分片上传
     *
     * @param chunkVo
     * @return
     */
    Map<String, Object> checkChunk(ChunkVo chunkVo);

    /**
     * 文件分片上传
     *
     * @param chunkVo
     */
    void upload(ChunkVo chunkVo);
}
