package top.okya.framework.security.impl;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import top.okya.component.domain.HttpResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: maojiaqi
 * @Date: 2023/7/20 10:18
 * @describe: 认证失败处理类 返回未授权
 */

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        int code = HttpStatus.UNAUTHORIZED.value();
        String msg = "请求访问：" + request.getRequestURI() + "，认证失败，无法访问系统资源";
        log.error(msg);
        response.sendError(code, JSON.toJSONString(HttpResult.error(code, msg)));
    }
}
