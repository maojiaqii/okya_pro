package top.okya.component.domain.vo.others.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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
public class TableOrderVo {
    @NotBlank(message = "表格排序字段名不能为空！")
    private String col;
    @Builder.Default
    @NotBlank(message = "表格排序方式不能为空！")
    @Pattern(regexp="^(asc|desc)$", message = "表格排序方式只能是asc或desc！")
    private String order = "asc";
}
