package top.okya.workflow.listener;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import top.okya.component.constants.FlowConstants;

import java.util.Objects;

/**
 * @author: maojiaqi
 * @Date: 2025/5/18 23:18
 * @describe: 用户任务创建监听
 */

public class TaskCreateListener implements TaskListener {
    
    @Override
    public void notify(DelegateTask execution) {
        if (TaskListener.EVENTNAME_CREATE.equals(execution.getEventName())) {
            execution.setVariable(FlowConstants.APPROVE_COUNT, 0);
            execution.setVariable(FlowConstants.REJECT_COUNT, 0);
            Object variable = execution.getVariable(FlowConstants.REJECT_TO);
            execution.setVariable(FlowConstants.REJECT_TO, null);
            if (Objects.nonNull(variable)) {
                execution.getProcessEngine()
                        .getRuntimeService()
                        .createProcessInstanceModification(execution.getProcessInstanceId())
                        .cancelAllForActivity(execution.getTaskDefinitionKey())
                        .startBeforeActivity(variable + (Objects.equals(FlowConstants.STARTER_NODE_ID, variable) ? "" : "#multiInstanceBody"))   // 启动目标节点
                        .execute();
            }
        }
    }
}
