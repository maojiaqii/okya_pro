package top.okya.api.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import top.okya.component.annotation.ApiLog;
import top.okya.component.annotation.TestController;
import top.okya.component.domain.HttpResult;
import top.okya.component.enums.OperationType;
import top.okya.component.enums.exception.ServiceExceptionType;
import top.okya.component.exception.ServiceException;
import top.okya.component.domain.dto.AsUser;
import top.okya.system.service.OperationService;

/**
 * @author: maojiaqi
 * @Date: 2023/7/19 14:11
 * @describe：
 */

@TestController
public class TestyController {

    @Autowired
    OperationService operationService;

    @ApiLog(title = "自定义异常测试", operationType = OperationType.TEST)
    @GetMapping("/a")
    public HttpResult testa() {
        throw new ServiceException(ServiceExceptionType.SERVER_EXCEPTION, new Object[]{"异常测试"}, new AsUser().setUserId("1").setEmail("1233333"));
    }

    @ApiLog(title = "查询测试", operationType = OperationType.SEARCH)
    @GetMapping("/b")
    public HttpResult testb() {
        return HttpResult.success(operationService.queryAll());
    }

    @ApiLog(title = "运行时异常测试", operationType = OperationType.TEST)
    @GetMapping("/ac")
    public HttpResult testc() {
        throw new RuntimeException("运行时异常");
    }
}
