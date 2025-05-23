package top.okya.component.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

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
public class WorkflowSelect {
    private String processInstanceId;
    private String taskId;
    private boolean lastNode;
    private List<NextAssignee> nextAssignees;
    private List<PreNode> preNodes;
}
