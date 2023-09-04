package top.okya.system.domain;

import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 角色和部门关联表(AsRoleDept)实体类
 *
 * @author mjq
 * @since 2023-08-28 16:26:41
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsRoleDept implements Serializable {
    private static final long serialVersionUID = 278254140723713594L;
    /**
    * 角色ID
    */
    private Long roleId;
    /**
    * 部门ID
    */
    private Long deptId;

}
