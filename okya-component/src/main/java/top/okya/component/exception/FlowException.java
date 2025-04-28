package top.okya.component.exception;

import lombok.extern.slf4j.Slf4j;
import top.okya.component.enums.exception.FlowExceptionType;
import top.okya.component.exception.base.BaseException;

/**
 * @author: maojiaqi
 * @Date: 2023/6/27 16:43
 * @describe: 流程服务异常
 */

@Slf4j
public class FlowException extends BaseException {

    public FlowException(FlowExceptionType serviceExceptionType) {
        super("流程", serviceExceptionType.getCode(), serviceExceptionType.getDesc());
    }

    public FlowException(FlowExceptionType serviceExceptionType, Object... args) {
        super("流程", serviceExceptionType.getCode(), serviceExceptionType.getDesc(), args);
    }

    public FlowException(FlowExceptionType serviceExceptionType, Object[] args, Object data) {
        super("流程", serviceExceptionType.getCode(), serviceExceptionType.getDesc(), args, data);
    }
}
