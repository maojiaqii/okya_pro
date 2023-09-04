package top.okya.component.constants;

/**
 * @author: maojiaqi
 * @Date: 2023/7/22 10:32
 * @describe: 通用常量
 */

public class CommonConstants {

    public static final int SUCCESS = 0;

    public static final int FAIL = 1;

    public static final String UNKNOWN = "UNKNOWN";

    /**
     * 无需鉴权用户标志
     */
    public static final String ANONYMOUS_USER = "anonymousUser";

    /**
     * 不使用验证码功能常量
     */
    public static final String DISABLED_CAPTCHA = "NONE";

    /**
     * 数字计算类型验证码常量
     */
    public static final String MATH_CAPTCHA = "MATH";

    /**
     * 字符类型验证码常量
     */
    public static final String CHAR_CAPTCHA = "CHAR";

    /**
     * 验证码base64编码前缀
     */
    public static final String CAPTCHA_TO_BASE64_PREFIX = "data:image/jpeg;base64,";

    /**
     * 支持的Content-Type
     */
    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    /**
     * 支持的Content-Type
     */
    public static final String APPLICATION_JSON = "application/json";

    public static final String HTTP_POST = "POST";

    public static final String HTTP_GET = "GET";
}
