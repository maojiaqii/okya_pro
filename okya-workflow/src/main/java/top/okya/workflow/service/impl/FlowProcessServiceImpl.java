package top.okya.workflow.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.okya.component.constants.CharacterConstants;
import top.okya.component.constants.FlowConstants;
import top.okya.component.domain.NextAssignee;
import top.okya.component.domain.NextNode;
import top.okya.component.domain.PreNode;
import top.okya.component.domain.WorkflowSelect;
import top.okya.component.domain.dto.AsUser;
import top.okya.component.domain.vo.WorkflowProcessVo;
import top.okya.component.enums.exception.FlowExceptionType;
import top.okya.component.exception.FlowException;
import top.okya.component.global.Global;
import top.okya.component.utils.common.DateFormatUtil;
import top.okya.component.utils.common.IdUtil;
import top.okya.workflow.dao.AsFlowBusinessInfoMapper;
import top.okya.workflow.dao.AsFlowMapper;
import top.okya.workflow.domain.AsFlow;
import top.okya.workflow.domain.AsFlowBusinessInfo;
import top.okya.workflow.service.FlowProcessService;
import top.okya.workflow.util.CamundaProcessPredictor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: maojiaqi
 * @Date: 2025/4/17 22:30
 * @describe: 流程实例相关服务
 */
