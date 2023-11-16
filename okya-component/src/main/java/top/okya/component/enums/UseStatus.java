package top.okya.component.enums;

/**
 * @author: maojiaqi
 * @Date: 2023/7/20 15:35
 * @describe: 用户状态
 */

public enum UseStatus {
    // 状态 正常
    OK(0, "正常"),
    // 状态 停用
    DISABLED(1, "停用"),
    // 状态 删除
    DELETED(2, "删除");

    private final int code;
    private final String describe;

    UseStatus(int code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public int getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }
}
