package top.okya.api.authenticated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.okya.component.annotation.ApiLog;
import top.okya.component.domain.HttpResult;
import top.okya.component.enums.OperationType;
import top.okya.system.service.PermissionService;

import javax.validation.constraints.NotBlank;

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
     * 我的租户
     */
    @GetMapping(value = "/myTenancys")
    @ApiLog(title = "我的租户", operationType = OperationType.SEARCH)
    public HttpResult getMyTenancys() {
        return HttpResult.success(permissionService.myTenancys());
    }
    /**
     * 我的菜单
     */
    @GetMapping(value = "/myPermissions")
    @ApiLog(title = "我的菜单", operationType = OperationType.SEARCH)
    public HttpResult getMyPermissions(@NotBlank(message = "租户id不能为空！") @RequestParam(value = "currentTenancy") String currentTenancy) {
        return HttpResult.success(permissionService.myPermissions(currentTenancy));
    }
}
