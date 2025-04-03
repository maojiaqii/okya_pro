package top.okya.component.domain.child;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2025/4/3 17:26
 * @describe: 路由自定义属性中的权限类
 */

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionMetaPermis {
    /**
     * 按钮权限标记
     */
    private List<String> buttons;
    /**
     * 表格列权限标记
     */
    private List<String> columns;
    /**
     * 表单字段权限标记
     */
    private List<String> fields;
}
