package top.okya.component.exception;

import lombok.extern.slf4j.Slf4j;
import top.okya.component.enums.exception.ServiceExceptionType;
import top.okya.component.exception.base.BaseException;

/**
 * @author: maojiaqi
 * @Date: 2023/6/27 16:43
 * @describe: 自定义服务异常
 */

@Slf4j
public class ServiceException extends BaseException {

    public ServiceException(ServiceExceptionType serviceExceptionType) {
        super("业务", serviceExceptionType.getCode(), serviceExceptionType.getDesc());
    }

    public ServiceException(ServiceExceptionType serviceExceptionType, Object... args) {
        super("业务", serviceExceptionType.getCode(), serviceExceptionType.getDesc(), args);
    }

    public ServiceException(ServiceExceptionType serviceExceptionType, Object[] args, Object data) {
        super("业务", serviceExceptionType.getCode(), serviceExceptionType.getDesc(), args, data);
    }
}
