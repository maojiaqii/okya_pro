package top.okya.workflow.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import top.okya.component.constants.CharacterConstants;
import top.okya.component.enums.exception.FlowExceptionType;
import top.okya.component.exception.FlowException;
import top.okya.workflow.domain.AsFlow;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BpmnConverter {

    private static final String NODE_ID = "nodeId";
    private static final String NODE_NAME = "nodeName";
    private static final String NODE_TYPE = "nodeType";
    private static final String IS_DEFAULT = "isDefault";
    private static final String CONDITIONS = "conditions";
    private static final String NODE_TO = "nodeTo";
    private static final String END_EVENT_ID = "endEventIdm";

    public static BpmnModelInstance convertToBpmn(AsFlow asFlow) {
        // 创建BPMN模型实例
        BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
        Definitions definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace("http://camunda.org/examples");
        modelInstance.setDefinitions(definitions);

        // 创建流程
        Process process = modelInstance.newInstance(Process.class);
        process.setId(asFlow.getFlowCode());  // 确保流程ID与flowCode相同
        process.setName(asFlow.getFlowName());
        process.setExecutable(true);  // 重要！确保流程是可执行的
        definitions.addChildElement(process);

        JSONArray nodes = asFlow.getFlowNodes();

        // 创建节点映射
        Map<String, FlowNode> nodeMap = new HashMap<>();
        // 创建条件节点映射
        Map<String, ConditionExpression> conditionMap = new HashMap<>();
        // 创建条件节点映射
        Map<String, JSONObject> conditionNodeMap = new HashMap<>();

        // 遍历节点并创建对应的BPMN元素
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            String nodeId = node.getString(NODE_ID);
            String nodeName = node.getString(NODE_NAME);
            int nodeType = node.getInteger(NODE_TYPE);

            switch (nodeType) {
                case 1: // 发起人节点
                    StartEvent startEvent = modelInstance.newInstance(StartEvent.class);
                    startEvent.setId(nodeId);
                    startEvent.setName(nodeName);
                    process.addChildElement(startEvent);
                    nodeMap.put(nodeId, startEvent);
                    break;
                case 2: // 网关节点
                    ExclusiveGateway gateway = modelInstance.newInstance(ExclusiveGateway.class);
                    gateway.setId(nodeId);
                    gateway.setName(nodeName);
                    process.addChildElement(gateway);
                    nodeMap.put(nodeId, gateway);
                    break;
                case 3: // 条件节点
                    ConditionExpression conditionExpressionObj = modelInstance.newInstance(ConditionExpression.class);
                    conditionExpressionObj.setId(nodeId);
                    String conditionExpression = node.getString(CONDITIONS);
                    if (StringUtils.isNotBlank(conditionExpression)) {
                        conditionExpressionObj.setTextContent(conditionExpression);
                    }
                    conditionNodeMap.put(nodeId, node);
                    conditionMap.put(nodeId, conditionExpressionObj);
                    break;
                case 4: // 审核人节点
                    UserTask userTask = modelInstance.newInstance(UserTask.class);
                    userTask.setId(nodeId);
                    userTask.setName(nodeName);
                    process.addChildElement(userTask);
                    nodeMap.put(nodeId, userTask);
                    break;
            }
        }

        // 添加结束事件
        EndEvent endEvent = modelInstance.newInstance(EndEvent.class);
        endEvent.setId(END_EVENT_ID);
        endEvent.setName("流程结束");
        process.addChildElement(endEvent);

        // 连接节点
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            String nodeId = node.getString(NODE_ID);
            JSONArray nodeTo = node.getJSONArray(NODE_TO);
            if (nodeMap.containsKey(nodeId)) {
                if (nodeTo != null && !nodeTo.isEmpty()) {
                    for (int j = 0; j < nodeTo.size(); j++) {
                        String targetNodeId = nodeTo.getString(j);
                        SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
                        sequenceFlow.setSource(nodeMap.get(nodeId));

                        if (nodeMap.containsKey(targetNodeId)) { // 流程节点间的直接连线
                            sequenceFlow.setId(nodeId + CharacterConstants.UNDER_SCORE + targetNodeId);
                            sequenceFlow.setTarget(nodeMap.get(targetNodeId));
                        } else if (conditionMap.containsKey(targetNodeId)) { // 条件连线
                            JSONObject nodeCondition = conditionNodeMap.get(targetNodeId);
                            JSONArray nodeConditionTo = nodeCondition.getJSONArray(NODE_TO);
                            if (nodeConditionTo != null && !nodeConditionTo.isEmpty()) {
                                String conditionTargetNodeId = nodeConditionTo.getString(0);
                                sequenceFlow.setId(nodeId + CharacterConstants.UNDER_SCORE + targetNodeId + CharacterConstants.UNDER_SCORE + conditionTargetNodeId);
                                if (nodeMap.containsKey(conditionTargetNodeId)) {
                                    sequenceFlow.setTarget(nodeMap.get(conditionTargetNodeId));
                                    ConditionExpression conditionExpressionObj = conditionMap.get(targetNodeId);
                                    // 添加条件表达式
                                    sequenceFlow.setConditionExpression(conditionExpressionObj);
                                } else {
                                    // 如果目标节点不存在，连接到结束事件
                                    sequenceFlow.setTarget(endEvent);
                                }
                            } else {
                                // 条件节点没有后续节点时，连接到结束事件
                                sequenceFlow.setId(nodeId + CharacterConstants.UNDER_SCORE + targetNodeId + CharacterConstants.UNDER_SCORE + END_EVENT_ID);
                                sequenceFlow.setTarget(endEvent);
                            }
                        }

                        process.addChildElement(sequenceFlow);
                    }
                } else {
                    // 如果没有后续节点，连接到结束事件
                    SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
                    sequenceFlow.setId(nodeId + CharacterConstants.UNDER_SCORE + END_EVENT_ID);
                    sequenceFlow.setSource(nodeMap.get(nodeId));
                    sequenceFlow.setTarget(endEvent);
                    process.addChildElement(sequenceFlow);
                }
            }
        }

        // 验证模型是否合法
        try {
            Bpmn.validateModel(modelInstance);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
            throw new FlowException(FlowExceptionType.FLOW_VALIDATE_ERROR, e.getMessage());
        }

        return modelInstance;
    }
}