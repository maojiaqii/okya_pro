package top.okya.framework.security.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import top.okya.system.dao.AsUserMapper;
import top.okya.component.domain.dto.AsUser;
import top.okya.component.constants.RedisConstants;
import top.okya.component.domain.LoginUser;
import top.okya.component.enums.exception.LoginExceptionType;
import top.okya.component.enums.UseStatus;
import top.okya.component.exception.LoginException;
import top.okya.component.utils.redis.JedisUtil;

import javax.annotation.Resource;

/**
 * @author: maojiaqi
 * @Date: 2023/7/19 14:35
 * @describe: 用户登录
 */

@Component
@Slf4j
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    AsUserMapper asUserMapper;

    @Resource
    private JedisUtil jedisUtil;

    @Value(value = "${security.password.maxRetryCount}")
    private int maxRetryCount;

    @Value(value = "${security.password.lockTime}")
    private int lockTime;

    @Override
    public UserDetails loadUserByUsername(String userCode) throws UsernameNotFoundException {
        log.info("开始用户登录校验：" + userCode);
        // 查询数据库用户表，获得用户信息
        AsUser asUser = asUserMapper.queryByUserCode(userCode);
        if (asUser == null) {
            throw new LoginException(LoginExceptionType.USER_NOT_FOUND, new Object[]{userCode}, userCode);
        } else if (asUser.getStatus() == UseStatus.DISABLED.getCode()) {
            throw new LoginException(LoginExceptionType.USER_DISABLED, new Object[]{userCode}, userCode);
        } else if (asUser.getStatus() == UseStatus.DELETED.getCode()) {
            throw new LoginException(LoginExceptionType.USER_DELETED, new Object[]{userCode}, userCode);
        }
        checkPwd(asUser);

        // 使用获得的信息创建SecurityUserDetails
        LoginUser user = new LoginUser(asUser.getUserCode(), asUser);
        return user;
    }

    private void checkPwd(AsUser asUser) {
        String userPwdErrKey = RedisConstants.PWD_ERR_CNT_KEY + asUser.getUserCode();
        String fc = jedisUtil.get(userPwdErrKey);
        int failCount = (fc == null ? 0 : Integer.parseInt(fc)) + 1;
        if (failCount > maxRetryCount) {
            throw new LoginException(LoginExceptionType.PASSWORD_RETRY_LIMIT_EXCEED, new Object[]{maxRetryCount, jedisUtil.getExpire(userPwdErrKey)}, asUser.getUserCode());
        }

        Authentication usernamePasswordAuthenticationToken = SecurityContextHolder.getContext().getAuthentication();
        String password = usernamePasswordAuthenticationToken.getCredentials().toString();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, asUser.getPassword())) {
            jedisUtil.set(userPwdErrKey, failCount, lockTime * 60 * 1000L);
            if (failCount == maxRetryCount) {
                throw new LoginException(LoginExceptionType.PASSWORD_RETRY_LIMIT_EXCEED, new Object[]{maxRetryCount, lockTime * 60}, asUser.getUserCode());
            }
            throw new LoginException(LoginExceptionType.PASSWORD_ERROR, new Object[]{maxRetryCount - failCount}, asUser.getUserCode());
        }
        jedisUtil.del(userPwdErrKey);
    }
}
