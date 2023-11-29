package top.okya.component.global;

import org.springframework.security.core.context.SecurityContext;
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
        return (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
