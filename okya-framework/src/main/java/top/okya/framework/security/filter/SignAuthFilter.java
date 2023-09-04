package top.okya.framework.security.filter;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import top.okya.component.constants.CommonConstants;
import top.okya.component.constants.RedisConstants;
import top.okya.component.enums.SecurityExceptionType;
import top.okya.component.exception.SecurityException;
import top.okya.component.utils.common.DateFormatUtil;
import top.okya.component.utils.redis.JedisUtil;
import top.okya.component.utils.security.SecureUtil;
import top.okya.framework.security.wrapper.DefaultRequestWrapper;
import top.okya.framework.security.wrapper.JsonRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author: maojiaqi
 * @Date: 2022/8/15 15:44
 * @describe： 防篡改、防重放攻击、防重复提交过滤器
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "security.sign")
public class SignAuthFilter extends OncePerRequestFilter {

    public void setIgnoreUri(List<String> ignoreUri) {
        this.ignoreUri = ignoreUri;
    }

    private List<String> ignoreUri;

    @Value("${security.sign.timeout:#{null}}")
    private Long signTimeout;

    @Value("${security.sign.signHeader:#{null}}")
    private String signHeader;

    @Value("${security.sign.timeHeader:#{null}}")
    private String timeHeader;

    @Value("${security.token.header:#{null}}")
    private String tokenHeader;

    @Value("${security.sign.repeatMillis:#{null}}")
    private Long repeatMillis;

    @Autowired
    private JedisUtil jedisUtil;

    @Lazy
    @Autowired
    HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 防止流读取一次后就没有了, 所以需要将流继续写出去
        HttpServletRequest httpRequest = request;
        String ct = request.getContentType();

        HttpServletRequestWrapper requestWrapper = null;
        String requestBody = "";
        if (ct == null || ct.equals(CommonConstants.APPLICATION_X_WWW_FORM_URLENCODED)) {
            requestWrapper = new DefaultRequestWrapper(httpRequest);
            Map<String, String[]> pm = requestWrapper.getParameterMap();
            Map<String, String> pm1 = new LinkedHashMap<>();
            for (Map.Entry<String, String[]> entry : pm.entrySet()) {
                pm1.put(entry.getKey(), entry.getValue()[0]);
            }
            requestBody = JSON.toJSONString(pm1);
        } else if (ct.equals(CommonConstants.APPLICATION_JSON)) {
            JsonRequestWrapper jsonRequestWrapper = new JsonRequestWrapper(httpRequest);
            requestWrapper = jsonRequestWrapper;
            requestBody = jsonRequestWrapper.getRequestBody();
        } else {
            handlerExceptionResolver.resolveException(request, response, null, new SecurityException(SecurityExceptionType.UNSUPPORTED_HTTP_CONTENT_TYPE, ct));
            return;
        }
        log.info("Content-Type认证通过！");

        String requestUri = httpRequest.getRequestURI();
        log.info("当前请求的URI是==>{}", requestUri);
        for (String uri : ignoreUri) {
            if (uri.equals(requestUri)) {
                filterChain.doFilter(requestWrapper, response);
                return;
            } else if (uri.endsWith("*")) {
                boolean flag = requestUri.startsWith(uri.substring(0, uri.length() - 1));
                if (flag) {
                    filterChain.doFilter(requestWrapper, response);
                    return;
                }
            }
        }

        String sign = requestWrapper.getHeader(signHeader);
        String timestamp = requestWrapper.getHeader(timeHeader);
        if (StringUtils.isBlank(sign)) {
            handlerExceptionResolver.resolveException(request, response, null, new SecurityException(SecurityExceptionType.EMPTY_SIGNATURE));
            return;
        }
        if (timestamp == null) {
            handlerExceptionResolver.resolveException(request, response, null, new SecurityException(SecurityExceptionType.EMPTY_TIME_STAMP));
            return;
        }
        //重放时间限制（单位毫秒）
        Long difference = DateFormatUtil.currentMillis() - Long.valueOf(timestamp);
        if (difference > signTimeout) {
            log.info("前端时间戳：{},服务端时间戳：{}", timestamp, DateFormatUtil.currentMillis());
            handlerExceptionResolver.resolveException(request, response, null, new SecurityException(SecurityExceptionType.SIGNATURE_EXPIRED));
            return;
        }
        log.info("防重放认证通过！");

        if (!SecureUtil.matches(timestamp + requestBody, sign)) {
            handlerExceptionResolver.resolveException(request, response, null, new SecurityException(SecurityExceptionType.VERIFICATION_SIGNATURE_FAILED));
            return;
        }
        log.info("签名认证通过！");

        String cacheRepeatKey = RedisConstants.REPEAT_SUBMIT_KEY + requestUri + ":" + Optional.ofNullable(request.getHeader(tokenHeader)).orElse("");
        String cacheObj = jedisUtil.get(cacheRepeatKey);
        if (Objects.equals(cacheObj, requestBody)) {
            handlerExceptionResolver.resolveException(request, response, null, new SecurityException(SecurityExceptionType.REPEAT_SUBMIT));
            return;
        }
        jedisUtil.set(cacheRepeatKey, requestBody, repeatMillis);
        log.info("防重复提交认证通过！");

        filterChain.doFilter(requestWrapper, response);
    }
}
