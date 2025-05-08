package top.okya.workflow.service;

import top.okya.component.domain.WorkflowHistory;
import top.okya.component.domain.vo.WorkflowProcessVo;

import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2025/4/17 22:25
 * @describe: 流程实例相关服务接口
 */
public interface FlowHistoryService {

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