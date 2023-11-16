package top.okya.system.domain;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 字典数据表(AsDictionaryData)实体类
 *
 * @author mjq
 * @since 2023-11-08 10:23:53
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsDictionaryData implements Serializable {
    private static final long serialVersionUID = -18491477307566401L;
    /**
    * 字典id
    */
    private Long dictId;
    /**
    * 字典编码
    */
    private String dictCode;
    /**
    * 字典实际值
    */
    private String id;
    /**
    * 字典显示值
    */
    private String label;
    /**
    * 字典显示值（英文）
    */
    private String labelEn;
    /**
    * 默认选中（0否 1是）
    */
    private Integer isDefault;
    /**
    * 创建人
    */
    private String createBy;
    /**
    * 创建时间
    */
    private Date createDate;
    /**
    * 更新人
    */
    private String updateBy;
    /**
    * 更新时间
    */
    private Date updateDate;

}
