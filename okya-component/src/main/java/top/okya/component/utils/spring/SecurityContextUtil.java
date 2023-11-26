package top.okya.component.utils.spring;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import top.okya.component.domain.LoginUser;

/**
 * @author: maojiaqi
 * @Date: 2023/11/26 21:29
 * @describe： SecurityContext工具类
 */

@Component
public class SecurityContextUtil {

    public static SecurityContext getSecurityContext(){
        return SecurityContextHolder.getContext();
    }

    public static LoginUser getLoginUser(){
        return (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
