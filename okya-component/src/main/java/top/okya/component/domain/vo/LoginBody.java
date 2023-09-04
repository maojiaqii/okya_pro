package top.okya.component.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author: maojiaqi
 * @Date: 2023/7/21 17:08
 * @describe: 登录接口请求对象
 */

@Accessors(chain = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginBody {
    @Builder.Default
    private String uuid = "";
    @NotBlank(message = "用户名不能为空")
    private String userCode;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;
}
