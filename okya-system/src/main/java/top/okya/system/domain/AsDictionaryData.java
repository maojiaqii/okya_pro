package top.okya.system.domain;

import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.domain.dto.base.Basic;

/**
 * 字典数据表(AsDictionaryData)实体类
 *
 * @author mjq
 * @since 2023-11-08 10:23:53
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsDictionaryData extends Basic implements Serializable {
    private static final long serialVersionUID = -18491477307566401L;
    /**
    * 字典id
    */
    private String dictId;
    /**
     * 父级id
     */
    private String pId;
    /**
    * 字典编码
    */
    private String dictCode;
    /**
    * 字典实际值
    */
    private String value;
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

}
