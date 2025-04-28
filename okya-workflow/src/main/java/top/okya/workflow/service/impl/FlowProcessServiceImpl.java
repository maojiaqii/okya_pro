package top.okya.workflow.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.okya.component.domain.WorkflowHistory;
import top.okya.component.domain.child.NodeExecuteInfo;
import top.okya.component.domain.dto.AsUser;
import top.okya.component.domain.vo.WorkflowProcessVo;
import top.okya.component.enums.exception.FlowExceptionType;
import top.okya.component.exception.FlowException;
import top.okya.component.global.Global;
import top.okya.component.utils.common.DateFormatUtil;
import top.okya.component.utils.common.QLExpressUtil;
import top.okya.workflow.dao.AsFlowMapper;
import top.okya.workflow.domain.AsFlow;
import top.okya.workflow.service.FlowProcessService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

/**
 * @author: maojiaqi
 * @Date: 2025/4/17 22:30
 * @describe: 流程实例相关服务
 */
@Service
@Slf4j
public class FlowProcessServiceImpl implements FlowProcessService {

    private static final String END_EVENT_ID = "endEventIdm";
    private static final String NODE_TYPE = "nodeType";
    private static final String CONDITIONS = "conditions";
    private static final String NODE_TO = "nodeTo";
    private static final String NODE_ID = "nodeId";

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AsFlowMapper asFlowMapper;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public Map<String, Object> testProcess(WorkflowProcessVo workflowStartVo) {
        Map<String, Object> variables = generateVariables(workflowStartVo);
        // 启动流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                workflowStartVo.getFlowCode(),
                workflowStartVo.getBusinessKey(),
                variables
        );

        String processInstanceId = processInstance.getId();
        log.info("流程启动成功，流程实例Id：{}", processInstanceId);

