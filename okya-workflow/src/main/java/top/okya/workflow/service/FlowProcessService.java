package top.okya.workflow.service;

import top.okya.component.domain.PreNode;
import top.okya.component.domain.WorkflowSelect;
import top.okya.component.domain.vo.WorkflowProcessVo;

import java.util.List;
import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2025/4/17 22:25
 * @describe: 流程实例相关服务接口
 */
public interface FlowProcessService {

    /**
     * 测试流程
     *
     * @param workflowStartVo 流程启动参数
     * @return 测试结果
     */
    Map<String, Object> testProcess(WorkflowProcessVo workflowStartVo);

    /**
     * 完成任务
     *
     * @param workflowStartVo 流程启动参数
     */
    void completeTask(WorkflowProcessVo workflowStartVo);

    /**
     * 取消流程实例
     *
     * @param processInstanceId 流程实例ID
     * @param reason 取消原因
     */
    void cancelProcess(String processInstanceId, String reason);

    WorkflowSelect nextAssignees(WorkflowProcessVo workflowStartVo);

    WorkflowSelect preNodes(WorkflowProcessVo workflowStartVo);
}