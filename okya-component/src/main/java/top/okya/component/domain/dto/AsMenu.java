package top.okya.component.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 菜单权限表(AsMenu)实体类
 *
 * @author mjq
 * @since 2023-08-28 16:26:40
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsMenu implements Serializable {
    private static final long serialVersionUID = -35642748054532389L;
    /**
    * 菜单ID
    */
    private Long menuId;
    /**
    * 菜单名称
    */
    private String menuName;
    /**
     * 菜单名称
     */
    private String menuNameEn;
    /**
    * 父菜单ID
    */
    private Long parentId;
    /**
    * 显示顺序
    */
    private Integer orderNum;
    /**
    * 组件路径
    */
    private String component;
    /**
     * 路由
     */
    private String path;
    /**
    * 路由参数
    */
    private Object query;
    /**
    * 是否为外链（0是 1否）
    */
    private Integer isFrame;
    /**
    * 菜单类型（C目录 M菜单 B按钮）
    */
    private String menuType;
    /**
    * 菜单状态（0正常 1停用 2删除）
    */
    private String status;
    /**
    * 权限标识
    */
    private String perms;
    /**
    * 菜单图标
    */
    private String icon;
    /**
    * 创建者
    */
    private String createBy;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 更新者
    */
    private String updateBy;
    /**
    * 更新时间
    */
    private Date updateTime;
    /**
    * 备注
    */
    private String remark;

    private List<AsMenu> children;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AsMenu asMenu = (AsMenu) o;
        return Objects.equals(menuId, asMenu.menuId) && Objects.equals(menuName, asMenu.menuName) && Objects.equals(menuNameEn, asMenu.menuNameEn) && Objects.equals(parentId, asMenu.parentId) && Objects.equals(orderNum, asMenu.orderNum) && Objects.equals(component, asMenu.component) && Objects.equals(query, asMenu.query) && Objects.equals(isFrame, asMenu.isFrame) && Objects.equals(menuType, asMenu.menuType) && Objects.equals(status, asMenu.status) && Objects.equals(perms, asMenu.perms) && Objects.equals(icon, asMenu.icon) && Objects.equals(createBy, asMenu.createBy) && Objects.equals(createTime, asMenu.createTime) && Objects.equals(updateBy, asMenu.updateBy) && Objects.equals(updateTime, asMenu.updateTime) && Objects.equals(remark, asMenu.remark) && Objects.equals(children, asMenu.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuId, menuName, menuNameEn, parentId, orderNum, component, query, isFrame, menuType, status, perms, icon, createBy, createTime, updateBy, updateTime, remark, children);
    }
}
