package top.okya.workflow.util;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.SubProcess;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

@Slf4j
public class CamundaUtils {

    /**
     * 根据节点，获取入口连线
     *
     * @param target 源节点
     * @return 入口连线列表
     */
    public static List<SequenceFlow> getElementIncomingFlows(BpmnModelInstance modelInstance, FlowNode target) {
        return modelInstance.getModelElementsByType(SequenceFlow.class)
                .stream().filter(flow -> flow.getTarget().getId().equals(target.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 根据节点，获取出口连线
     *
     * @param source 源节点
     * @return 出口连线列表
     */
    public static List<SequenceFlow> getElementOutgoingFlows(BpmnModelInstance modelInstance, FlowNode source) {
        return modelInstance.getModelElementsByType(SequenceFlow.class)
                .stream().filter(flow -> flow.getSource().getId().equals(source.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取全部节点列表，包含子流程节点
     *
     * @param modelInstance BPMN模型实例
     * @return 所有节点集合
     */
    public static Collection<FlowElement> getAllElements(BpmnModelInstance modelInstance) {
        Set<FlowElement> allElements = new HashSet<>();
        Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);

        for (Process process : processes) {
            allElements.addAll(process.getFlowElements());
            // 递归获取子流程中的节点
            Collection<SubProcess> subProcesses = process.getChildElementsByType(SubProcess.class);
            for (SubProcess subProcess : subProcesses) {
                allElements.addAll(subProcess.getFlowElements());
            }
        }
        return allElements;
    }

    /**
     * 迭代获取父级任务节点列表，向前找
     *
     * @param source          起始节点
     * @param hasSequenceFlow 已经经过的连线的ID
     * @param userTaskList    已找到的用户任务节点
     * @return 父级用户任务列表
     */
    public static List<UserTask> iteratorFindParentUserTasks(BpmnModelInstance modelInstance, FlowNode source, Set<String> hasSequenceFlow, List<UserTask> userTaskList) {
        userTaskList = userTaskList == null ? new ArrayList<>() : userTaskList;
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;

        // 获取入口连线
        List<SequenceFlow> sequenceFlows = getElementIncomingFlows(modelInstance, source);

        for (SequenceFlow sequenceFlow : sequenceFlows) {
            if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                continue;
            }
            hasSequenceFlow.add(sequenceFlow.getId());

            FlowNode sourceNode = sequenceFlow.getSource();
            if (sourceNode instanceof UserTask) {
                userTaskList.add((UserTask) sourceNode);
                continue;
            }

            if (sourceNode instanceof SubProcess) {
                StartEvent startEvent = ((SubProcess) sourceNode).getChildElementsByType(StartEvent.class).iterator().next();
                List<UserTask> childUserTaskList = findChildProcessUserTasks(modelInstance, startEvent, null, null);
                if (childUserTaskList != null && !childUserTaskList.isEmpty()) {
                    userTaskList.addAll(childUserTaskList);
                    continue;
                }
            }

            userTaskList = iteratorFindParentUserTasks(modelInstance, sourceNode, new HashSet<>(hasSequenceFlow), userTaskList);
        }
        return userTaskList;
    }

    /**
     * 迭代获取子流程用户任务节点
     *
     * @param source          起始节点
     * @param hasSequenceFlow 已经经过的连线的ID
     * @param userTaskList    需要撤回的用户任务列表
     * @return 子流程用户任务列表
     */
    public static List<UserTask> findChildProcessUserTasks(BpmnModelInstance modelInstance, FlowNode source, Set<String> hasSequenceFlow, List<UserTask> userTaskList) {
        hasSequenceFlow = hasSequenceFlow == null ? new HashSet<>() : hasSequenceFlow;
        userTaskList = userTaskList == null ? new ArrayList<>() : userTaskList;

        List<SequenceFlow> sequenceFlows = getElementOutgoingFlows(modelInstance, source);

        for (SequenceFlow sequenceFlow : sequenceFlows) {
            if (hasSequenceFlow.contains(sequenceFlow.getId())) {
                continue;
            }
            hasSequenceFlow.add(sequenceFlow.getId());

            FlowNode targetNode = sequenceFlow.getTarget();
            if (targetNode instanceof UserTask) {
                userTaskList.add((UserTask) targetNode);
                continue;
            }

            if (targetNode instanceof SubProcess) {
                StartEvent startEvent = ((SubProcess) targetNode).getChildElementsByType(StartEvent.class).iterator().next();
                List<UserTask> childUserTaskList = findChildProcessUserTasks(modelInstance, startEvent, hasSequenceFlow, null);
                if (childUserTaskList != null && !childUserTaskList.isEmpty()) {
                    userTaskList.addAll(childUserTaskList);
                    continue;
                }
            }

            userTaskList = findChildProcessUserTasks(modelInstance, targetNode, new HashSet<>(hasSequenceFlow), userTaskList);
        }
        return userTaskList;
    }

    /**
     * 历史节点数据清洗，清洗掉又回滚导致的脏数据
     *
     * @param modelInstance        BPMN模型实例
     * @param historicActivityList 历史任务实例信息，数据采用开始时间升序
     * @return 清洗后的历史节点ID列表
     */
    public static List<String> historicTaskInstanceClean(BpmnModelInstance modelInstance, List<HistoricActivityInstance> historicActivityList) {
        // 获取所有会签节点
        List<String> multiTask = modelInstance.getModelElementsByType(UserTask.class).stream()
                .filter(task -> task.getLoopCharacteristics() != null)
                .map(FlowElement::getId)
                .collect(Collectors.toList());

        // 使用栈来处理历史记录
        Stack<HistoricActivityInstance> stack = new Stack<>();
        historicActivityList.forEach(stack::push);

        List<String> lastHistoricTaskInstanceList = new ArrayList<>();
        StringBuilder userTaskKey = null;
        List<String> deleteKeyList = new ArrayList<>();
        List<Set<String>> dirtyDataLineList = new ArrayList<>();

        int multiIndex = -1;
        StringBuilder multiKey = null;
        boolean multiOpera = false;

        while (!stack.empty()) {
            Set<String> sequenceFlows = new HashSet<>();
            boolean[] isDirtyData = {false};

            // 检查是否在脏数据线上
            for (Set<String> oldDirtyDataLine : dirtyDataLineList) {
                if (oldDirtyDataLine.contains(stack.peek().getActivityId())) {
                    isDirtyData[0] = true;
                    break;
                }
            }

            if (!isDirtyData[0]) {
                lastHistoricTaskInstanceList.add(stack.peek().getActivityId());
            }

            // 处理脏数据清理
            processDirtyDataCleanup(stack.peek(), multiTask, deleteKeyList, dirtyDataLineList, multiIndex, multiKey, multiOpera);

            userTaskKey = new StringBuilder(stack.pop().getActivityId());
        }

        log.info("清洗后的历史节点数据：{}", lastHistoricTaskInstanceList);
        return lastHistoricTaskInstanceList;
    }

    private static String extractDirtyPoint(String deleteReason) {
        if (deleteReason.contains("Change activity to ")) {
            return deleteReason.replace("Change activity to ", "");
        }
        if (deleteReason.contains("Change parent activity to ")) {
            return deleteReason.replace("Change parent activity to ", "");
        }
        return "";
    }

    public static FlowNode findFlowNodeById(BpmnModelInstance modelInstance, String id) {
        return modelInstance.getModelElementsByType(FlowNode.class).stream()
                .filter(node -> node.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private static Set<String> findDirtyRoads(BpmnModelInstance modelInstance, FlowNode source, Set<String> sequenceFlows, List<String> targets) {
        Set<String> dirtyRoads = new HashSet<>();
        List<SequenceFlow> incomingFlows = getElementIncomingFlows(modelInstance, source);

        for (SequenceFlow flow : incomingFlows) {
            if (sequenceFlows.contains(flow.getId())) {
                continue;
            }
            sequenceFlows.add(flow.getId());

            FlowNode sourceNode = flow.getSource();
            if (targets.contains(sourceNode.getId())) {
                dirtyRoads.add(sourceNode.getId());
                continue;
            }

            if (sourceNode instanceof SubProcess) {
                StartEvent startEvent = ((SubProcess) sourceNode).getChildElementsByType(StartEvent.class).iterator().next();
                dirtyRoads.addAll(findDirtyRoads(modelInstance, startEvent, sequenceFlows, targets));
            }

            dirtyRoads.addAll(findDirtyRoads(modelInstance, sourceNode, new HashSet<>(sequenceFlows), targets));
        }
        return dirtyRoads;
    }

    private static void processDirtyDataCleanup(HistoricActivityInstance currentInstance,
                                                List<String> multiTask,
                                                List<String> deleteKeyList,
                                                List<Set<String>> dirtyDataLineList,
                                                int multiIndex,
                                                StringBuilder multiKey,
                                                boolean multiOpera) {
        // 处理会签节点
        if (multiKey == null && multiTask.contains(currentInstance.getActivityId())) {
            for (int i = 0; i < deleteKeyList.size(); i++) {
                if (deleteKeyList.get(i).contains(currentInstance.getActivityId())) {
                    multiIndex = i;
                    multiKey = new StringBuilder(currentInstance.getActivityId());
                    break;
                }
            }
        }

        // 处理脏数据清理
        for (int i = 0; i < deleteKeyList.size(); i++) {
            if (deleteKeyList.get(i).contains(currentInstance.getActivityId())) {
                deleteKeyList.set(i, deleteKeyList.get(i).replace(currentInstance.getActivityId() + ",", ""));
                if (deleteKeyList.get(i).isEmpty()) {
                    deleteKeyList.remove(i);
                    dirtyDataLineList.remove(i);
                    break;
                }
            }
        }
    }
} 