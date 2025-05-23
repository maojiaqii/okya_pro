package top.okya.workflow.listener;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import top.okya.component.constants.FlowConstants;

import java.util.Objects;

/**
 * @author: maojiaqi
 * @Date: 2025/5/18 23:18
 * @describe: 流程结束节点监听
 */

public class EndEventListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) {
        if (ExecutionListener.EVENTNAME_START.equals(execution.getEventName())) {
            execution.setVariable(FlowConstants.APPROVE_COUNT, 0);
            execution.setVariable(FlowConstants.REJECT_COUNT, 0);
            Object variable = execution.getVariable(FlowConstants.REJECT_TO);
            execution.setVariable(FlowConstants.REJECT_TO, null);
            if (Objects.nonNull(variable)) {
                execution.getProcessEngine()
                        .getRuntimeService()
                        .createProcessInstanceModification(execution.getProcessInstanceId())
                        .startBeforeActivity(variable + (Objects.equals(FlowConstants.STARTER_NODE_ID, variable) ? "" : "#multiInstanceBody"))   // 启动目标节点
                        .execute();
            }
        }
    }
}
