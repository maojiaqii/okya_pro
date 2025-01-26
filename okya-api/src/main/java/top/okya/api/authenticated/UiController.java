package top.okya.api.authenticated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.okya.component.annotation.ApiLog;
import top.okya.component.domain.HttpResult;
import top.okya.component.enums.OperationType;
import top.okya.system.service.UiService;

import javax.validation.constraints.NotBlank;

/**
 * @author: maojiaqi
 * @Date: 2023/10/25 15:49
 * @describe: 页面Ui请求接口
 */

@RestController
@RequestMapping("/ui")
@Validated
public class UiController {

    @Autowired
    UiService uiService;

    /**
     * 表格信息
     */
    @GetMapping(value = "/jsInfo")
    @ApiLog(title = "js代码信息", operationType = OperationType.SEARCH)
    public HttpResult jsInfo(@NotBlank(message = "js编码不能为空！") @RequestParam(value = "code") String jsCode) {
        return HttpResult.success(uiService.getJsInfo(jsCode));
    }

    /**
     * 表格信息
     */
    @GetMapping(value = "/formInfo")
    @ApiLog(title = "表单信息", operationType = OperationType.SEARCH)
    public HttpResult formInfo(@NotBlank(message = "表单编码不能为空！") @RequestParam(value = "code") String formCode) {
        return HttpResult.success(uiService.getFormInfo(formCode));
    }

    /**
     * 表格信息
     */
    @GetMapping(value = "/tableInfo")
    @ApiLog(title = "表格信息", operationType = OperationType.SEARCH)
    public HttpResult tableInfo(@NotBlank(message = "表格编码不能为空！") @RequestParam(value = "code") String tableCode) {
        return HttpResult.success(uiService.getTableInfo(tableCode));
    }
}
