package top.okya.component.exception;

import lombok.extern.slf4j.Slf4j;
import top.okya.component.enums.SecurityExceptionType;
import top.okya.component.exception.base.BaseException;

/**
 * @author: maojiaqi
 * @Date: 2023/6/27 16:43
 * @describe： 自定义服务异常
 */

@Slf4j
public class SecurityException extends BaseException {

    public SecurityException(SecurityExceptionType securityExceptionType) {
        super("安全协议", securityExceptionType.getCode(), securityExceptionType.getDesc());
    }

    public SecurityException(SecurityExceptionType securityExceptionType, Object... args) {
        super("安全协议", securityExceptionType.getCode(), securityExceptionType.getDesc(), args);
    }

    public SecurityException(SecurityExceptionType securityExceptionType, Object[] args, Object data) {
        super("安全协议", securityExceptionType.getCode(), securityExceptionType.getDesc(), args, data);
    }

}
