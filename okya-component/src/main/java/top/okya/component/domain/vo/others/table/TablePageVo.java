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
public class TablePageVo {
    @NotBlank(message = "每页条数不能为空！")
    private int pageSize;
    @NotBlank(message = "页码不能为空！")
    private int currentPage;
}
