package top.okya.component.domain.vo;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.domain.vo.others.table.TableOrderVo;
import top.okya.component.domain.vo.others.table.TablePageVo;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2023/11/7 15:05
 * @describe: 表单数据请求对象
 */

@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDataVo {
    @NotBlank(message = "表单代码不能为空！")
    private String formCode;
    @Builder.Default
    private List<Map<String, Object>> data = Lists.newArrayList();
}
