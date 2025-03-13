package top.okya.component.enums.exception;

/**
 * @author: maojiaqi
 * @Date: 2023/7/12 10:50
 * @describe: 登录相关异常（2000 + 4位编号）
 */

public enum LoginExceptionType {

    // 用户不存在 异常
    USER_NOT_FOUND(20001001, "用户【%s】不存在！"),
    // 用户状态 异常
    USER_DISABLED(20001002, "用户【%s】已被停用！"),
    // 用户状态 异常
    USER_DELETED(20001003, "用户【%s】已被删除！"),
    // 密码 异常
    PASSWORD_RETRY_LIMIT_EXCEED(20001004, "密码错误超过【%s】次，已被锁定，请在【%s】秒之后重试！"),
    // 密码 异常
    PASSWORD_ERROR(20001005, "密码错误，剩余尝试次数：【%s】次！"),
    // 验证码 异常
    CAPTCHA_ERROR(20001006, "验证码错误！"),
    CAPTCHA_EMPTY(20001007, "验证码不允许为空！"),
    UNLOGIN(20001008, "未登录！"),
    // 未知错误 异常
    SERVER_EXCEPTION(20009999, "系统错误：【%s】");

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
    private LoginExceptionType(int code, String desc) {
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
