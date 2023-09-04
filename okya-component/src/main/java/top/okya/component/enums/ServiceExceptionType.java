package top.okya.component.enums;

/**
 * @author: maojiaqi
 * @Date: 2023/7/12 10:50
 * @describe: 业务异常（3000 + 4位编号）
 */

public enum ServiceExceptionType {

    // 验证码 异常
    CAPTCHA_CREATE_ERROR(30001001, "验证码生成失败！"),
    // 未知错误 异常
    SERVER_EXCEPTION(30009999, "系统错误：【%s】");

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
    private ServiceExceptionType(int code, String desc) {
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
