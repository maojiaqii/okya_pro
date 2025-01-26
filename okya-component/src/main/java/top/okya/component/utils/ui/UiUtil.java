package top.okya.component.utils.ui;

import top.okya.component.domain.Permission;
import top.okya.component.domain.PermissionMeta;
import top.okya.component.domain.dto.AsPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: maojiaqi
 * @Date: 2023/9/20 16:13
 * @describe: 前端对象封装工具类
 */

public class UiUtil {

    /**
     * 动态菜单
     */
    public static List<Permission> formatPermissions(List<AsPermission> dataList, String parentId) {
        if (dataList.isEmpty()) {
            return null;
        }
        List<Permission> newTreeList = new ArrayList<>();
        for (AsPermission data : dataList) {
            if (Objects.equals(parentId, data.getParentId()) && Objects.equals("M", data.getPermissionType())) {
                PermissionMeta permissionMeta = new PermissionMeta()
                        .setActiveMenu(data.getActiveMenu())
                        .setHidden(data.getHidden() == 1)
                        .setAffix(data.getAffix() == 1)
                        .setIcon(data.getIcon())
                        .setBreadcrumb(data.getBreadcrumb() == 1)
                        .setCanTo(data.getCanTo() == 1)
                        .setAlwaysShow(data.getAlwaysShow() == 1)
                        .setNoCache(data.getNoCache() == 1)
                        .setTitle(data.getTitle())
                        .setNoTagsView(data.getNoTagsView() == 1)
                        .setOuterLink(data.getOuterLink())
                        .setParams(data.getParams());
                Permission permission = new Permission()
                        .setPath(data.getPath())
                        .setComponent(data.getComponent())
                        .setName(data.getName())
                        .setRedirect(data.getRedirect())
                        .setMeta(permissionMeta)
                        .setChildren(formatPermissions(dataList, data.getPermissionId()));
                newTreeList.add(permission);
            }
        }
        return newTreeList.isEmpty() ? null : newTreeList;
    }
}
