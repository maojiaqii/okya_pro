package top.okya.component.utils.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author: maojiaqi
 * @Date: 2023/7/26 14:47
 * @describe: 安全加密工具类
 */

public class SecureUtil {

    private static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static String encode(CharSequence rawString){
        return bCryptPasswordEncoder.encode(rawString);
    }

    public static boolean matches(CharSequence rawString, String encodeString){
        return bCryptPasswordEncoder.matches(rawString, encodeString);
    }

}
