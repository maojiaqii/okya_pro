package top.okya.framework.security.impl;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import top.okya.component.constants.LoginConstants;
import top.okya.component.domain.HttpResult;
import top.okya.component.domain.LoginUser;
import top.okya.component.utils.security.JwtUtil;
import top.okya.system.async.AsyncService;
import top.okya.system.domain.AsLoginRecord;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: maojiaqi
 * @Date: 2023/7/20 11:25
 * @describe: 退出登录处理类
 */
@Configuration
@Slf4j
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    /**
     * 退出处理
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        LoginUser loginUser = JwtUtil.getUserInfo(JwtUtil.getToken(request));
        if (loginUser != null) {
            String userCode = loginUser.getUserCode();
            // 删除用户缓存记录
            JwtUtil.delLoginUser(loginUser.getId());
            log.info("用户：{}登出成功！", userCode);
            AsLoginRecord asLoginRecord = new AsLoginRecord()
                    .setUserCode(userCode)
                    .setStatus(LoginConstants.LOGOUT_SUCCESS)
                    .setMsg("登出成功！");
            // 记录用户退出日志
            AsyncService.me().insertLoginRecord(asLoginRecord);
        }
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(JSON.toJSONString(HttpResult.success("退出成功")));
    }
}
