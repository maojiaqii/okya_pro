package top.okya.api.authenticated;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.okya.component.annotation.ApiLog;
import top.okya.component.domain.HttpResult;
import top.okya.component.domain.vo.WorkflowDesignVo;
import top.okya.component.domain.vo.WorkflowProcessVo;
import top.okya.component.domain.vo.vgroup.workflow.Cancel;
import top.okya.component.domain.vo.vgroup.workflow.Publish;
import top.okya.component.domain.vo.vgroup.workflow.Save;
import top.okya.component.enums.OperationType;
import top.okya.workflow.service.FlowDesignService;
import top.okya.workflow.service.FlowHistoryService;
import top.okya.workflow.service.FlowProcessService;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2025/4/14 23:00
 * @describe: 流程相关
 */

@RestController
@RequestMapping("/flow")
public class FlowController {

    @Autowired
    FlowDesignService flowDesignService;

    @Autowired
    FlowProcessService flowProcessService;

    @Autowired
    FlowHistoryService flowHistoryService;

    /**
     * 获取流程列表
     */
    @GetMapping(value = "/flowList")
    @ApiLog(title = "获取流程列表", operationType = OperationType.SEARCH)
    public HttpResult getFlowList() {
        return HttpResult.success(flowDesignService.getFlowList());
    }

    /**
     * 获取流程的完整信息
     */
    @GetMapping(value = "/workflowDetail")
    @ApiLog(title = "获取流程的完整信息", operationType = OperationType.SEARCH)
    public HttpResult getWorkflow(@NotBlank(message = "流程Id不能为空！") @RequestParam(value = "flowId") String flowId) {
        return HttpResult.success(flowDesignService.getWorkflow(flowId));
    }

    /**
     * 新建流程
     */
    @PostMapping(value = "/saveWorkflow")
    @ApiLog(title = "获取流程列表", operationType = OperationType.SEARCH)
    public HttpResult saveWorkflow(@Validated(Save.class) @RequestBody WorkflowDesignVo workflowVo) {
        return HttpResult.success(flowDesignService.saveWorkflow(workflowVo));
    }

    /**
     * 发布流程
     */
    @PostMapping(value = "/publishWorkflow")
    @ApiLog(title = "发布流程", operationType = OperationType.OTHER)
    public HttpResult publishWorkflow(@Validated(Publish.class) @RequestBody WorkflowDesignVo workflowVo) {
        return HttpResult.success(flowDesignService.publishWorkflow(workflowVo));
    }

    /**
     * 测试流程
     */
    @PostMapping(value = "/testProcess")
    @ApiLog(title = "测试流程", operationType = OperationType.TEST)
    public HttpResult testProcess(@Validated @RequestBody WorkflowProcessVo workflowStartVo) {
        Map<String, Object> stringObjectMap = flowProcessService.testProcess(workflowStartVo);
        if ((boolean) stringObjectMap.get("completed")) {
            return HttpResult.success("测试流程已成功执行完毕", stringObjectMap.get("processInstanceId"));
        } else {
            return HttpResult.error("测试流程未能正常完成", stringObjectMap.get("processInstanceId"));
        }
    }

    /**
     * 获取流程历史
     */
    @GetMapping(value = "/processHistory")
    @ApiLog(title = "获取流程历史", operationType = OperationType.SEARCH)
    public HttpResult processHistory(@NotBlank(message = "流程实例Id不能为空！") @RequestParam(value = "processInstanceId") String processInstanceId) {
        return HttpResult.success(flowHistoryService.getProcessHistoryPath(processInstanceId));
    }

    /**
     * 发起流程
     */
    @PostMapping(value = "/completeTask")
    @ApiLog(title = "发起流程/送审", operationType = OperationType.OTHER)
    public HttpResult completeTask(@Validated @RequestBody WorkflowProcessVo workflowStartVo) {
        flowProcessService.completeTask(workflowStartVo);
        return HttpResult.success("审核/送审成功");
    }

    /**
     * 获取下一节点审批用户列表
     */
    @PostMapping(value = "/nextAssignees")
    @ApiLog(title = "获取下一节点审批用户列表", operationType = OperationType.SEARCH)
    public HttpResult nextAssignees(@Validated @RequestBody WorkflowProcessVo workflowStartVo) {
        return HttpResult.success("操作成功", flowProcessService.nextAssignees(workflowStartVo));
    }

    /**
     * 获取已执行（可退回）审批节点列表
     */
    @PostMapping(value = "/preNodes")
    @ApiLog(title = "获取已执行（可退回）审批节点列表", operationType = OperationType.SEARCH)
    public HttpResult preNodes(@Validated @RequestBody WorkflowProcessVo workflowStartVo) {
        return HttpResult.success("操作成功", flowProcessService.preNodes(workflowStartVo));
    }

    /**
     * 取消流程
     */
    @PostMapping(value = "/cancelProcess")
    @ApiLog(title = "取消流程", operationType = OperationType.DELETE)
    public HttpResult cancelProcess(@Validated(Cancel.class) @RequestBody WorkflowProcessVo workflowStartVo) {
        return HttpResult.success("操作成功", flowProcessService.preNodes(workflowStartVo));
    }

}
