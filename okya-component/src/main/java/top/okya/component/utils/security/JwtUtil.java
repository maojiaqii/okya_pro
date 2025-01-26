package top.okya.component.utils.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.okya.component.constants.RedisConstants;
import top.okya.component.domain.LoginUser;
import top.okya.component.utils.common.IdUtil;
import top.okya.component.utils.ip.IpUtil;
import top.okya.component.utils.redis.JedisUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2023/7/14 17:29
 * @describe： token工具类
 */

@Component
@Slf4j
public class JwtUtil {

    /**
     * token的有效时长
     */
    @Value("${security.token.expireTime:#{null}}")
    private long expireTime;
    /**
     * token所在请求头内的位置
     */
    @Value("${security.token.header:#{null}}")
    private String header;
    /**
     * token的秘钥
     */
    @Value("${security.token.secret:#{null}}")
    private String secret;
    /**
     * token的前缀
     */
    @Value("${security.token.prefix:#{null}}")
    private String prefix;
    /**
     * token的用户信息键
     */
    @Value("${security.token.userKey:#{null}}")
    private String userKey;

    public static String headerKey;
    public static long expireTimeS;
    public static String secretS;
    public static String prefixS;
    public static String userKeyS;

    @Resource
    private JedisUtil jedisUtil;

    private static JedisUtil redisUtil;

    @PostConstruct
    private void init() {
        log.info("初始化 JwtUtil；expireTime：{}  header：{}  secret：{}  prefix：{}  userKey：{}", expireTime, header, secret, prefix, userKey);
        headerKey = header;
        expireTimeS = expireTime * 60 * 1000L;
        secretS = secret;
        prefixS = prefix + " ";
        userKeyS = userKey;
        redisUtil = jedisUtil;
    }

    /**
     * 从请求中获取token
     *
     * @param request 请求
     * @return 获取到的token
     */
    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader(headerKey);
        if (StringUtils.isNotBlank(token)) {
            token = token.replace(prefixS, "");
        }
        return token;
    }

    /**
     * 创建token
     *
     * @param loginUser 登录用户
     * @return 创建的token
     */
    public static String createToken(LoginUser loginUser) {

        String id = IdUtil.simpleUUID();
        long current = System.currentTimeMillis();
        loginUser.setId(id);
        loginUser.setLoginTime(Instant.ofEpochMilli(current).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // token的过期时间
        Date date = new Date(current + expireTimeS);
        loginUser.setExpireTime(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        setUserAgent(loginUser);

        redisUtil.set(RedisConstants.LOGIN_TOKEN_KEY + id, loginUser, expireTimeS);

        // jwt的header部分
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        // 创建token
        String token;
        try {
            token = JWT.create()
                    //header部分
                    .withHeader(map)
                    //存储用户信息
                    .withClaim(userKeyS, id)
                    //过期时间
                    .withExpiresAt(date)
                    //签发时间
                    .withIssuedAt(new Date(current))
                    //私钥
                    .sign(Algorithm.HMAC256(secretS));
        } catch (Exception e) {
            log.error("为用户{}创建token失败", loginUser.getUserCode());
            throw new RuntimeException("为用户创建token失败", e);
        }
        return prefixS + token;
    }

    /**
     * 校验token
     *
     * @param token 传入的token
     * @return 是否校验通过
     */
    public static boolean verifyToken(String token) {
        try {
            isExpire(token);
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretS)).build();
            verifier.verify(token.contains(" ") ? token.split(" ")[1] : token);
            return true;
        } catch (Exception e) {
            log.error("校验token={}失败", token, e);
            return false;
        }
    }

    /**
     * 从token中获取用户信息
     *
     * @param token 传入的token
     * @return 用户信息
     */
    public static LoginUser getUserInfo(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token.contains(" ") ? token.split(" ")[1] : token);
            return (LoginUser) redisUtil.get(RedisConstants.LOGIN_TOKEN_KEY + jwt.getClaim(userKeyS).asString(), LoginUser.class);
        } catch (Exception e) {
            log.error("从token={}中获取用户信息失败", token, e);
            return null;
        }
    }

    public static void delLoginUser(String id) {
        redisUtil.del(RedisConstants.LOGIN_TOKEN_KEY + id);
    }

    /**
     * 判断是否过期
     */
    public static boolean isExpire(String token) {
        DecodedJWT jwt = JWT.decode(token);
        boolean flag = jwt.getExpiresAt().getTime() < System.currentTimeMillis();
        log.info("token：{}是否过期：{}", token, flag);
        return flag;
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    private static void setUserAgent(LoginUser loginUser) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            UserAgent userAgent = UserAgent.parseUserAgentString(requestAttributes.getRequest().getHeader("User-Agent"));
            loginUser.setIp(IpUtil.getIpAddr());
            loginUser.setLoginLocation(IpUtil.ip2Region(loginUser.getIp()));
            loginUser.setBrowser(userAgent.getBrowser().getName());
            loginUser.setOs(userAgent.getOperatingSystem().getName());
        }
    }
}

