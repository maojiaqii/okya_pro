package top.okya.api.authenticated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.okya.component.annotation.ApiLog;
import top.okya.component.domain.HttpResult;
import top.okya.component.enums.OperationType;
import top.okya.system.service.PermissionService;

/**
 * @author: maojiaqi
 * @Date: 2023/7/21 17:29
 * @describe: 菜单、按钮、角色等权限
 */

@RestController
@RequestMapping("/perms")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 我的菜单
     */
    @GetMapping(value = "/myMenus")
    @ApiLog(title = "我的菜单", operationType = OperationType.SEARCH)
    public HttpResult getMyMenus() {
        return HttpResult.success(permissionService.myMenus());
    }

    /**
     * 我的按钮
     */
    @GetMapping(value = "/myButtons")
    @ApiLog(title = "我的按钮", operationType = OperationType.SEARCH)
    public HttpResult getMyButtons() {
        return HttpResult.success(permissionService.myButtons());
    }
}
