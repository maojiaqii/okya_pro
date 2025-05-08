package top.okya.system.domain;

import java.util.Date;
import java.io.Serializable;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.domain.dto.base.Basic;

/**
 * 表格表(AsTable)实体类
 *
 * @author makejava
 * @since 2024-12-06 22:29:58
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsTable extends Basic implements Serializable {
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
     * 流程表格类型
     */
    private Integer workflowType;
}
