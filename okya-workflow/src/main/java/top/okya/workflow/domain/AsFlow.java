package top.okya.workflow.domain;
 
import java.io.Serializable;

import com.alibaba.fastjson2.JSONArray;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.domain.dto.base.Basic;

/**
 * 流程设计表(AsFlow)实体类
 *
 * @author makejava
 * @since 2025-04-14 23:08:49
 */

@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsFlow extends Basic implements Serializable {
    private static final long serialVersionUID = 702194291299222930L;
    /**
     * 流程id
     */ 
    private String flowId;
    /**
     * 流程代码
     */ 
    private String flowCode;
    /**
     * 流程名称
     */ 
    private String flowName;
    /**
     * 流程版本
     */ 
    private Integer flowVersion;
    /**
     * 流程节点信息
     */ 
    private JSONArray flowNodes;
    /**
     * 流程发布状态
     */
    private Integer published;
    /**
     * 流程部署Id
     */
    private String deploymentId;
}
