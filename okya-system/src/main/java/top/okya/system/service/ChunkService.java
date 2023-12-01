package top.okya.system.service;

import top.okya.component.domain.vo.ChunkVo;

import java.util.List;

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
    List<Integer> checkChunk(ChunkVo chunkVo);

    /**
     * 文件分片上传
     *
     * @param chunkVo
     */
    void upload(ChunkVo chunkVo);

    /**
     * 文件合并
     *
     * @param identifier
     */
    void merge(String identifier);
}
