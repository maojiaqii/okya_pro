package top.okya.component.domain.vo.others.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author: maojiaqi
 * @Date: 2023/11/7 15:05
 * @describe: 表格数据请求对象
 */

@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableQueryVo {
    @NotBlank(message = "表格查询字段名不能为空！")
    private String col;
    @NotBlank(message = "表格查询字段值不能为空！")
    private Object val;
}
