package top.okya.api.unauthenticated;

import com.google.code.kaptcha.Producer;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import top.okya.component.annotation.ApiLog;
import top.okya.component.annotation.UnAuthController;
import top.okya.component.config.OkyaConfig;
import top.okya.component.constants.CommonConstants;
import top.okya.component.constants.RedisConstants;
import top.okya.component.domain.HttpResult;
import top.okya.component.domain.vo.LoginBody;
import top.okya.component.enums.OperationType;
import top.okya.component.enums.exception.ServiceExceptionType;
import top.okya.component.exception.ServiceException;
import top.okya.component.utils.common.IdUtil;
import top.okya.component.utils.redis.JedisUtil;
import top.okya.system.service.LoginService;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @author: maojiaqi
 * @Date: 2023/7/21 17:29
 * @describe: 登录相关接口
 */

@UnAuthController("/login")
public class AuthController {

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;
    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;
    @Autowired
    private LoginService loginService;
    @Resource
    private JedisUtil jedisUtil;

    /**
     * 验证码生成
     */
    @GetMapping(value = "/captchaImage")
    @ApiLog(title = "获取验证码")
    public HttpResult getCaptchaImage(@RequestParam(value = "type", required = false) String type) {
        /*if (Objects.equals(CommonConstants.DISABLED_CAPTCHA, OkyaConfig.getCaptchaType())) {
            return HttpResult.error(ServiceExceptionType.DISABLED_CAPTCHA.getDesc());
        }*/
        String uuid = IdUtil.simpleUUID();
        String capStr = null, code = null;
        BufferedImage bi = null;
        String finalType = (StringUtils.isNotBlank(type) ? type : OkyaConfig.getCaptchaType()).toUpperCase();
        if (Objects.equals(CommonConstants.CHAR_CAPTCHA, finalType)) {
            capStr = code = captchaProducer.createText();
            bi = captchaProducer.createImage(capStr);
        } else {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            bi = captchaProducerMath.createImage(capStr);
        }
        jedisUtil.set(RedisConstants.CAPTCHA_CODE_KEY + uuid, code, RedisConstants.CAPTCHA_EXPIRATION);
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        try {
            ImageIO.write(bi, "jpg", out);
        } catch (Exception e) {
            throw new ServiceException(ServiceExceptionType.CAPTCHA_CREATE_ERROR);
        }
        return HttpResult.success(ImmutableMap.of("uuid", uuid, "img", CommonConstants.CAPTCHA_TO_BASE64_PREFIX + Base64.encodeBase64String(out.toByteArray())));
    }

    @PostMapping("/userLogin")
    @ApiLog(title = "密码登录", operationType = OperationType.LOGIN)
    public HttpResult userLogin(HttpServletResponse response, @Validated @RequestBody LoginBody loginBody) {
        return HttpResult.success("登录成功！", loginService.login(response, loginBody));
    }
}
