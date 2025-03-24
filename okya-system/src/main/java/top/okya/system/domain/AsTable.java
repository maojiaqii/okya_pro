package top.okya.system.domain;

import java.util.Date;
import java.io.Serializable;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 表格表(AsTable)实体类
 *
 * @author makejava
 * @since 2024-12-06 22:29:58
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsTable implements Serializable {
    private static final long serialVersionUID = 869958808508298009L;
    /**
     * 表格id
     */
    private String tableId;
    /**
     * 表格编码
     */
    private String tableCode;
    /**
     * 表格名称
     */
    private String tableName;
    /**
     * 表格数据源
     */
    private String tableSource;
    /**
     * 按钮信息
     */
    private JSONArray buttons;
    /**
     * 表格属性
     */
    private JSONObject props;
    /**
     * 表格属性
     */
    private JSONArray columns;
    /**
     * 表格状态（0正常 1停用）
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
