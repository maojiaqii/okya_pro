package top.okya.component.enums;

/**
 * @author: maojiaqi
 * @Date: 2023/7/12 10:50
 * @describe: 安全协议异常（10001 + 3位编号）
 */

public enum SecurityExceptionType {

    // Content-Type不支持 异常
    UNSUPPORTED_HTTP_CONTENT_TYPE(10001001, "不支持的Http Content-Type：【%s】！"),
    // 签名为空 异常
    EMPTY_SIGNATURE(10001002, "签名不允许为空！"),
    // 时间戳为空 异常
    EMPTY_TIME_STAMP(10001003, "时间戳不允许为空！"),
    // 签名过期 异常
    SIGNATURE_EXPIRED(10001004, "签名已过期！"),
    // 签名验证不通过 异常
    VERIFICATION_SIGNATURE_FAILED(10001005, "签名验证不通过！"),
    // 重复提交 异常
    REPEAT_SUBMIT(10001006, "不允许重复提交，请稍候再试！"),
    // 未知错误 异常
    SERVER_EXCEPTION(10001999, "系统错误：【%s】");

    /**
     * 错误代码
     */
    private int code;
    /**
     * 中文描述
     */
    private String desc;

    /**
     * 私有构造,防止被外部调用
     *
     * @param desc
     */
    private SecurityExceptionType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
