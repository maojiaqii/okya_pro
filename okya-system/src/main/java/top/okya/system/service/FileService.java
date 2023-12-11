package top.okya.system.service;

import top.okya.component.domain.vo.ChunkVo;
import top.okya.system.domain.AsUploaderFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2023/11/24 14:38
 * @describe: 文件上传Service
 */
public interface FileService {

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

    /**
     * 获取文件信息
     *
     * @param fileIdentifier
     * @return 文件信息
     */
    AsUploaderFile getInfo(String fileIdentifier);

    /**
     * 下载文件
     *
     * @param fileIdentifier
     * @param no
     * @param request
     * @param response
     * @exception IOException
     */
    void downLoad(String fileIdentifier, int no, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 下载文件数量
     *
     * @param fileIdentifier
     * @return 下载文件数量
     */
    int downLoadCount(String fileIdentifier);
}
