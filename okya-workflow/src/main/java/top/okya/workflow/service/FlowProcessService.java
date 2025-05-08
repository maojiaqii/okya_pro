package top.okya.workflow.service;

import top.okya.component.domain.WorkflowHistory;
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
     * 启动流程实例
     *
     * @param workflowStartVo 流程启动参数
     * @return 流程实例ID
     */
    String startProcess(WorkflowProcessVo workflowStartVo);

    /**
     * 完成任务
     *
     * @param taskId 任务ID
     * @param variables 任务变量
     */
    void completeTask(String taskId, Map<String, Object> variables);

    /**
     * 取消流程实例
     *
     * @param processInstanceId 流程实例ID
     * @param reason 取消原因
     */
    void cancelProcess(String processInstanceId, String reason);
}