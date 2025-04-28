package top.okya.api.authenticated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.okya.component.annotation.ApiLog;
import top.okya.component.domain.HttpResult;
import top.okya.component.domain.vo.RolePermissionVo;
import top.okya.component.domain.vo.UserPwdVo;
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

    /**
     * 所有权限控制的按钮
     */
    @GetMapping(value = "/tablePermiButtons")
    @ApiLog(title = "所有权限控制的按钮", operationType = OperationType.SEARCH)
    public HttpResult getTablePermiButtons() {
        return HttpResult.success(permissionService.getTablePermiButtons());
    }

    /**
     * 所有权限控制的表格列
     */
    @GetMapping(value = "/tablePermiColumns")
    @ApiLog(title = "所有权限控制的表格列", operationType = OperationType.SEARCH)
    public HttpResult getTablePermiColumns() {
        return HttpResult.success(permissionService.getTablePermiColumns());
    }

    /**
     * 所有权限控制的表单字段
     */
    @GetMapping(value = "/formPermiFields")
    @ApiLog(title = "所有权限控制的表单字段", operationType = OperationType.SEARCH)
    public HttpResult getFormPermiFields() {
        return HttpResult.success(permissionService.getFormPermiFields());
    }

    /**
     * 角色权限集合
     */
    @GetMapping(value = "/getRolePermissions")
    @ApiLog(title = "角色权限集合", operationType = OperationType.SEARCH)
    public HttpResult getRolePermissions(@NotBlank(message = "角色id不能为空！") @RequestParam(value = "roleId") String roleId) {
        return HttpResult.success(permissionService.getRolePermissions(roleId));
    }

    /**
     * 保存角色权限集合
     */
    @PostMapping(value = "/saveRolePermissions")
    @ApiLog(title = "保存角色权限集合", operationType = OperationType.GRANT)
    public HttpResult saveRolePermissions(@Validated @RequestBody RolePermissionVo rolePermissionVo) {
        permissionService.saveRolePermissions(rolePermissionVo);
        return HttpResult.success();
    }

    /**
     * 重置密码
     */
    @PostMapping(value = "/resetUserPwd")
    @ApiLog(title = "重置密码", operationType = OperationType.UPDATE)
    public HttpResult resetUserPwd(@Validated @RequestBody UserPwdVo userPwdVo) {
        permissionService.resetPwd(userPwdVo);
        return HttpResult.success();
    }

    /**
     * 重置用户状态
     */
    @GetMapping(value = "/resetUserStatus")
    @ApiLog(title = "重置用户状态", operationType = OperationType.UPDATE)
    public HttpResult resetUserStatus(@NotBlank(message = "用户id不能为空！") @RequestParam(value = "user_id") String user_id) {
        permissionService.resetStatus(user_id);
        return HttpResult.success();
    }
}
