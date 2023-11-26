package top.okya.component.domain;

import com.alibaba.fastjson2.JSON;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Objects;

/**
 * 操作消息提醒
 *
 * @author mao
 */
public class HttpResult extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    public static final String CODE_TAG = "code";

    /**
     * 返回内容
     */
    public static final String MSG_TAG = "msg";

    /**
     * 数据对象
     */
    public static final String DATA_TAG = "data";

    /**
     * 初始化一个新创建的 HttpResult 对象，使其表示一个空消息。
     */
    public HttpResult() {
    }

    /**
     * 初始化一个新创建的 HttpResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public HttpResult(int code, String msg) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 HttpResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public HttpResult(int code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (data != null) {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static HttpResult success() {
        return HttpResult.success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static HttpResult success(Object data) {
        return HttpResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static HttpResult success(String msg) {
        return HttpResult.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static HttpResult success(String msg, Object data) {
        return new HttpResult(HttpStatus.OK.value(), msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return 错误消息
     */
    public static HttpResult error() {
        return HttpResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 错误消息
     */
    public static HttpResult error(String msg) {
        return HttpResult.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 错误消息
     */
    public static HttpResult error(String msg, Object data) {
        return new HttpResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 错误消息
     */
    public static HttpResult error(int code, String msg) {
        return new HttpResult(code, msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     * @return 错误消息
     */
    public static HttpResult error(int code, String msg, Object data) {
        return new HttpResult(code, msg, data);
    }

    /**
     * 是否为成功消息
     *
     * @return 结果
     */
    public boolean isSuccess() {
        return Objects.equals(HttpStatus.OK.value(), this.get(CODE_TAG));
    }

    /**
     * 是否为错误消息
     *
     * @return 结果
     */
    public boolean isError() {
        return Objects.equals(HttpStatus.INTERNAL_SERVER_ERROR.value(), this.get(CODE_TAG));
    }

    /**
     * 方便链式调用
     *
     * @param key   键
     * @param value 值
     * @return 数据对象
     */
    @Override
    public HttpResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
