package top.okya.component.global;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import top.okya.component.domain.LoginUser;

/**
 * @author: maojiaqi
 * @Date: 2023/11/29 21:29
 * @describe： 全局工具类
 */

@Component
public class Global {

    public static LoginUser getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof LoginUser) {
                return (LoginUser) principal;
            }
        }
        return null;
    }

}
