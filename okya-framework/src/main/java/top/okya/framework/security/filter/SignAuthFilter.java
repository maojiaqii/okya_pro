package top.okya.framework.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
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
import top.okya.component.enums.exception.SecurityExceptionType;
import top.okya.component.exception.SecurityException;
import top.okya.component.utils.common.DateFormatUtil;
import top.okya.component.utils.redis.JedisUtil;
import top.okya.framework.security.wrapper.DefaultRequestWrapper;
import top.okya.framework.security.wrapper.JsonRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author: maojiaqi
 * @Date: 2022/8/15 15:44
 * @describe: 防篡改、防重放攻击、防重复提交过滤器
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

    @Value("${security.sign.secretKey:#{null}}")
    private String secretKey;

    @Value("${security.sign.nonceHeader:#{null}}")
    private String nonceHeader;

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
        String method = request.getMethod();

        String requestUri = httpRequest.getRequestURI();
        log.info("当前请求的URI是==>{}", requestUri);
        for (String uri : ignoreUri) {
            if (uri.equals(requestUri)) {
                filterChain.doFilter(httpRequest, response);
                return;
            } else if (uri.endsWith("*")) {
                boolean flag = requestUri.startsWith(uri.substring(0, uri.length() - 1));
                if (flag) {
                    filterChain.doFilter(httpRequest, response);
                    return;
                }
            }
        }

        HttpServletRequestWrapper requestWrapper = null;
        String requestBody = "";
        // method认证
        if (Objects.equals(method, CommonConstants.HTTP_GET)) {
            requestWrapper = new DefaultRequestWrapper(httpRequest);
        } else if(Objects.equals(method, CommonConstants.HTTP_POST)) {
            // Content-Type认证
            if (ct.equals(CommonConstants.APPLICATION_JSON)) {
                JsonRequestWrapper jsonRequestWrapper = new JsonRequestWrapper(httpRequest);
                requestWrapper = jsonRequestWrapper;
                requestBody = jsonRequestWrapper.getRequestBody();
            } else {
                handlerExceptionResolver.resolveException(request, response, null, new SecurityException(SecurityExceptionType.UNSUPPORTED_HTTP_CONTENT_TYPE, ct));
                return;
            }
        } else {
            handlerExceptionResolver.resolveException(request, response, null, new SecurityException(SecurityExceptionType.UNSUPPORTED_HTTP_METHOD, method));
            return;
        }

        // 防重放认证
        String timestamp = requestWrapper.getHeader(timeHeader);
        if (timestamp == null) {
            handlerExceptionResolver.resolveException(request, response, null, new SecurityException(SecurityExceptionType.EMPTY_TIME_STAMP));
            return;
        }
        //重放时间限制（单位毫秒）
        Long difference = DateFormatUtil.currentMillis() - Long.valueOf(timestamp);
        if (difference > signTimeout) {
            handlerExceptionResolver.resolveException(request, response, null, new SecurityException(SecurityExceptionType.SIGNATURE_EXPIRED));
            return;
        }

        // 签名认证（仅POST）
        if(Objects.equals(method, CommonConstants.HTTP_POST)) {
            String sign = requestWrapper.getHeader(signHeader);
            String nonce = requestWrapper.getHeader(nonceHeader);
            if (StringUtils.isBlank(sign) || StringUtils.isBlank(nonce)) {
                handlerExceptionResolver.resolveException(request, response, null, new SecurityException(SecurityExceptionType.EMPTY_SIGNATURE));
                return;
            }
            // 2. 验证nonce是否重复（需要配合Redis）
            if (jedisUtil.hasKey(RedisConstants.NONCE_KEY + nonce)) {
                handlerExceptionResolver.resolveException(request, response, null, new SecurityException(SecurityExceptionType.REPEAT_SUBMIT));
                return;
            }
            jedisUtil.set(RedisConstants.NONCE_KEY + nonce, nonce, repeatMillis);

            // 3. 验证签名
            String signStr = timestamp + nonce + requestBody + secretKey;
            String serverSign = DigestUtils.sha256Hex(signStr);
            if (!serverSign.equals(sign)) {
                handlerExceptionResolver.resolveException(request, response, null, new SecurityException(SecurityExceptionType.VERIFICATION_SIGNATURE_FAILED));
                return;
            }
        }

        filterChain.doFilter(requestWrapper, response);
    }
}
