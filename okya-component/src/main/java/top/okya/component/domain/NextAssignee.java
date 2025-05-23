package top.okya.component.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: maojiaqi
 * @Date: 2024/7/26 16:05
 * @describe: 流程流转历史类
 */

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NextAssignee {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

}
