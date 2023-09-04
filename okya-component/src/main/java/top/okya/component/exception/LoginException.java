package top.okya.component.exception;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import top.okya.component.enums.LoginExceptionType;
import top.okya.component.exception.base.BaseException;

/**
 * @author: maojiaqi
 * @Date: 2023/6/27 16:43
 * @describe： 自定义登录异常
 */

@Slf4j
public class LoginException extends InternalAuthenticationServiceException {

    private int code;

    private String msg;

    private String userCode;

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public String getUserCode() {
        return userCode;
    }

    public LoginException(LoginExceptionType loginExceptionType, String userCode) {
        super(loginExceptionType.getDesc());
        this.code = loginExceptionType.getCode();
        this.msg = loginExceptionType.getDesc();
        this.userCode = userCode;
        log.error("【登录】异常--------------> 错误码：{}，错误描述：{}，登录账号：{}", this.code, this.msg, this.userCode);
    }

    public LoginException(LoginExceptionType loginExceptionType, Object[] args, String userCode) {
        super(String.format(loginExceptionType.getDesc(), args));
        this.code = loginExceptionType.getCode();
        this.msg = String.format(loginExceptionType.getDesc(), args);
        this.userCode = userCode;
        log.error("【登录】异常--------------> 错误码：{}，错误描述：{}，登录账号：{}", this.code, this.msg, this.userCode);
    }
}
