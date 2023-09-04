package top.okya.component.utils.common;

import java.util.UUID;

/**
 * @author: maojiaqi
 * @Date: 2023/7/26 9:32
 * @describe:
 */

public class IdUtil {

    /**
     * 生成唯一编码
     * @return
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成简单唯一编码（不含-）
     * @return
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString().replace("\\-", "");
    }

}
