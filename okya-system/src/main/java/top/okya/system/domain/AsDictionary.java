package top.okya.system.domain;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.domain.dto.base.Basic;

/**
 * 字典表(AsDictionary)实体类
 *
 * @author mjq
 * @since 2023-11-08 10:22:49
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsDictionary extends Basic implements Serializable {
    private static final long serialVersionUID = 868778977724261631L;
    /**
    * 字典id
    */
    private String dictId;
    /**
    * 字典编码
    */
    private String dictCode;
    /**
    * 字典名称
    */
    private String dictName;
    /**
     * 下拉内容展示形式（list默认tree树形table表格）
     */
    private String showStyle;
    /**
    * 字典数据源
    */
    private String dictSource;
    /**
    * value字段名
    */
    private String dictValue;
    /**
    * label字段名
    */
    private String dictLabel;
    /**
     * parentId字段名
     */
    private String dictPid;

}
