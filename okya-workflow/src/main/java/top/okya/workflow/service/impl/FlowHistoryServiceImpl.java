package top.okya.workflow.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.okya.component.constants.FlowConstants;
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
import top.okya.workflow.service.FlowHistoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author: maojiaqi
 * @Date: 2025/4/17 22:30
 * @describe: 流程实例相关服务
 */
@Service
@Slf4j
public class FlowHistoryServiceImpl implements FlowHistoryService {

    @Autowired
    private AsFlowMapper asFlowMapper;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public WorkflowHistory getProcessHistoryPath(String processInstanceId) {
        // 直接查询获取部署ID
        String processDefinitionId = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult()
                .getProcessDefinitionId(); // 获取流程定义ID

        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);

        // 从流程定义ID获取部署ID
        String deploymentId = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult()
                .getDeploymentId();

        AsFlow asFlow = asFlowMapper.queryFlowByDeploymentId(deploymentId);
        if (Objects.isNull(asFlow)) {
            throw new FlowException(FlowExceptionType.FLOW_TEMPLATE_NOT_FOUND, deploymentId);
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
            if (Objects.equals(flowNode.getInteger(FlowConstants.NODE_TYPE), 3)) {
                // 获取条件表达式
                String conditionExpression = flowNode.getString(FlowConstants.CONDITIONS);
                boolean conditionMet = false;

                // 评估条件表达式
                if (StringUtils.isNotBlank(conditionExpression)) {
                    conditionMet = evaluateCondition(conditionExpression, historicVariables);
                }
                // 获取该条件节点指向的目标节点ID并判断目标节点是否执行完成
                JSONArray nodeTo = flowNode.getJSONArray(FlowConstants.NODE_TO);
                boolean flag = nodeTo == null || nodeTo.isEmpty() || nodeIds.contains(nodeTo.getString(0));
                flowNode.put("executed", conditionMet && flag);
            } else {
                String flowId = flowNode.getString(FlowConstants.NODE_ID);
                if(nodeExecuteInfos.containsKey(flowId)){
                    flowNode.put("executedInfo", nodeExecuteInfos.get(flowId));
                }
                flowNode.put("executed", nodeIds.contains(flowId));
            }
        }
        return new WorkflowHistory().setFlowNodes(flowNodes).setFinished(nodeIds.contains(FlowConstants.END_EVENT_ID));
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
        variables.put("flowBusinessKey", workflowStartVo.getFlowBusinessKey());
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
 