@Service
@Slf4j
public class FlowProcessServiceImpl implements FlowProcessService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    HistoryService historyService;
    @Autowired
    CamundaProcessPredictor camundaProcessPredictor;
    @Autowired
    AsFlowMapper asFlowMapper;
    @Autowired
    AsFlowBusinessInfoMapper asFlowBusinessInfoMapper;

    @Override
    public Map<String, Object> testProcess(WorkflowProcessVo workflowStartVo) {
        Map<String, Object> variables = generateVariables(workflowStartVo);
        // 启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                workflowStartVo.getFlowCode(),
                workflowStartVo.getFlowBusinessKey(),
                variables
        );
        String processInstanceId = processInstance.getId();
        log.info("流程启动成功，流程实例Id：{}", processInstanceId);

        // 自动完成流程
        boolean completed = autoCompleteProcess(processInstanceId);
        return ImmutableMap.of("completed", completed, "processInstanceId", processInstanceId);
    }

    @Override
    public void completeTask(WorkflowProcessVo workflowStartVo) {
        String checkType = workflowStartVo.getCheckType();
        String taskId = workflowStartVo.getTaskId();
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        if (Objects.isNull(task)) {
            log.error("无法找到流程任务：{}", taskId);
            throw new FlowException(FlowExceptionType.FLOW_TASK_NOT_FOUND, taskId);
        }
        String processInstanceId = task.getProcessInstanceId();
        JSONObject variables = Optional.ofNullable(workflowStartVo.getFormData()).orElse(new JSONObject());
        String comment = (Objects.equals(checkType, "1") ? "通过#" : "退回#")
                + Optional.ofNullable(workflowStartVo.getComment()).orElse(Objects.equals(checkType, "1") ? FlowConstants.APPROVE : FlowConstants.REJECT);
        if (Objects.equals(checkType, "1")) {
            List<NextNode> nextNodes = getNextNode(task, variables);
            // 下一节点审批人变量
            if (!nextNodes.isEmpty()) {
                NextNode nextNode = nextNodes.get(0);
                variables.put(nextNode.getNodeId(), workflowStartVo.getCheckers());
            }
            variables.put(FlowConstants.REJECT_TO, null);
        } else if (Objects.equals(checkType, "0")) {
            String backNode = workflowStartVo.getBackNode();
            if (StringUtils.isBlank(backNode)) {
                log.error("退回时任务节点Id为空");
                throw new FlowException(FlowExceptionType.FLOW_RUN_ERROR, "退回时任务节点Id为空");
            }
            String[] split = backNode.split(CharacterConstants.AT);
            variables.put(FlowConstants.REJECT_TO, split[1]);
        }
        taskService.createComment(taskId, processInstanceId, comment);
        taskService.complete(taskId, variables);
        List<Task> newTasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
        // 更新业务流程对应表
        if (newTasks == null || newTasks.isEmpty()) {
            asFlowBusinessInfoMapper.finish(processInstanceId, DateFormatUtil.formatNow());
        } else {
            Task newTask = newTasks.get(0);
            String taskDefinitionKey = newTask.getTaskDefinitionKey();
            // 是否到下一岗
            if (!Objects.equals(task.getTaskDefinitionKey(), taskDefinitionKey)) {
                // 分组id
                String id = IdUtil.randomUUID();
                for (Task taskOne : newTasks) {
                    taskOne.setOwner(id);
                    // 设置任务描述（任务产生方式：被退回产生或者正常通过产生）
                    taskOne.setDescription(Objects.equals(checkType, "1") ? FlowConstants.FROM_APPROVE : FlowConstants.FROM_REJECT);
                    // 保存任务修改
                    taskService.saveTask(taskOne);
                }
                asFlowBusinessInfoMapper.updateNodeInfo(processInstanceId, taskDefinitionKey, newTask.getName());
            }
        }
    }

    @Override
    @Transactional
    public void cancelProcess(String processInstanceId, String reason) {
        // 取消流程实例
        runtimeService.deleteProcessInstance(processInstanceId, reason);
        asFlowBusinessInfoMapper.deleteByProcessInstanceId(processInstanceId);
    }

    @Override
    public WorkflowSelect nextAssignees(WorkflowProcessVo workflowStartVo) {
        Map<String, Object> variables = generateVariables(workflowStartVo);
        try {
            Task task = null;
            String taskId = workflowStartVo.getTaskId();
            if (StringUtils.isBlank(taskId)) {
                String flowBusinessKey = workflowStartVo.getFlowBusinessKey();
                String flowCode = workflowStartVo.getFlowCode();

                // 启动流程实例
                ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                        flowCode,
                        flowBusinessKey,
                        variables
                );

                String processInstanceId = processInstance.getId();

                // 从流程定义ID获取部署ID
                String deploymentId = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(processInstance.getProcessDefinitionId())
                        .singleResult()
                        .getDeploymentId();

                // 入记录表
                AsFlow asFlow = asFlowMapper.queryFlowByDeploymentId(deploymentId);
                if (Objects.isNull(asFlow)) {
                    throw new FlowException(FlowExceptionType.FLOW_TEMPLATE_NOT_FOUND, deploymentId);
                }
                AsUser asUser = Objects.requireNonNull(Global.getLoginUser()).getAsUser();
                AsFlowBusinessInfo asFlowBusinessInfo = new AsFlowBusinessInfo()
                        .setInfoId(IdUtil.randomUUID())
                        .setBusinessTable(flowBusinessKey.split("#")[0])
                        .setBusinessId(flowBusinessKey.split("#")[1])
                        .setFlowCode(flowCode)
                        .setFlowStartTime(new Date())
                        .setFlowBusinessKey(flowBusinessKey)
                        .setFlowVersion(asFlow.getFlowVersion())
                        .setFlowCurrentNodeId(FlowConstants.STARTER_NODE_ID)
                        .setFlowCurrentNodeName("发起人")
                        .setStartDeptId(asUser.getDeptId())
                        .setStartUserId(asUser.getUserId())
                        .setProcInstId(processInstanceId);
                asFlowBusinessInfoMapper.insert(asFlowBusinessInfo);

                // 查询第一个任务
                task = taskService.createTaskQuery()
                        .processInstanceId(processInstanceId)
                        .orderByTaskCreateTime().asc()
                        .list()
                        .get(0);

                task.setDescription(FlowConstants.FROM_APPROVE);
                task.setOwner(IdUtil.randomUUID());
                taskService.saveTask(task);
            } else {
                task = taskService.createTaskQuery()
                        .taskId(taskId)
                        .singleResult();
            }
            if (Objects.isNull(task)) {
                log.error("无法找到流程任务：{}", taskId);
                throw new FlowException(FlowExceptionType.FLOW_TASK_NOT_FOUND, taskId);
            }
            List<NextNode> nextNodes = getNextNode(task, variables);
            if (nextNodes.isEmpty()) {
                return new WorkflowSelect(task.getProcessInstanceId(), task.getId(), true, null, null);
            }
            return new WorkflowSelect(task.getProcessInstanceId(), task.getId(), false, getAssignee(task.getProcessInstanceId(), nextNodes.get(0)), null);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
            throw new FlowException(FlowExceptionType.FLOW_START_ERROR, e.getMessage());
        }
    }

    @Override
    public WorkflowSelect preNodes(WorkflowProcessVo workflowStartVo) {
        // 查询流程的历史活动实例，按开始时间排序,只有任务节点
        String processInstanceId = workflowStartVo.getProcessInstanceId();
        String taskId = workflowStartVo.getTaskId();
        List<HistoricTaskInstance> activityInstances = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list()
                .stream()
                .filter(e -> !Objects.equals(e.getDeleteReason(), "deleted"))
                .collect(Collectors.toList());

        if (activityInstances.isEmpty()) {
            log.warn("流程实例不存在或没有历史记录，流程实例ID: {}", processInstanceId);
            return new WorkflowSelect(processInstanceId, taskId, false, null, null);
        }

        List<String> path = new ArrayList<>();
        List<PreNode> preNodes = new ArrayList<>();

        for (HistoricTaskInstance task : activityInstances) {
            String key = task.getTaskDefinitionKey();
            String name = task.getName();
            int lastIndex = path.lastIndexOf(key);

            if (lastIndex != -1) {
                // 截断路径到该键最后一次出现的位置（包括该位置）
                path = new ArrayList<>(path.subList(0, lastIndex + 1));
                preNodes = new ArrayList<>(preNodes.subList(0, lastIndex + 1));
            } else {
                path.add(key);
                preNodes.add(new PreNode(name + CharacterConstants.AT + key));
            }
        }
        if (!preNodes.isEmpty()) {
            preNodes.remove(preNodes.size() - 1);
        }
        return new WorkflowSelect(processInstanceId, taskId, false, null, preNodes);
    }

    private List<NextNode> getNextNode(Task task, Map<String, Object> variables) {
        if (Objects.isNull(task.getAssignee())) {
            task.setAssignee(Objects.requireNonNull(Global.getLoginUser()).getAsUser().getUserId());
        }
        // 预判下一岗节点
        List<NextNode> nextTaskInfos = camundaProcessPredictor.predictNextUserTasksByTask(task.getId(), variables);
        if (nextTaskInfos.size() > 1) {
            log.error("下一节点过多：{}", JSONObject.toJSONString(nextTaskInfos));
            throw new FlowException(FlowExceptionType.FLOW_TOO_MANY_NODES, task.getId());
        }
        return nextTaskInfos;
    }

    private boolean autoCompleteProcess(String processInstanceId) {
        try {
            // 检查流程实例是否存在
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .active()
                    .singleResult();

            if (processInstance == null) {
                log.error("流程实例不存在或已结束，流程实例Id：{}", processInstanceId);
                return false;
            }

            log.info("开始自动完成流程，流程实例Id：{}", processInstanceId);

            // 最大循环次数，防止无限循环
            int maxLoops = 300;
            int loopCount = 0;

            while (loopCount < maxLoops) {
                // 查询当前流程实例的任务列表
                List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();

                // 如果任务列表为空，表示流程已经结束，跳出循环
                if (tasks == null || tasks.isEmpty()) {
                    log.info("流程已结束，流程实例Id：{}", processInstanceId);
                    return true;
                }

                log.info("完成任务：{}，任务ID：{}", tasks.get(0).getName(), tasks.get(0).getId());
                taskService.complete(tasks.get(0).getId(), null);

                loopCount++;

                // 添加短暂休眠，避免CPU占用过高
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("线程被中断", e);
                    return false;
                }
            }

            // 如果达到最大循环次数还未结束，记录警告
            log.error("流程未在预期内结束，可能存在死循环，流程实例Id：{}", processInstanceId);
            return false;
        } catch (Exception e) {
            log.error("自动完成流程失败，流程实例Id：{}，错误：{}", processInstanceId, e.getMessage(), e);
            throw new FlowException(FlowExceptionType.FLOW_RUN_ERROR, e.getMessage());
        }
    }

    private Map<String, Object> generateVariables(WorkflowProcessVo workflowStartVo) {
        Map<String, Object> variables = getCamundaPropertiesByFlowCode(workflowStartVo.getFlowCode());
        variables.putAll(workflowStartVo.getFormData());
        // 添加其他必要变量
        AsUser loginUser = Objects.requireNonNull(Global.getLoginUser()).getAsUser();
        variables.put("startUserId", loginUser.getUserId());
        variables.put("startUserName", loginUser.getUserName());
        variables.put("startUserDept", loginUser.getDeptId());
        variables.put("flowBusinessKey", workflowStartVo.getFlowBusinessKey());
        variables.put(FlowConstants.APPROVE_COUNT, 0);
        variables.put(FlowConstants.REJECT_COUNT, 0);
        return variables;
    }

    private List<NextAssignee> getAssignee(String processInstanceId, NextNode nextTaskInfo) {
        List<NextAssignee> assignees = new ArrayList<>();
        List<Map<String, Object>> maps = asFlowBusinessInfoMapper.queryByProcInstId(processInstanceId);
        if (maps == null || maps.isEmpty()) {
            log.error("未获取到流程执行信息：{}", processInstanceId);
            throw new FlowException(FlowExceptionType.FLOW_BUSINESS_INFO_NOT_FOUND, processInstanceId);
        }
        Map<String, Object> stringObjectMap = maps.get(0);
        JSONArray flowNodes = JSON.parseArray(stringObjectMap.get("flow_nodes").toString());
        for (int i = 0; i < flowNodes.size(); i++) {
            JSONObject flowNode = flowNodes.getJSONObject(i);
            // 只处理条件节点(nodeType = 4)
            if (Objects.equals(flowNode.getString(FlowConstants.NODE_ID), nextTaskInfo.getNodeId())) {
                JSONArray jsonArray = flowNode.getJSONArray(FlowConstants.NODE_APPROVE_LIST);
                if (jsonArray == null || jsonArray.isEmpty()) {
                    return assignees;
                }
                Integer type = jsonArray.getJSONObject(0).getInteger("type");
                // 1: 指定用户 3：指定角色 5：发起人 7：自定义sql
                switch (type) {
                    case 1:
                        jsonArray.forEach(item -> {
                            if (item instanceof JSONObject) {
                                JSONObject jsonObject = (JSONObject) item;
                                NextAssignee nextAssignee = new NextAssignee(jsonObject.getString("targetId"), jsonObject.getString("name") + CharacterConstants.AT + jsonObject.getString("code"));
                                assignees.add(nextAssignee);
                            }
                        });
                        break;
                    case 3:
                        Integer roleRange = flowNode.getInteger(FlowConstants.ROLE_RANGE);
                        List<String> targetIds = jsonArray.stream()
                                .filter(item -> item instanceof JSONObject)
                                .map(item -> {
                                    JSONObject jsonObject = (JSONObject) item;
                                    return jsonObject.getString("targetId");
                                })
                                .collect(Collectors.toList());
                        assignees.addAll(asFlowMapper.getAssigneesByRole(targetIds, roleRange, stringObjectMap.get("start_dept_id")));
                        break;
                    case 5:
                        assignees.add(new NextAssignee(stringObjectMap.get("start_user_id").toString(), stringObjectMap.get("user_name").toString()));
                        break;
                    case 7:
                        break;
                }
                break;
            }
        }
        return assignees;
    }

    /**
     * 在启动流程实例之前，根据flowCode获取流程模型中的CamundaProperty
     *
     * @param flowCode 流程代码
     * @return CamundaProperty的Map，key为属性名，value为属性值
     */
    private Map<String, Object> getCamundaPropertiesByFlowCode(String flowCode) {
        Map<String, Object> propertyMap = new HashMap<>();

        try {
            // 根据flowCode获取最新的流程定义
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(flowCode)
                    .latestVersion()
                    .singleResult();

            if (processDefinition == null) {
                log.error("未找到流程定义，flowCode: {}", flowCode);
                return propertyMap;
            }

            // 获取BPMN模型实例
            BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinition.getId());

            // 获取流程定义
            Process process = modelInstance.getModelElementsByType(Process.class)
                    .stream()
                    .filter(p -> p.getId().equals(flowCode))
                    .findFirst()
                    .orElse(null);

            if (process == null) {
                log.error("在BPMN模型中未找到流程定义，flowCode: {}", flowCode);
                return propertyMap;
            }

            // 获取扩展元素
            ExtensionElements extensionElements = process.getExtensionElements();
            if (extensionElements == null) {
                return propertyMap;
            }

            // 获取Camunda属性集合
            Collection<CamundaProperties> camundaPropertiesCollection = extensionElements.getChildElementsByType(CamundaProperties.class);
            if (camundaPropertiesCollection == null || camundaPropertiesCollection.isEmpty()) {
                return propertyMap;
            }

            // 遍历所有CamundaProperties
            for (CamundaProperties camundaProperties : camundaPropertiesCollection) {
                // 获取所有的CamundaProperty
                Collection<CamundaProperty> properties = camundaProperties.getCamundaProperties();
                if (properties != null) {
                    // 将CamundaProperty转换为Map
                    for (CamundaProperty property : properties) {
                        String camundaValue = property.getCamundaValue();
                        if (StringUtils.isNotBlank(camundaValue) && camundaValue.startsWith(CharacterConstants.BRACKETS_LEFT) && camundaValue.endsWith(CharacterConstants.BRACKETS_RIGHT)) {
                            propertyMap.put(property.getCamundaName(), JSON.parseArray(camundaValue));
                        } else {
                            propertyMap.put(property.getCamundaName(), camundaValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取流程模型CamundaProperty时发生错误，flowCode: {}, error: {}", flowCode, e.getMessage(), e);
        }
        return propertyMap;
    }
}
 