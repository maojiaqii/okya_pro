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

    /**
     * 自动完成流程中的所有任务，直到流程结束
     *
     * @param processInstanceId 流程实例ID
     * @param variables 任务变量
     * @return 是否成功完成流程
     */
    boolean autoCompleteProcess(String processInstanceId, Map<String, Object> variables);
    
    /**
     * 获取流程实例的历史路径
     *
     * @param processInstanceId 流程实例ID
     * @return 流程历史路径信息列表
     */
    WorkflowHistory getProcessHistoryPath(String processInstanceId);

    /**
     * 获取流程实例的历史路径描述
     *
     * @param processInstanceId 流程实例ID
     * @return 流程历史路径描述
     */
    String getProcessHistoryPathDescription(String processInstanceId);
}