package top.okya.component.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2024/7/26 16:05
 * @describe: 表格数据类
 */

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableData {
    /**
     * 总条数
     */
    private long total;
    /**
     * 表格数据
     */
    private List<Map<String, Object>> list;
}
