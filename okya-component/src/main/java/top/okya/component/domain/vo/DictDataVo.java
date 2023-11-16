package top.okya.component.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2023/11/7 15:05
 * @describe: 下拉数据源请求对象
 */

@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictDataVo {
    @NotBlank(message = "字典编码不能为空！")
    private String dictCode;
    private String selectedId;
    private List<String> selectedIds;
    @Builder.Default
    private String filterValue = "";
    private int pageNum;
    private int pageSize;
}
