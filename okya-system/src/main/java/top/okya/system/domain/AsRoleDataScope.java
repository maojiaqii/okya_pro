package top.okya.system.domain;

import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * (AsRoleDataScope)实体类
 *
 * @author mjq
 * @since 2023-08-28 17:42:25
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsRoleDataScope implements Serializable {
    private static final long serialVersionUID = -13825773900375609L;
    /**
    * 角色id
    */
    private Long roleId;
    /**
    * 数据权限sql
    */
    private String sqlText;

}
