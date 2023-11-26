package top.okya.system.domain;

import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 角色和菜单关联表(AsRoleMenu)实体类
 *
 * @author mjq
 * @since 2023-08-28 16:26:40
 */

public class AsRoleMenu implements Serializable {
    private static final long serialVersionUID = -27182953334398268L;
    /**
    * 角色ID
    */
    private Long roleId;
    /**
    * 菜单ID
    */
    private Long menuId;

}
