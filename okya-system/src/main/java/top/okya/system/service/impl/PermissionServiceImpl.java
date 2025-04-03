package top.okya.system.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.okya.component.constants.CharacterConstants;
import top.okya.component.domain.LoginUser;
import top.okya.component.domain.Permission;
import top.okya.component.domain.child.PermissionMetaPermis;
import top.okya.component.domain.dto.AsUser;
import top.okya.component.domain.vo.UserPwdVo;
import top.okya.component.global.Global;
import top.okya.component.utils.security.SecureUtil;
import top.okya.component.utils.ui.UiUtil;
import top.okya.system.dao.AsPermissionMapper;
import top.okya.component.domain.dto.AsPermission;
import top.okya.system.dao.AsUserMapper;
import top.okya.system.service.PermissionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: maojiaqi
 * @Date: 2023/9/20 15:07
 * @describe: 权限相关service实现
 */

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    AsPermissionMapper asPermissionMapper;

    @Autowired
    AsUserMapper asUserMapper;

    @Override
    public List<Map<String, Object>> myTenancys() {
        LoginUser loginUser = Global.getLoginUser();
        AsUser asUser = loginUser.getAsUser();
        String userId = asUser.isAdmin() ? null : asUser.getUserId();
        return asPermissionMapper.queryTenancysByUserId(userId);
    }

    @Override
    public void resetPwd(UserPwdVo userPwdVo) {
        String userId = userPwdVo.getUserId();
        if (Objects.isNull(userId)) {
            LoginUser loginUser = Global.getLoginUser();
            AsUser asUser = loginUser.getAsUser();
            userId = asUser.getUserId();
        }
        asUserMapper.updatePwdByUserId(userId, SecureUtil.encode(userPwdVo.getPassword()));
    }

    @Override
    public void resetStatus(String userId) {
        asUserMapper.updateStatusByUserId(userId);
    }

    @Override
    public List<Permission> myPermissions(String currentTenancy) {
        LoginUser loginUser = Global.getLoginUser();
        AsUser asUser = loginUser.getAsUser();
        String userId = asUser.isAdmin() ? null : asUser.getUserId();
        List<AsPermission> asPermissions = asPermissionMapper.queryByUserId(userId, currentTenancy).stream().distinct().collect(Collectors.toList());
        // 新增加按钮、表格列、表单字段权限
        PermissionMetaPermis permissionMetaPermis = new PermissionMetaPermis();
        if (asUser.isAdmin()) {
            // admin角色的用户默认全部权限
            ArrayList<String> allPermis = Lists.newArrayList(CharacterConstants.STAR);
            permissionMetaPermis.setButtons(allPermis)
                    .setColumns(allPermis)
                    .setFields(allPermis);
        } else {
            permissionMetaPermis.setButtons(asPermissionMapper.queryButtonsByUserId(userId))
                    .setColumns(asPermissionMapper.queryColumnsByUserId(userId))
                    .setFields(asPermissionMapper.queryFieldsByUserId(userId));
        }
        asPermissions.forEach(obj -> {
            obj.setPermission(permissionMetaPermis);
        });
        return UiUtil.formatPermissions(asPermissions, "0");
    }
}
