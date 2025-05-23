package top.okya.workflow.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.okya.component.constants.FlowConstants;
import top.okya.component.domain.WorkflowHistory;
import top.okya.component.domain.child.FullPath;
import top.okya.component.domain.child.NodeExecuteInfo;
import top.okya.component.enums.exception.FlowExceptionType;
import top.okya.component.exception.FlowException;
import top.okya.component.utils.common.DateFormatUtil;
import top.okya.component.utils.common.QLExpressUtil;
import top.okya.workflow.dao.AsFlowBusinessInfoMapper;
import top.okya.workflow.service.FlowHistoryService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: maojiaqi
 * @Date: 2025/4/17 22:30
 * @describe: 流程历史相关服务
 */
@Service
@Slf4j
public class FlowHistoryServiceImpl implements FlowHistoryService {

    @Autowired
    AsFlowBusinessInfoMapper asFlowBusinessInfoMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Override
    public WorkflowHistory getProcessHistoryPath(String processInstanceId) {
        List<Map<String, Object>> asFlowInfo = asFlowBusinessInfoMapper.queryByProcInstId(processInstanceId);
        if (asFlowInfo == null || asFlowInfo.isEmpty()) {
            log.error("未获取到流程执行信息：{}", processInstanceId);
            throw new FlowException(FlowExceptionType.FLOW_BUSINESS_INFO_NOT_FOUND, processInstanceId);
        }

        // 查询流程的历史活动实例，按开始时间排序,只有任务节点
        List<HistoricTaskInstance> activityInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list().stream().filter(e -> !Objects.equals(e.getDeleteReason(), "deleted")).collect(Collectors.toList());

        if (activityInstances.isEmpty()) {
            log.warn("流程实例不存在或没有历史记录，流程实例ID: {}", processInstanceId);
            return null;
        }

        Map<String, List<NodeExecuteInfo>> simplestPath = getSimplestPath(activityInstances);
        Set<String> nodeIds = simplestPath.keySet();
        // 获取流程历史变量
        Map<String, Object> historicVariables = new HashMap<>();
        historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list().forEach(variable -> historicVariables.put(variable.getName(), variable.getValue()));

        Map<String, Object> asFlowInfoObj = asFlowInfo.get(0);
        JSONArray flowNodes = JSON.parseArray(asFlowInfoObj.get("flow_nodes").toString());
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
                if (simplestPath.containsKey(flowId)) {
                    flowNode.put("executedInfos", simplestPath.get(flowId));
                }
                flowNode.put("executed", nodeIds.contains(flowId));
            }
        }
        List<FullPath> fullPathMap = activityInstances.stream()
                .collect(Collectors.groupingBy(HistoricTaskInstance::getOwner,
                        Collectors.collectingAndThen(Collectors.toList(), this::getFullPath)
                )).values().stream()
                .sorted(Comparator.comparing(FullPath::getFinishTime,
                        Comparator.nullsFirst(Comparator.reverseOrder())
                )).collect(Collectors.toList());
        return new WorkflowHistory().setFlowNodes(flowNodes).setFinished(nodeIds.contains(FlowConstants.END_EVENT_ID)).setFullPath(fullPathMap);
    }

    private Map<String, List<NodeExecuteInfo>> getSimplestPath(List<HistoricTaskInstance> tasks) {
        List<String> path = new ArrayList<>();
        List<String> groupId = new ArrayList<>();

        for (HistoricTaskInstance task : tasks) {
            String key = task.getTaskDefinitionKey();
            int lastIndex = path.lastIndexOf(key);

            if (lastIndex != -1) {
                // 截断路径到该键最后一次出现的位置（包括该位置）
                path = new ArrayList<>(path.subList(0, lastIndex));
                groupId = new ArrayList<>(groupId.subList(0, lastIndex));
            }
            path.add(key);
            groupId.add(task.getOwner());
        }

        List<String> finalPath = path;
        List<String> finalGroupId = groupId;
        return tasks.stream().filter(e -> finalPath.contains(e.getTaskDefinitionKey()) && finalGroupId.contains(e.getOwner())).collect(Collectors.groupingBy(HistoricTaskInstance::getTaskDefinitionKey, Collectors.collectingAndThen(Collectors.toList(), this::aggregateTaskData)));
    }

    private FullPath getFullPath(List<HistoricTaskInstance> tasks) {
        FullPath fullPath = new FullPath();
        HistoricTaskInstance historicTaskInstance = tasks.get(0);
        Date latestStart = historicTaskInstance.getStartTime();
        fullPath.setStartTime(latestStart);
        fullPath.setId(historicTaskInstance.getOwner());
        fullPath.setNodeName(historicTaskInstance.getName());
        fullPath.setCheckerCount(tasks.size());
        boolean isFullComplete = tasks.stream().allMatch(w -> Objects.nonNull(w.getEndTime()));
        if (isFullComplete) {
            HistoricTaskInstance latestEndTask = tasks.stream().max(Comparator.comparing(HistoricTaskInstance::getEndTime)).orElse(null);
            Date latestEnd = Objects.requireNonNull(latestEndTask).getEndTime();
            fullPath.setFinishTime(latestEnd);
            fullPath.setDuration((latestEnd.getTime() - latestStart.getTime()) / 1000 / 60);
            List<Comment> taskComments = taskService.getTaskComments(latestEndTask.getId());
            fullPath.setResult(taskComments != null && !taskComments.isEmpty() ? taskComments.get(0).getFullMessage().substring(0, 2) : null);
        }
        fullPath.setExecutedInfos(aggregateTaskData(tasks));
        return fullPath;
    }

    private List<NodeExecuteInfo> aggregateTaskData(List<HistoricTaskInstance> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return null;
        }

        List<NodeExecuteInfo> nodeExecuteInfos = new ArrayList<>();
        for (HistoricTaskInstance eTask : tasks) {
            Date latestStart = eTask.getStartTime();
            Date latestEnd = eTask.getEndTime();
            long totalDuration = latestEnd == null ? -1L : ((latestEnd.getTime() - latestStart.getTime()) / 1000 / 60);
            String assignee = asFlowBusinessInfoMapper.queryByAssignee(eTask.getAssignee());
            List<Comment> taskComments = taskService.getTaskComments(eTask.getId());
            nodeExecuteInfos.add(new NodeExecuteInfo().setActivityName(tasks.get(0).getName()).setActivityType("userTask").setStartTime(DateFormatUtil.formatDate(latestStart)).setEndTime(DateFormatUtil.formatDate(latestEnd)).setDurationInMin(totalDuration).setAssignee(assignee).setComment(taskComments != null && !taskComments.isEmpty() ? taskComments.get(0).getFullMessage() : null));
        }
        return nodeExecuteInfos;
    }

    /**
     * 评估条件表达式
     *
     * @param expression 条件表达式
     * @param variables  流程变量
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
 