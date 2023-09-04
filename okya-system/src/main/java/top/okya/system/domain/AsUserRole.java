package top.okya.system.domain;

import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户和角色关联表(AsUserRole)实体类
 *
 * @author mjq
 * @since 2023-08-28 16:26:41
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsUserRole implements Serializable {
    private static final long serialVersionUID = 494355610774647940L;
    /**
    * 用户ID
    */
    private Long userId;
    /**
    * 角色ID
    */
    private Long roleId;

}
