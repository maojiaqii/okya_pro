package top.okya.component.enums.exception;

/**
 * @author: maojiaqi
 * @Date: 2023/7/12 10:50
 * @describe: 业务异常（3000 + 4位编号）
 */

public enum ServiceExceptionType {

    // 验证码 异常
    CAPTCHA_CREATE_ERROR(30001001, "验证码生成失败！"),
    DISABLED_CAPTCHA(30001002, "未启用验证码功能，获取失败！"),
    DISABLED(30001101, "【%s】已被停用！"),
    DELETED(30001102, "【%s】已被删除！"),
    UNKNOWN(30001103, "未知的【%s】，编码：【%s】！"),
    GENERATE_FILE_FAILED(30001201, "在磁盘【%s】上生成分片文件失败！"),
    MERGE_FILE_FAILED(30001202, "在磁盘【%s】上合并文件失败！"),
    GET_FILE_INFO_FAILED(30001203, "获取文件信息失败！"),
    FILE_NOT_EXISTS(30001204, "文件不存在！"),
    FILE_IO_ERROR(30001204, "文件IO异常！"),
    INVALID_CHUNK_NAME(30001205, "无效的分片文件名:【%s】！"),
    DESIGN_ERROR(30001301, "【%s】配置错误：【%s】！"),
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
