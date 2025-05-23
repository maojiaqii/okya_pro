package top.okya.component.domain.vo;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.domain.vo.vgroup.workflow.Cancel;
import top.okya.component.domain.vo.vgroup.workflow.Run;
import top.okya.component.domain.vo.vgroup.workflow.Start;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2025/4/17 22:27
 * @describe: 流程启动请求对象
 */

@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowProcessVo {
    /**
     * 流程编码
     */
    @NotBlank(message = "流程编码不能为空！", groups = {Start.class})
    private String flowCode;

    /**
     * 业务主键
     */
    @NotBlank(message = "业务主键不能为空！", groups = {Start.class})
    private String flowBusinessKey;
    
    /**
     * 表单数据
     */
    @NotNull(message = "表单数据不能为空！", groups = {Start.class})
    private JSONObject formData;

    /**
     * 流程实例Id
     */
    @NotBlank(message = "流程实例Id不能为空！", groups = {Run.class, Cancel.class})
    private String processInstanceId;

    /**
     * 任务Id
     */
    @NotBlank(message = "任务Id不能为空！", groups = {Run.class})
    private String taskId;

    /**
     * 审核类型
     */
    @NotBlank(message = "审核类型不能为空！", groups = {Run.class})
    private String checkType;

    /**
     * 审核成员
     */
    @Builder.Default
    private List<String> checkers = new ArrayList<>();

    /**
     * 退回节点
     */
    @Builder.Default
    private String backNode = "";

    /**
     * 审核意见
     */
    private String comment;

} 