package top.okya.system.domain;

import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户与岗位关联表(AsUserPost)实体类
 *
 * @author mjq
 * @since 2023-08-28 16:26:41
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsUserPost implements Serializable {
    private static final long serialVersionUID = -22836397926955696L;
    /**
    * 用户ID
    */
    private Long userId;
    /**
    * 岗位ID
    */
    private Long postId;

}
