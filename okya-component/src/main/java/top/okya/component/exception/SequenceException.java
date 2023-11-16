package top.okya.component.exception;

import lombok.extern.slf4j.Slf4j;
import top.okya.component.enums.exception.SequenceExceptionType;
import top.okya.component.exception.base.BaseException;

/**
 * @author: maojiaqi
 * @Date: 2023/6/27 16:43
 * @describe： 自定义序列异常
 */

@Slf4j
public class SequenceException extends BaseException {

    public SequenceException(SequenceExceptionType sequenceExceptionType) {
        super("序列生成器", sequenceExceptionType.getCode(), sequenceExceptionType.getDesc());
    }

    public SequenceException(SequenceExceptionType sequenceExceptionType, Object... args) {
        super("序列生成器", sequenceExceptionType.getCode(), sequenceExceptionType.getDesc(), args);
    }

    public SequenceException(SequenceExceptionType sequenceExceptionType, Object[] args, Object data) {
        super("序列生成器", sequenceExceptionType.getCode(), sequenceExceptionType.getDesc(), args, data);
    }
}
