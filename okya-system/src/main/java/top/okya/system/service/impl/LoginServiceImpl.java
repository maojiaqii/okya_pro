package top.okya.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import top.okya.component.config.OkyaConfig;
import top.okya.component.constants.CommonConstants;
import top.okya.component.constants.LoginConstants;
import top.okya.component.constants.RedisConstants;
import top.okya.component.domain.LoginUser;
import top.okya.component.domain.vo.LoginBody;
import top.okya.component.enums.exception.LoginExceptionType;
import top.okya.component.exception.LoginException;
import top.okya.component.utils.common.DateFormatUtil;
import top.okya.component.utils.redis.JedisUtil;
import top.okya.component.utils.security.JwtUtil;
import top.okya.system.async.AsyncService;
import top.okya.system.dao.AsLoginRecordMapper;
import top.okya.system.dao.AsUserMapper;
import top.okya.system.domain.AsLoginRecord;
import top.okya.system.service.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author: maojiaqi
 * @Date: 2023/7/21 16:42
 * @describe: 登录相关Service实现
 */

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Value("${security.token.header:#{null}}")
    private String tokenHeader;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AsLoginRecordMapper asLoginRecordMapper;

    @Autowired
    AsUserMapper asUserMapper;

    @Resource
    JedisUtil jedisUtil;

    @Override
    public LoginUser login(HttpServletResponse response, LoginBody loginBody) {
        if (!Objects.equals(OkyaConfig.getCaptchaType().toUpperCase(), CommonConstants.DISABLED_CAPTCHA)) {
            if(StringUtils.isBlank(loginBody.getCaptchaCode())){
                throw new LoginException(LoginExceptionType.CAPTCHA_EMPTY, loginBody.getUserCode());
            }
            if (!Objects.equals(jedisUtil.get(RedisConstants.CAPTCHA_CODE_KEY + loginBody.getUuid()), loginBody.getCaptchaCode())) {
                throw new LoginException(LoginExceptionType.CAPTCHA_ERROR, loginBody.getUserCode());
            }
        }
        // 用户验证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginBody.getUserCode(), loginBody.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        log.info("用户：{}登陆成功！", loginUser.getUserCode());
        AsLoginRecord asLoginRecord = new AsLoginRecord()
                .setUserCode(loginBody.getUserCode())
                .setLoginTime(DateFormatUtil.nowDate())
                .setStatus(LoginConstants.LOGIN_SUCCESS)
                .setMsg("登陆成功！");
        AsyncService.me().insertLoginRecord(asLoginRecord);
        // 生成token
        response.setHeader(tokenHeader, JwtUtil.createToken(loginUser));
        return loginUser;
    }

    @Override
    public int insertLoginRecord(AsLoginRecord asLoginRecord) {
        return asLoginRecordMapper.insert(asLoginRecord);
    }
}
