package top.okya.component.enums;

/**
 * @author: maojiaqi
 * @Date: 2023/7/20 15:35
 * @describe: 用户状态
 */

public enum UserStatus {
    // 用户状态 正常
    OK("0", "正常"),
    // 用户状态 停用
    DISABLED("1", "停用"),
    // 用户状态 删除
    DELETED("2", "删除");

    private final String code;
    private final String describe;

    UserStatus(String code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public String getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }
}
