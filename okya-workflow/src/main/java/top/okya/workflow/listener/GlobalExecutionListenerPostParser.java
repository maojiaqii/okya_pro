package top.okya.workflow.listener;

import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.core.variable.mapping.IoMapping;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.camunda.bpm.engine.impl.variable.VariableDeclaration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2025/5/19 9:53
 * @describe: 流程执行监听配置
 */

@Component
public class GlobalExecutionListenerPostParser implements BpmnParseListener {
    @Override
    public void parseProcess(Element element, ProcessDefinitionEntity processDefinitionEntity) {

    }

    @Override
    public void parseStartEvent(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseExclusiveGateway(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseInclusiveGateway(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseParallelGateway(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseScriptTask(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseServiceTask(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseBusinessRuleTask(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseTask(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseManualTask(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseUserTask(Element element, ScopeImpl scope, ActivityImpl activity) {
        UserTaskActivityBehavior behavior = (UserTaskActivityBehavior) activity.getActivityBehavior();
        TaskDefinition taskDefinition = behavior.getTaskDefinition();
        taskDefinition.addTaskListener(TaskListener.EVENTNAME_COMPLETE, new TaskCompleteListener());
        taskDefinition.addTaskListener(TaskListener.EVENTNAME_CREATE, new TaskCreateListener());
    }

    @Override
    public void parseEndEvent(Element element, ScopeImpl scope, ActivityImpl activity) {
        activity.addListener(ExecutionListener.EVENTNAME_START, new EndEventListener());
    }

    @Override
    public void parseBoundaryTimerEventDefinition(Element element, boolean b, ActivityImpl activity) {

    }

    @Override
    public void parseBoundaryErrorEventDefinition(Element element, boolean b, ActivityImpl activity, ActivityImpl activity1) {

    }

    @Override
    public void parseSubProcess(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseCallActivity(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseProperty(Element element, VariableDeclaration variableDeclaration, ActivityImpl activity) {

    }

    @Override
    public void parseSequenceFlow(Element element, ScopeImpl scope, TransitionImpl transition) {

    }

    @Override
    public void parseSendTask(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseMultiInstanceLoopCharacteristics(Element element, Element element1, ActivityImpl activity) {

    }

    @Override
    public void parseIntermediateTimerEventDefinition(Element element, ActivityImpl activity) {

    }

    @Override
    public void parseRootElement(Element element, List<ProcessDefinitionEntity> list) {

    }

    @Override
    public void parseReceiveTask(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseIntermediateSignalCatchEventDefinition(Element element, ActivityImpl activity) {

    }

    @Override
    public void parseIntermediateMessageCatchEventDefinition(Element element, ActivityImpl activity) {

    }

    @Override
    public void parseBoundarySignalEventDefinition(Element element, boolean b, ActivityImpl activity) {

    }

    @Override
    public void parseEventBasedGateway(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseTransaction(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseCompensateEventDefinition(Element element, ActivityImpl activity) {

    }

    @Override
    public void parseIntermediateThrowEvent(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseIntermediateCatchEvent(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseBoundaryEvent(Element element, ScopeImpl scope, ActivityImpl activity) {

    }

    @Override
    public void parseBoundaryMessageEventDefinition(Element element, boolean b, ActivityImpl activity) {

    }

    @Override
    public void parseBoundaryEscalationEventDefinition(Element element, boolean b, ActivityImpl activity) {

    }

    @Override
    public void parseBoundaryConditionalEventDefinition(Element element, boolean b, ActivityImpl activity) {

    }

    @Override
    public void parseIntermediateConditionalEventDefinition(Element element, ActivityImpl activity) {

    }

    @Override
    public void parseConditionalStartEventForEventSubprocess(Element element, ActivityImpl activity, boolean b) {

    }

    @Override
    public void parseIoMapping(Element element, ActivityImpl activity, IoMapping ioMapping) {

    }
}
