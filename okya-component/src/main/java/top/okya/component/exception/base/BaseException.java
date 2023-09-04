package top.okya.component.exception.base;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import top.okya.component.enums.SequenceExceptionType;

/**
 * @author: maojiaqi
 * @Date: 2023/6/27 16:43
 * @describe： 自定义序列异常
 */

@Slf4j
public class BaseException extends RuntimeException {

    private int code;

    private String msg;

    private Object data;

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }

    public BaseException(String module, int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
        log.error("【{}】异常--------------> 错误码：{}，错误描述：{}", module, this.code, this.msg);
    }

    public BaseException(String module, int code, String msg, Object... args) {
        super(String.format(msg, args));
        this.code = code;
        this.msg = String.format(msg, args);
        log.error("【{}】异常--------------> 错误码：{}，错误描述：{}", module, this.code, this.msg);
    }

    public BaseException(String module, int code, String msg, Object[] args, Object data) {
        super(String.format(msg, args));
        this.code = code;
        this.msg = String.format(msg, args);
        this.data = data;
        log.error("【{}】异常--------------> 错误码：{}，错误描述：{}，返回数据：{}", module, this.code, this.msg, JSON.toJSONString(this.data));
    }
}
