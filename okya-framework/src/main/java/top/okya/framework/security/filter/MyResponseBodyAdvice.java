package top.okya.framework.security.filter;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import top.okya.component.domain.HttpResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author: maojiaqi
 * @Date: 2023/9/22 16:04
 * @describe: 为了适配客户端应用，token额外添加在返回消息体里
 */

@RestControllerAdvice
public class MyResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    //判断是否要执行beforeBodyWrite方法，true为执行，false不执行
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    //对response处理的执行方法
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpResult hr = (HttpResult) body;
        List<String> token = response.getHeaders().get("Authorization");
        if(!Optional.ofNullable(token).orElse(Collections.emptyList()).isEmpty()){
            hr.put("token", token.get(0));
        }
        return hr;
    }

}
