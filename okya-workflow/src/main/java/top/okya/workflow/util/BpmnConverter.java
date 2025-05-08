package top.okya.workflow.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import top.okya.component.constants.CharacterConstants;
import top.okya.component.constants.CommonConstants;
import top.okya.component.constants.FlowConstants;
import top.okya.component.enums.exception.FlowExceptionType;
import top.okya.component.exception.FlowException;
import top.okya.workflow.domain.AsFlow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class BpmnConverter {
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
        
        // 添加默认的流程变量
        ExtensionElements extensionElements = modelInstance.newInstance(ExtensionElements.class);
        process.addChildElement(extensionElements);
        
        // 创建Camunda属性
        CamundaProperties camundaProperties = modelInstance.newInstance(CamundaProperties.class);
        extensionElements.addChildElement(camundaProperties);
        
        JSONArray nodes = asFlow.getFlowNodes();
        definitions.addChildElement(process);

        // 创建节点映射
        Map<String, FlowNode> nodeMap = new HashMap<>();
        // 创建条件节点映射
        Map<String, ConditionExpression> conditionMap = new HashMap<>();
        // 创建条件节点映射
        Map<String, JSONObject> conditionNodeMap = new HashMap<>();

        StartEvent startEvent = modelInstance.newInstance(StartEvent.class);
        startEvent.setId(FlowConstants.START_EVENT_ID);
        startEvent.setName("流程开始");
        process.addChildElement(startEvent);

        // 遍历节点并创建对应的BPMN元素
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            String nodeId = node.getString(FlowConstants.NODE_ID);
            String nodeName = node.getString(FlowConstants.NODE_NAME);
            int nodeType = node.getInteger(FlowConstants.NODE_TYPE);

            switch (nodeType) {
                case 1: // 发起人节点
                    UserTask userTask1 = modelInstance.newInstance(UserTask.class);
                    userTask1.setId(nodeId);
                    userTask1.setName(nodeName);
                    // 流程发起人即为发起岗的处理人
                    userTask1.setCamundaAssignee("${startUserId}");
                    process.addChildElement(userTask1);
                    nodeMap.put(nodeId, userTask1);
                    defineProcessVariable(modelInstance, camundaProperties, "startUserId", CommonConstants.UNKNOWN);
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
                    String conditionExpression = node.getString(FlowConstants.CONDITIONS);
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
                    // 将单人审批和多人审批合并（单人审批 = 多人会签）
                    MultiInstanceLoopCharacteristics loop = modelInstance.newInstance(MultiInstanceLoopCharacteristics.class);
                    loop.setSequential(false); // 并行会签
                    // 指定任务接收人时，只需要添加nodeId作为key的流程变量（值需要是List集合）
                    loop.setCamundaCollection(String.format("${%s}", nodeId));
                    loop.setCamundaElementVariable(nodeId + "_approver"); // 元素变量
                    String signType = node.getString("signType");
                    if (Objects.equals(signType, "1")) {
                        // 或签：设置条件为至少一个实例完成
                        CompletionCondition completionCondition = modelInstance.newInstance(CompletionCondition.class);
                        completionCondition.setTextContent("${nrOfCompletedInstances >= 1}");
                        loop.setCompletionCondition(completionCondition);
                    } else if (Objects.equals(signType, "2")) {
                        // 会签：不设置completionCondition，需要所有实例完成
                    } else if (Objects.equals(signType, "3")) {
                        // 比例签：完成比例达到阈值
                        CompletionCondition completionCondition = modelInstance.newInstance(CompletionCondition.class);
                        Integer approvePercent = node.getInteger("approvePercent");
                        completionCondition.setTextContent(String.format("${nrOfCompletedInstances / nrOfInstances >= %s}", approvePercent / 100.0));
                        loop.setCompletionCondition(completionCondition);
                    }
                    userTask.setLoopCharacteristics(loop);
                    // 指定受理人
                    userTask.setCamundaAssignee(String.format("${%s_approver}", nodeId));
                    process.addChildElement(userTask);
                    nodeMap.put(nodeId, userTask);
                    defineProcessVariable(modelInstance, camundaProperties, nodeId, CharacterConstants.BRACKETS_LEFT + CharacterConstants.DOUBLE_QUOTATION_MARK + CommonConstants.UNKNOWN + CharacterConstants.DOUBLE_QUOTATION_MARK + CharacterConstants.BRACKETS_RIGHT);
                    break;
            }
        }

        // 添加结束事件
        EndEvent endEvent = modelInstance.newInstance(EndEvent.class);
        endEvent.setId(FlowConstants.END_EVENT_ID);
        endEvent.setName("流程结束");
        process.addChildElement(endEvent);

        // 连接节点
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            int nodeType = node.getInteger(FlowConstants.NODE_TYPE);
            String nodeId = node.getString(FlowConstants.NODE_ID);
            JSONArray nodeTo = node.getJSONArray(FlowConstants.NODE_TO);
            if (nodeMap.containsKey(nodeId)) {
                if (nodeType == 1) {
                    // 发起人链接开始节点
                    SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
                    sequenceFlow.setId(FlowConstants.START_EVENT_ID + CharacterConstants.UNDER_SCORE + nodeId);
                    sequenceFlow.setSource(startEvent);
                    sequenceFlow.setTarget(nodeMap.get(nodeId));
                    process.addChildElement(sequenceFlow);
                }
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
                            JSONArray nodeConditionTo = nodeCondition.getJSONArray(FlowConstants.NODE_TO);
                            if (nodeConditionTo != null && !nodeConditionTo.isEmpty()) {
                                String conditionTargetNodeId = nodeConditionTo.getString(0);
                                sequenceFlow.setId(nodeId + CharacterConstants.UNDER_SCORE + targetNodeId + CharacterConstants.UNDER_SCORE + conditionTargetNodeId);
                                // 如果目标节点不存在，连接到结束事件
                                sequenceFlow.setTarget(nodeMap.getOrDefault(conditionTargetNodeId, endEvent));
                            } else {
                                // 条件节点没有后续节点时，连接到结束事件
                                sequenceFlow.setId(nodeId + CharacterConstants.UNDER_SCORE + targetNodeId + CharacterConstants.UNDER_SCORE + FlowConstants.END_EVENT_ID);
                                sequenceFlow.setTarget(endEvent);
                            }
                            // 添加条件表达式
                            sequenceFlow.setConditionExpression(conditionMap.get(targetNodeId));
                        }
                        process.addChildElement(sequenceFlow);
                    }
                } else {
                    // 如果没有后续节点，连接到结束事件
                    SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
                    sequenceFlow.setId(nodeId + CharacterConstants.UNDER_SCORE + FlowConstants.END_EVENT_ID);
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

    /**
     * 辅助方法：定义流程变量
     * @param modelInstance BPMN模型实例
     * @param camundaProperties Camunda属性集合
     * @param name 变量名称
     * @param defaultValue 默认值
     */
    private static void defineProcessVariable(BpmnModelInstance modelInstance, CamundaProperties camundaProperties, 
                                              String name, String defaultValue) {
        CamundaProperty property = modelInstance.newInstance(CamundaProperty.class);
        property.setCamundaName(name);
        property.setCamundaValue(defaultValue);
        camundaProperties.addChildElement(property);
    }
}