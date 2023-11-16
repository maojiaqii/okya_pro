package top.okya.component.utils.ui;

import top.okya.component.domain.dto.AsMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: maojiaqi
 * @Date: 2023/9/20 16:13
 * @describe: 前端对象封装工具类
 */

public class UiUtil {

    // 动态菜单
    public static List<AsMenu> formatMenu(List<AsMenu> dataList, Long parentId) {
        if (dataList.isEmpty()) {
            return dataList;
        }
        if (Objects.equals(parentId, null)) {
            parentId = 0L;
        }
        List<AsMenu> newTreeList = new ArrayList<AsMenu>();
        for (AsMenu data : dataList) {
            if (Objects.equals(parentId, data.getParentId())) {
                data.setChildren(formatMenu(dataList, data.getMenuId()));
                newTreeList.add(data);
            }
        }
        return newTreeList;
    }
}
