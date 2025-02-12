package top.okya.system.service;

import top.okya.component.domain.Permission;
import top.okya.component.domain.vo.UserPwdVo;

import java.util.List;
import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2023/9/20 15:03
 * @describe: 权限相关service
 */
public interface PermissionService {
    List<Permission> myPermissions(String currentTenancy);

    List<Map<String, Object>> myTenancys();

    void resetPwd(UserPwdVo userPwdVo);

    void resetStatus(String userId);
}
