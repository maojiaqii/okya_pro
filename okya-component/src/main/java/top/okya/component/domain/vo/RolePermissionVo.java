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
 * @Date: 2025/4/10 10:05
 * @describe: 角色权限请求对象
 */

@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionVo {
    @NotBlank(message = "角色Id不能为空！")
    private String roleId;
    private List<String> roleTenancy;
    private List<String> rolePermission;
    private List<String> roleButton;
    private List<String> roleTable;
    private List<String> roleForm;
}