        // 自动完成流程
        boolean completed = autoCompleteProcess(processInstanceId, null);
        return ImmutableMap.of("completed", completed, "processInstanceId", processInstanceId);
    }

    @Override
    @Transactional
    public String startProcess(WorkflowProcessVo workflowStartVo) {

        Map<String, Object> variables = generateVariables(workflowStartVo);

        try {
            // 启动流程实例
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                    workflowStartVo.getFlowCode(),
                    workflowStartVo.getBusinessKey(),
                    variables
            );

            return processInstance.getId();
        } catch (Exception e) {
            throw new FlowException(FlowExceptionType.FLOW_START_ERROR, e.getMessage());
        }
    }

    @Override
    public void completeTask(String taskId, Map<String, Object> variables) {
        // 完成任务
        taskService.complete(taskId, variables);
    }

    @Override
    public void cancelProcess(String processInstanceId, String reason) {
        // 取消流程实例
        runtimeService.deleteProcessInstance(processInstanceId, reason);
    }

    @Override
    public boolean autoCompleteProcess(String processInstanceId, Map<String, Object> variables) {
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
            int maxLoops = 100;
            int loopCount = 0;

            while (loopCount < maxLoops) {
                // 查询当前流程实例的任务列表
                List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();

                // 如果任务列表为空，表示流程已经结束，跳出循环
                if (tasks == null || tasks.isEmpty()) {
                    log.info("流程已结束，流程实例Id：{}", processInstanceId);
                    return true;
                }

                // 循环完成所有当前任务
                for (Task task : tasks) {
                    log.info("完成任务：{}，任务ID：{}", task.getName(), task.getId());
                    taskService.complete(task.getId(), variables);
                }

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
            return false;
        }
    }

    @Override
    public WorkflowHistory getProcessHistoryPath(String processInstanceId) {
        // 直接查询获取部署ID
        String processDefinitionId = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult()
                .getProcessDefinitionId(); // 获取流程定义ID

        // 从流程定义ID获取部署ID
        String deploymentId = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult()
                .getDeploymentId();

        AsFlow asFlow = asFlowMapper.queryFlowByDeploymentId(deploymentId);
        if (Objects.isNull(asFlow)) {
            throw new FlowException(FlowExceptionType.FLOW_TEMPLATE_NOT_FOUND);
        }

        // 查询流程的历史活动实例，按开始时间排序,只有任务节点
        List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();

        if (activityInstances == null || activityInstances.isEmpty()) {
            log.warn("流程实例不存在或没有历史记录，流程实例ID: {}", processInstanceId);
            return null;
        }

        Map<String, NodeExecuteInfo> nodeExecuteInfos = new HashMap<>();
        List<String> nodeIds = new ArrayList<>();

        for (HistoricActivityInstance instance : activityInstances) {
            String activityId = instance.getActivityId();
            // 添加到已执行节点ID列表
            nodeIds.add(activityId);

            // 基本信息
            NodeExecuteInfo runInfo = new NodeExecuteInfo().setActivityName(instance.getActivityName())
                    .setActivityType(instance.getActivityType())
                    .setStartTime(DateFormatUtil.formatDate(instance.getStartTime()))
                    .setEndTime(DateFormatUtil.formatDate(instance.getEndTime()))
                    .setDurationInMin(instance.getDurationInMillis() / 1000 / 60)
                    .setAssignee(instance.getAssignee());

            nodeExecuteInfos.put(activityId, runInfo);
        }

        // 获取流程历史变量
        Map<String, Object> historicVariables = new HashMap<>();
        historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .list()
                .forEach(variable -> historicVariables.put(variable.getName(), variable.getValue()));

        JSONArray flowNodes = asFlow.getFlowNodes();
        for (int i = 0; i < flowNodes.size(); i++) {
            JSONObject flowNode = flowNodes.getJSONObject(i);
            // 只处理条件节点(nodeType = 3)
            if (Objects.equals(flowNode.getInteger(NODE_TYPE), 3)) {
                // 获取该条件节点指向的目标节点ID
                JSONArray nodeTo = flowNode.getJSONArray(NODE_TO);
                if (nodeTo != null && !nodeTo.isEmpty() && nodeIds.contains(nodeTo.getString(0))) {
                    // 获取条件表达式
                    String conditionExpression = flowNode.getString(CONDITIONS);
                    boolean conditionMet = false;

                    // 评估条件表达式
                    if (StringUtils.isNotBlank(conditionExpression)) {
                        conditionMet = evaluateCondition(conditionExpression, historicVariables);
                    }
                    flowNode.put("executed", conditionMet);
                } else {
                    flowNode.put("executed", false);
                }
            } else {
                String flowId = flowNode.getString(NODE_ID);
                if(nodeExecuteInfos.containsKey(flowId)){
                    flowNode.put("executedInfo", nodeExecuteInfos.get(flowId));
                }
                flowNode.put("executed", nodeIds.contains(flowId));
            }
        }
        return new WorkflowHistory().setFlowNodes(flowNodes).setFinished(nodeIds.contains(END_EVENT_ID));
    }

    @Override
    public String getProcessHistoryPathDescription(String processInstanceId) {
        return "";
        /*List<Map<String, Object>> historyPath = getProcessHistoryPath(processInstanceId);

        if (historyPath.isEmpty()) {
            return "该流程实例没有历史记录";
        }

        StringBuilder description = new StringBuilder();
        description.append("流程实例 ").append(processInstanceId).append(" 的执行路径：\n");

        int step = 1;
        for (Map<String, Object> node : historyPath) {
            String activityName = (String) node.get("activityName");
            String activityType = (String) node.get("activityType");
            String activityId = (String) node.get("activityId");

            // 格式化时间
            java.util.Date startTime = (java.util.Date) node.get("startTime");
            java.util.Date endTime = (java.util.Date) node.get("endTime");
            String timeInfo = "";
            if (startTime != null) {
                timeInfo = " (开始时间: " + formatDate(startTime);
                if (endTime != null) {
                    timeInfo += ", 结束时间: " + formatDate(endTime);

                    // 计算持续时间
                    Long duration = (Long) node.get("durationInMillis");
                    if (duration != null) {
                        timeInfo += ", 耗时: " + formatDuration(duration);
                    }
                }
                timeInfo += ")";
            }

            // 处理不同类型的节点
            String nodeInfo;
            if ("startEvent".equals(activityType)) {
                nodeInfo = "流程开始: " + activityName;
            } else if ("endEvent".equals(activityType)) {
                nodeInfo = "流程结束: " + activityName;
            } else if ("userTask".equals(activityType)) {
                String assignee = (String) node.get("assignee");
                nodeInfo = "用户任务: " + activityName;
                if (assignee != null && !assignee.isEmpty()) {
                    nodeInfo += " (处理人: " + assignee + ")";
                }
            } else if ("exclusiveGateway".equals(activityType)) {
                nodeInfo = "条件网关: " + activityName;
                String nextActivityId = (String) node.get("nextActivityId");
                if (node.containsKey("conditionActivityId")) {
                    String conditionActivityId = (String) node.get("conditionActivityId");
                    nodeInfo += " → 条件分支: " + conditionActivityId;
                }
            } else {
                nodeInfo = activityType + ": " + activityName;
            }

            description.append(step++).append(". ").append(nodeInfo).append(timeInfo).append("\n");

            // 添加下一步信息
            if (node.containsKey("nextActivityId") && step <= historyPath.size()) {
                String nextActivityId = (String) node.get("nextActivityId");
                // 尝试获取下一个节点的名称
                String nextActivityName = findActivityNameById(historyPath, nextActivityId);
                if (nextActivityName != null) {
                    description.append("   → 指向下一步: ").append(nextActivityName).append("\n");
                }
            }
        }

        description.append("\n总计步骤: ").append(historyPath.size()).append("步");

        return description.toString();*/
    }

    private Map<String, Object> generateVariables(WorkflowProcessVo workflowStartVo) {
        // 准备流程变量
        Map<String, Object> variables = new HashMap<>(Optional.ofNullable(workflowStartVo.getFormData()).orElse(new JSONObject()));
        // 添加其他必要变量
        AsUser loginUser = Objects.requireNonNull(Global.getLoginUser()).getAsUser();
        variables.put("startUserId", loginUser.getUserId());
        variables.put("startUserName", loginUser.getUserName());
        variables.put("startUserDept", loginUser.getDeptId());
        variables.put("businessKey", workflowStartVo.getBusinessKey());
        return variables;
    }

    /**
     * 根据节点ID查找节点名称
     */
    private String findActivityNameById(List<Map<String, Object>> historyPath, String activityId) {
        for (Map<String, Object> node : historyPath) {
            if (activityId.equals(node.get("activityId"))) {
                return (String) node.get("activityName");
            }
        }
        return null;
    }

    /**
     * 评估条件表达式
     * 
     * @param expression 条件表达式
     * @param variables 流程变量
     * @return 条件表达式评估结果
     */
    private boolean evaluateCondition(String expression, Map<String, Object> variables) {
        if (StringUtils.isBlank(expression)) {
            return true; // 空表达式默认为真
        }
        
        try {
            return QLExpressUtil.executeBoolean(expression, variables);
        } catch (Exception e) {
            log.error("评估条件表达式失败: {}, 错误: {}", expression, e.getMessage());
            return false;
        }
    }
} 
 