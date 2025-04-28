package top.okya.component.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.domain.vo.vgroup.workflow.Publish;
import top.okya.component.domain.vo.vgroup.workflow.Save;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2025/4/15 22:24
 * @describe: 流程请求对象
 */

@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDesignVo {
    @NotBlank(message = "流程Id不能为空！", groups = Publish.class)
    private String flowId;
    @NotBlank(message = "流程编码不能为空！", groups = Save.class)
    private String flowCode;
    @NotBlank(message = "流程名称不能为空！", groups = Save.class)
    private String flowName;
    private Integer flowVersion;
    @NotEmpty(message = "流程节点不能为空！", groups = Save.class)
    private List<Map<String, Object>> nodes;
    @NotNull(message = "流程状态不能为空！", groups = Save.class)
    private Integer status;
    @NotNull(message = "创建方式不能为空！", groups = Save.class)
    private Boolean isNew;
    private String remark;
}
