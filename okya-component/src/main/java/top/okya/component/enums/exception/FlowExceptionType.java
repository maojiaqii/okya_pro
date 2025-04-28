package top.okya.component.enums.exception;

/**
 * @author: maojiaqi
 * @Date: 2025/4/16 11:03
 * @describe: 流程相关异常（4000 + 4位编号）
 */

public enum FlowExceptionType {

    FLOW_DESIGN_ERROR(30001001, "流程保存失败：【%s】"),
    FLOW_VALIDATE_ERROR(30001002, "流程模型不合法：【%s】"),
    FLOW_PUBLISH_ERROR(30001003, "流程部署失败：【%s】"),
    FLOW_START_ERROR(30001004, "发起流程失败：【%s】"),
    FLOW_TEMPLATE_NOT_FOUND(30001005, "无法找到流程模版"),
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
    private FlowExceptionType(int code, String desc) {
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
