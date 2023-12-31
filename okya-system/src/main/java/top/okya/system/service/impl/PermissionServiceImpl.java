package top.okya.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.okya.component.domain.LoginUser;
import top.okya.component.domain.dto.AsMenu;
import top.okya.component.domain.dto.AsUser;
import top.okya.component.global.Global;
import top.okya.system.dao.AsMenuMapper;
import top.okya.system.service.PermissionService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: maojiaqi
 * @Date: 2023/9/20 15:07
 * @describe: 权限相关service实现
 */

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    AsMenuMapper asMenuMapper;

    @Override
    public List<AsMenu> myMenus() {
        LoginUser loginUser = Global.getLoginUser();
        AsUser asUser = loginUser.getAsUser();
        Long userId = asUser.isAdmin() ? null : asUser.getUserId();
        return asMenuMapper.selectMenuListByUserId(userId).stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<AsMenu> myButtons() {
        LoginUser loginUser = Global.getLoginUser();
        AsUser asUser = loginUser.getAsUser();
        Long userId = asUser.isAdmin() ? null : asUser.getUserId();
        return asMenuMapper.selectButtonListByUserId(userId).stream().distinct().collect(Collectors.toList());
    }
}
