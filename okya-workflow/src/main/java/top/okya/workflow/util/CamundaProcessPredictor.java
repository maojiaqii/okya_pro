package top.okya.workflow.util;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.springframework.stereotype.Component;
import top.okya.component.domain.NextNode;
import top.okya.component.utils.common.QLExpressUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author: maojiaqi
 * @Date: 2025/5/6 9:28
 * @describe:
 */

@Component
@Slf4j
public class CamundaProcessPredictor {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private RepositoryService repositoryService;

    // 缓存 BPMN 模型实例（Key: processDefinitionId）
    private final Map<String, BpmnModelInstance> modelCache = new ConcurrentHashMap<>();

    /**
     * 预测下一个用户任务节点（通过流程实例ID）
     */
    public List<NextNode> predictNextUserTasksByProcessInstance(String processInstanceId, Map<String, Object> variables) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (processInstance == null) {
            throw new IllegalArgumentException("流程实例不存在: " + processInstanceId);
        }
        return predictNextUserTasks(processInstance.getId(), null, variables);
    }

    /**
     * 预测下一个用户任务节点（通过当前任务ID）
     */
    public List<NextNode> predictNextUserTasksByTask(String taskId, Map<String, Object> variables) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new IllegalArgumentException("任务不存在: " + taskId);
        }
        return predictNextUserTasks(task.getProcessInstanceId(), task.getTaskDefinitionKey(), variables);
    }

    /**
     * 核心预测逻辑
     */
    private List<NextNode> predictNextUserTasks(String processInstanceId, String currentTaskKey, Map<String, Object> variables1) {
        // 获取流程实例和变量
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
        variables.putAll(variables1);

        // 获取 BPMN 模型
        BpmnModelInstance modelInstance = getCachedBpmnModel(processInstance.getProcessDefinitionId());

        // 确定当前节点
        String fCurrentTaskKey = currentTaskKey != null ? currentTaskKey : runtimeService.getActivityInstance(processInstanceId).getActivityId();
        FlowNode currentElement = modelInstance.getModelElementsByType(FlowNode.class).stream()
                .filter(node -> node.getId().equals(fCurrentTaskKey))
                .findFirst()
                .orElse(null);

        if (currentElement == null) {
            log.error("未找到当前节点：{}", fCurrentTaskKey);
            return Collections.emptyList();
        }

        // 直接从BPMN模型中获取所有连线
        List<SequenceFlow> elementOutgoingFlows = CamundaUtils.getElementOutgoingFlows(modelInstance, currentElement);
        List<NextNode> nextNodes = new ArrayList<>();
        
        log.info("当前节点ID: {}, 类型: {}", 
                currentElement.getId(), 
                currentElement.getClass().getSimpleName());

        // 查找以当前节点为源的所有连线
        for (SequenceFlow flow : elementOutgoingFlows) {
            if (isConditionMet(flow, variables)) {
                nextNodes.addAll(findNextUserTasks(flow.getTarget(), variables, new HashSet<>(), modelInstance));
            }
        }
        
        return nextNodes;
    }

    /**
     * 递归查找下一个用户任务
     */
    private List<NextNode> findNextUserTasks(FlowElement currentElement, Map<String, Object> variables, Set<String> visited, BpmnModelInstance modelInstance) {
        List<NextNode> tasks = new ArrayList<>();

        if (currentElement instanceof UserTask) {
            UserTask userTask = (UserTask) currentElement;
            log.info("找到用户任务节点：{}", userTask.getId());
            tasks.add(new NextNode(
                    userTask.getId(),
                    userTask.getName(),
                    "DIRECT",
                    null
            ));
            return tasks;
        }

        if (currentElement instanceof SequenceFlow) {
            currentElement = ((SequenceFlow) currentElement).getTarget();
        }

        if (currentElement instanceof Gateway) {
            Gateway gateway = (Gateway) currentElement;
            if (visited.contains(gateway.getId())) {
                return tasks; // 防止循环
            }
            visited.add(gateway.getId());
            log.info("处理网关节点：{}", gateway.getId());

            List<SequenceFlow> elementOutgoingFlows = CamundaUtils.getElementOutgoingFlows(modelInstance, gateway);
            // 查找以当前网关为源的所有连线
            for (SequenceFlow flow : elementOutgoingFlows) {
                if (isConditionMet(flow, variables)) {
                    tasks.addAll(findNextUserTasks(flow.getTarget(), variables, new HashSet<>(visited), modelInstance));
                }
            }
        } else if (currentElement instanceof Activity) {
            Activity activity = (Activity) currentElement;
            log.info("处理活动节点：{}", activity.getId());

            List<SequenceFlow> elementOutgoingFlows = CamundaUtils.getElementOutgoingFlows(modelInstance, activity);
            // 查找以当前活动为源的所有连线
            for (SequenceFlow flow : elementOutgoingFlows) {
                tasks.addAll(findNextUserTasks(flow.getTarget(), variables, visited, modelInstance));
            }
        }

        return tasks.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 评估条件表达式
     */
    private boolean isConditionMet(SequenceFlow flow, Map<String, Object> variables) {
        ConditionExpression condition = flow.getConditionExpression();
        if (condition == null) {
            return true; // 无条件视为通过
        }
        try {
            return QLExpressUtil.executeBoolean(condition.getTextContent(), variables);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取缓存的 BPMN 模型
     */
    private BpmnModelInstance getCachedBpmnModel(String processDefinitionId) {
        return modelCache.computeIfAbsent(processDefinitionId, id ->
                repositoryService.getBpmnModelInstance(processDefinitionId));
    }
}
