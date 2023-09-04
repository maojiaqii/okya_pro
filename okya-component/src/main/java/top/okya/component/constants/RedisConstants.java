package top.okya.component.constants;

/**
 * @author: maojiaqi
 * @Date: 2022/5/13 11:44
 * @describe： 缓存的key 常量
 */

public class RedisConstants {
    /**
     * 序列
     */
    public static final String SEQUENCE_KEY_PREFIX = "sequences:list:";

    /**
     * 序列锁
     */
    public static final String LOCK_KEY_PREFIX = "sequences:lock:";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login:users:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "login:pwd_err_cnt:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "login:captcha_codes:";

    /**
     * 验证码有效期（1分钟）
     */
    public static final Long CAPTCHA_EXPIRATION = 1000L * 60;

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

}
