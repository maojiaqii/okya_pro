package top.okya.workflow.service.impl;

import com.alibaba.fastjson2.JSONArray;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.okya.component.domain.vo.WorkflowDesignVo;
import top.okya.component.enums.exception.FlowExceptionType;
import top.okya.component.exception.FlowException;
import top.okya.component.exception.check.CheckAndThrowExceptions;
import top.okya.component.utils.common.IdUtil;
import top.okya.workflow.dao.AsFlowMapper;
import top.okya.workflow.domain.AsFlow;
import top.okya.workflow.service.FlowDesignService;
import top.okya.workflow.util.BpmnConverter;

import java.util.List;
import java.util.Optional;

/**
 * @author: maojiaqi
 * @Date: 2025/4/14 23:16
 * @describe:
 */

@Service
public class FlowDesignServiceImpl implements FlowDesignService {

    @Autowired
    AsFlowMapper asFlowMapper;
    @Autowired
    CheckAndThrowExceptions checkAndThrowExceptions;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;

    @Override
    public List<AsFlow> getFlowList() {
        return asFlowMapper.queryLasted(null);
    }

    @Override
    public AsFlow getWorkflow(String flowId) {
        return asFlowMapper.queryFlowById(flowId);
    }

    @Override
    @Transactional
    public String saveWorkflow(WorkflowDesignVo workflowVo) {
        if(workflowVo.getIsNew()){
            // 保存为新版本
            // 获取当前版本号
            Integer currentVersion = asFlowMapper.queryLastedVerByCode(workflowVo.getFlowCode());

            // 创建新的流程记录
            AsFlow asFlow = (AsFlow) new AsFlow()
                    .setFlowId(IdUtil.randomUUID())
                    .setFlowName(workflowVo.getFlowName())
                    .setFlowCode(workflowVo.getFlowCode())
                    .setFlowNodes(new JSONArray(workflowVo.getNodes()))
                    .setFlowVersion(Optional.ofNullable(currentVersion).orElse(0) + 1)
                    .setStatus(workflowVo.getStatus())
                    .setRemark(workflowVo.getRemark());
            try {
                // 保存新流程
                asFlowMapper.insert(asFlow);
            } catch (Exception e) {
                throw new FlowException(FlowExceptionType.FLOW_DESIGN_ERROR, "当前流程已有新的版本，更新版本失败");
            }
        } else {
            throw new FlowException(FlowExceptionType.FLOW_DESIGN_ERROR, "不允许修改流程");
            /*String flowId = workflowVo.getFlowId();
            if(StringUtils.isEmpty(flowId)){
                throw new FlowException(FlowExceptionType.FLOW_DESIGN_ERROR, "更新流程时流程Id不能为空");
            }
            AsFlow asFlow = (AsFlow) new AsFlow()
                    .setFlowId(flowId)
                    .setFlowName(workflowVo.getFlowName())
                    .setFlowNodes(new JSONArray(workflowVo.getNodes()))
                    .setStatus(workflowVo.getStatus())
                    .setRemark(workflowVo.getRemark());
            asFlowMapper.update(asFlow);*/
        }
        return "保存成功！";
    }

    @Override
    @Transactional
    public String publishWorkflow(WorkflowDesignVo workflowVo) {
        // 查询流程定义
        AsFlow asFlow = asFlowMapper.queryFlowById(workflowVo.getFlowId());
        checkAndThrowExceptions.checkDbResult(asFlow);
        String flowCode = asFlow.getFlowCode();

        try{
            // 转换为BPMN模型
            BpmnModelInstance bpmnModelInstance = BpmnConverter.convertToBpmn(asFlow);

            // 检查是否存在已部署的流程定义
            List<ProcessDefinition> existingDefinitions = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(flowCode)
                    .active()
                    .list();

            // 如果有已部署的流程，检查是否有在途流程
            if (!existingDefinitions.isEmpty()) {
                for (ProcessDefinition definition : existingDefinitions) {
                    // 查询该流程定义的运行实例
                    List<ProcessInstance> runningInstances = runtimeService.createProcessInstanceQuery()
                            .processDefinitionId(definition.getId())
                            .active()
                            .list();

                    if (runningInstances.isEmpty()) {
                        // 如果没有在途流程，停用旧版本
                        repositoryService.suspendProcessDefinitionById(definition.getId());
                    }
                }
            }

            // 部署新版本流程
            Deployment deployment = repositoryService.createDeployment()
                    .name(asFlow.getFlowName())
                    .addModelInstance(flowCode + ".bpmn", bpmnModelInstance)
                    .deploy();

            // 更新流程状态
            asFlowMapper.unPublished(flowCode, asFlow.getFlowId());
            asFlowMapper.published(asFlow.getFlowId(), deployment.getId());

            return "流程发布成功！部署ID：" + deployment.getId();
        } catch (Exception e) {
            throw new FlowException(FlowExceptionType.FLOW_PUBLISH_ERROR, e.getMessage());
        }
    }

}
