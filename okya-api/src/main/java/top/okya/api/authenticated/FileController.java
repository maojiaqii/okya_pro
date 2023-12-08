package top.okya.api.authenticated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.okya.component.annotation.ApiLog;
import top.okya.component.domain.HttpResult;
import top.okya.component.domain.vo.ChunkVo;
import top.okya.component.enums.OperationType;
import top.okya.system.service.FileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: maojiaqi
 * @Date: 2023/11/25 20:31
 * @describe: 文件上传
 */

@RestController
@RequestMapping("/file")
public class FileController {


    @Autowired
    FileService fileService;

    /**
     * 校验文件分片上传
     */
    @GetMapping(value = "/uploader")
    @ApiLog(title = "校验文件分片上传", operationType = OperationType.UPLOAD)
    public HttpResult checkChunk(ChunkVo chunkVo) {
        return HttpResult.success("校验文件分片上传完成！", fileService.checkChunk(chunkVo));
    }

    /**
     * 文件分片上传
     */
    @PostMapping(value = "/uploader")
    @ApiLog(title = "文件分片上传", operationType = OperationType.UPLOAD)
    public HttpResult upload(ChunkVo chunkVo) {
        fileService.upload(chunkVo);
        return HttpResult.success();
    }

    /**
     * 文件合并
     */
    @GetMapping(value = "/merge")
    @ApiLog(title = "文件合并", operationType = OperationType.UPLOAD)
    public HttpResult merge(@Validated ChunkVo chunkVo) {
        fileService.merge(chunkVo.getIdentifier());
        return HttpResult.success();
    }

    /**
     * 获取文件信息
     */
    @GetMapping(value = "/getInfo")
    @ApiLog(title = "获取文件信息", operationType = OperationType.SEARCH)
    public HttpResult getInfo(String fileIdentifier) {
        return HttpResult.success(fileService.getInfo(fileIdentifier));
    }

    /**
     * 文件下载总次数
     */
    @GetMapping(value = "/downLoadCount")
    @ApiLog(title = "文件下载总次数", operationType = OperationType.SEARCH)
    public HttpResult downLoadCount(String fileIdentifier) {
        return HttpResult.success(fileService.downLoadCount(fileIdentifier));
    }

    /**
     * 下载文件
     */
    @GetMapping(value = "/downLoad")
    @ApiLog(title = "下载文件", operationType = OperationType.DOWNLOAD)
    public void downLoad(String fileIdentifier, int no, HttpServletRequest request, HttpServletResponse response) throws IOException {
        fileService.downLoad(fileIdentifier, no, request, response);
    }

}
