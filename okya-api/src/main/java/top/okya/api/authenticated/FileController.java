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
import top.okya.system.service.ChunkService;

/**
 * @author: maojiaqi
 * @Date: 2023/11/25 20:31
 * @describe: 文件上传
 */

@RestController
@RequestMapping("/file")
public class FileController {


    @Autowired
    ChunkService chunkService;

    /**
     * 校验文件分片上传
     */
    @GetMapping(value = "/uploader")
    @ApiLog(title = "校验文件分片上传", operationType = OperationType.UPLOAD)
    public HttpResult checkChunk(ChunkVo chunkVo) {
        return HttpResult.success("校验文件分片上传完成！", chunkService.checkChunk(chunkVo));
    }

    /**
     * 文件分片上传
     */
    @PostMapping(value = "/uploader")
    @ApiLog(title = "文件分片上传", operationType = OperationType.UPLOAD)
    public HttpResult upload(ChunkVo chunkVo) {
        chunkService.upload(chunkVo);
        return HttpResult.success();
    }

    /**
     * 文件合并
     */
    @GetMapping(value = "/merge")
    @ApiLog(title = "文件合并", operationType = OperationType.UPLOAD)
    public HttpResult merge(@Validated ChunkVo chunkVo) {
        chunkService.merge(chunkVo.getIdentifier());
        return HttpResult.success();
    }
}