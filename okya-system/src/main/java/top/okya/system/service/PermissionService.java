package top.okya.system.service;

import top.okya.component.domain.dto.AsMenu;

import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2023/9/20 15:03
 * @describe: 权限相关service
 */
public interface PermissionService {
    /**
     * 我的菜单
     *
     * @return 菜单集合
     */
    List<AsMenu> myMenus();

    /**
     * 我的按钮
     *
     * @return 按钮集合
     */
    List<AsMenu> myButtons();
}
