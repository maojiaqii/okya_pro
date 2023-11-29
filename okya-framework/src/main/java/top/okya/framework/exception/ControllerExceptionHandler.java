package top.okya.framework.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.okya.component.constants.LoginConstants;
import top.okya.component.domain.HttpResult;
import top.okya.component.exception.LoginException;
import top.okya.component.exception.base.BaseException;
import top.okya.component.utils.common.DateFormatUtil;
import top.okya.system.async.AsyncService;
import top.okya.system.domain.AsLoginRecord;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: maojiaqi
 * @Date: 2023/6/27 16:43
 * @describe: controller异常处理器
 */

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    /**
     * 登录异常
     */
    @ExceptionHandler(LoginException.class)
    public HttpResult handleLoginException(LoginException e) {
        log.error(e.getMsg(), e);
        String userCode = e.getUserCode();
        AsLoginRecord asLoginRecord = new AsLoginRecord()
                .setUserCode(userCode)
                .setLoginTime(DateFormatUtil.nowDate())
                .setStatus(LoginConstants.LOGIN_FAIL)
                .setMsg(e.getMsg());
        AsyncService.me().insertLoginRecord(asLoginRecord);
        return HttpResult.error(e.getCode(), e.getMsg());
    }

    /**
     * 自定义异常
     */
    @ExceptionHandler(BaseException.class)
    public HttpResult handleServiceException(BaseException e) {
        log.error(e.getMsg(), e);
        Object data = e.getData();
        return data == null ? HttpResult.error(e.getCode(), e.getMsg()) : HttpResult.error(e.getCode(), e.getMsg(), data);
    }

    /**
     * 请求参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HttpResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return HttpResult.error(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public HttpResult handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        return HttpResult.error(e.getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public HttpResult handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestUri, e);
        return HttpResult.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public HttpResult handleException(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestUri, e);
        return HttpResult.error(e.getMessage());
    }

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public HttpResult handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestUri, e.getMessage());
        return HttpResult.error(HttpStatus.FORBIDDEN.value(), "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public HttpResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestUri, e.getMethod());
        return HttpResult.error(e.getMessage());
    }

}
