package top.okya.component.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: maojiaqi
 * @Date: 2025/5/6 9:33
 * @describe: 下一个任务信息封装类
 */

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NextNode {
    private String nodeId;
    private String nodeName;
    private String pathType; // DIRECT（直接跳转）/ CONDITIONAL（条件分支）
    private String conditionExpression;
}
