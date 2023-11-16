package top.okya.system.domain;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 字典表(AsDictionary)实体类
 *
 * @author mjq
 * @since 2023-11-08 10:22:49
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsDictionary implements Serializable {
    private static final long serialVersionUID = 868778977724261631L;
    /**
    * 字典id
    */
    private Long dictId;
    /**
    * 字典编码
    */
    private String dictCode;
    /**
    * 字典名称
    */
    private String dictName;
    /**
    * 字典类型（0静态列表1动态数据库表格）
    */
    private Integer dictType;
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
    * abel字段名
    */
    private String dictLabel;
    /**
     * 查询条件
     */
    private String dictParams;
    /**
    * 字典过滤搜索功能对应脚本
    */
    private String filterSqlPrefix;
    /**
    * 字典状态（0正常 1停用 2删除）
    */
    private Integer status;
    /**
    * 创建者
    */
    private String createBy;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 更新者
    */
    private String updateBy;
    /**
    * 更新时间
    */
    private Date updateTime;

}
