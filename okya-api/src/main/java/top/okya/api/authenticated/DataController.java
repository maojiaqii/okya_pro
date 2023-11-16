package top.okya.api.authenticated;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.okya.component.annotation.ApiLog;
import top.okya.component.domain.HttpResult;
import top.okya.component.enums.OperationType;

/**
 * @author: maojiaqi
 * @Date: 2023/10/25 15:49
 * @describe: 数据请求接口
 */

@RestController
@RequestMapping("/data")
public class DataController {

    /**
     * 我的菜单
     */
    @GetMapping(value = "/icons")
    @ApiLog(title = "我的菜单", operationType = OperationType.SEARCH)
    public HttpResult getMyMenus() {
        return HttpResult.success();
    }

}
