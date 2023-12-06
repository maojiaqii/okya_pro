package top.okya.api.authenticated;

import org.apache.tomcat.util.http.parser.ContentRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.okya.component.annotation.ApiLog;
import top.okya.component.constants.CommonConstants;
import top.okya.component.domain.HttpResult;
import top.okya.component.domain.vo.ChunkVo;
import top.okya.component.enums.OperationType;
import top.okya.system.service.FileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
     * 下载文件
     */
    @GetMapping(value = "/downLoad")
    @ApiLog(title = "下载文件", operationType = OperationType.DOWNLOAD)
    public void downLoad(String fileIdentifier, HttpServletRequest request, HttpServletResponse response) {
        // http状态码要为206：表示获取部分内容
        response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
        // Accept-Ranges：bytes，表示支持Range请求
        response.setHeader(HttpHeaders.ACCEPT_RANGES, CommonConstants.BYTES_STRING);
        response.setCharacterEncoding("utf-8");
        fileService.downLoad(fileIdentifier, request, response);
    }

}
