package top.okya.workflow.service;

import top.okya.component.domain.vo.WorkflowDesignVo;
import top.okya.workflow.domain.AsFlow;

import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2025/4/14 23:05
 * @describe: 流程设计相关service
 */
public interface FlowDesignService {

    List<AsFlow> getFlowList();

    String saveWorkflow(WorkflowDesignVo workflowVo);

    String publishWorkflow(WorkflowDesignVo workflowVo);

    AsFlow getWorkflow(String flowId);
}
