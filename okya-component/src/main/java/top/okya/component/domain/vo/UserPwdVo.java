package top.okya.component.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author: maojiaqi
 * @Date: 2025/2/12 15:45
 * @describe: 用户重置密码请求对象
 */

@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPwdVo {
    private String userId;
    @NotBlank(message = "密码不能为空！")
    private String password;
}
