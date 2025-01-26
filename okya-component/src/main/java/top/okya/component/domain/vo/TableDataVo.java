package top.okya.component.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.domain.vo.others.table.TableOrderVo;
import top.okya.component.domain.vo.others.table.TablePageVo;
import top.okya.component.domain.vo.others.table.TableQueryVo;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class TableDataVo {
    @NotBlank(message = "表格编码不能为空！")
    private String tableCode;
    @Builder.Default
    private Map<String, Object> params = new HashMap<>();
    private TablePageVo page;
    private List<TableOrderVo> order;
}
