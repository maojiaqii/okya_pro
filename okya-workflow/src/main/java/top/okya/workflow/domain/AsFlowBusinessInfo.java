package top.okya.workflow.domain;
 
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.domain.dto.base.Basic;

/**
 * 业务流程关联信息表(AsFlowBusinessInfo)实体类
 *
 * @author makejava
 * @since 2025-05-06 16:05:23
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsFlowBusinessInfo extends Basic implements Serializable {
    private static final long serialVersionUID = 289620450964033583L;
    /**
     * 数据唯一id
     */ 
    private String infoId;
    /**
     * 业务数据物理表名
     */ 
    private String businessTable;
    /**
     * 业务数据id
     */ 
    private String businessId;
    /**
     * 流程编码
     */ 
    private String flowCode;
    /**
     * 流程版本
     */ 
    private Integer flowVersion;
    /**
     * 流程业务id
     */ 
    private String flowBusinessKey;
    /**
     * 流程实例id
     */ 
    private String procInstId;
    /**
     * 流程当前所在节点id
     */ 
    private String flowCurrentNodeId;
    /**
     * 流程当前所在节点名称
     */ 
    private String flowCurrentNodeName;
    /**
     * 发起人
     */ 
    private String startUserId;
    /**
     * 发起人所属部门
     */ 
    private String startDeptId;
    /**
     * 流程开始时间
     */ 
    private Date flowStartTime;
    /**
     * 流程完结时间
     */ 
    private Date flowEndTime;
}
