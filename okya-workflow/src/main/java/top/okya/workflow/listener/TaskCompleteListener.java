package top.okya.workflow.listener;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import top.okya.component.constants.FlowConstants;

import java.util.Objects;
import java.util.Optional;

/**
 * @author: maojiaqi
 * @Date: 2025/5/18 23:25
 * @describe: 用户任务完成监听
 */

public class TaskCompleteListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        if (TaskListener.EVENTNAME_COMPLETE.equals(delegateTask.getEventName())) {
            if (Objects.isNull(delegateTask.getVariable(FlowConstants.REJECT_TO))) {
                delegateTask.setVariable(FlowConstants.APPROVE_COUNT, ((Integer) Optional.ofNullable(delegateTask.getVariable(FlowConstants.APPROVE_COUNT)).orElse(0)) + 1);
            } else {
                delegateTask.setVariable(FlowConstants.REJECT_COUNT, ((Integer) Optional.ofNullable(delegateTask.getVariable(FlowConstants.REJECT_COUNT)).orElse(0)) + 1);
            }
        }
    }
}
