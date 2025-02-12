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
 * @Date: 2025/2/11 10:43
 * @describe:
 */

@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictData {
    private String dictCode;
    private String dictValue;
    private String dictLabel;
    private String dictPid;
    private String dictStyle;
    private List<Map<String, Object>> data;
}
