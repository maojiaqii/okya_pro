package top.okya.component.enums;

/**
 * @author: maojiaqi
 * @Date: 2023/7/12 10:50
 * @describe: 序列器异常（70001 + 3位编号）
 */

public enum SequenceExceptionType {

    // 序列规则未配置 异常
    UNKNOWN_KEY(70001001, "未配置【%s】序列规则！"),
    // 序列规则逻辑 异常
    MISSING_LENGTH(70001002, "未配置序列【长度】！"),
    // 序列规则逻辑 异常
    ILLEGAL_LENGTH(70001003, "超过序列规则中允许的最大长度！"),
    // 序列规则逻辑 异常
    ILLEGAL_RANGE(70001004, "序列【最大值：%s】必须大于【最小值：%s】！"),
    // 序列规则逻辑 异常
    ILLEGAL_STEP(70001005, "序列【步长】不匹配！"),
    // 序列规则逻辑 异常
    OUT_OF_RANGE(70001006, "超出序列规则中允许的最大值【%s】，最小值【%s】！"),
    // 获取序列超时 异常
    TIME_OUT(70001007, "获取序列超时失败！"),
    // 未知错误 异常
    SERVER_EXCEPTION(70001999, "系统错误：【%s】");

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
    private SequenceExceptionType(int code, String desc) {
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
