package top.okya.framework.aspect;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.okya.component.annotation.ApiLog;
import top.okya.component.constants.CommonConstants;
import top.okya.component.domain.LoginUser;
import top.okya.component.exception.base.BaseException;
import top.okya.component.utils.common.DateFormatUtil;
import top.okya.component.utils.ip.IpUtil;
import top.okya.system.async.AsyncService;
import top.okya.system.domain.AsOperLog;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author: maojiaqi
 * @Date: 2022/5/13 11:44
 * @describe： 实现ApiLog的功能，即打印请求日志和响应日志
 */

@Aspect
@Component
@Slf4j
public class ApiLogAspect {

    /**
     * 记录业务请求的时间
     */
    private long req;

    private String reqTime;

    private AsOperLog asOperLog;

    /**
     * 在进入controller之前拦截并打印请求报文日志
     *
     * @param: [joinPoint, apiLog]
     */
    @Before("@annotation(apiLog)")
    public void printRequestDatagram(JoinPoint joinPoint, ApiLog apiLog) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = IpUtil.getIpAddr(request);
        String location = IpUtil.ip2Region(ip);
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        req = System.currentTimeMillis();
        reqTime = DateFormatUtil.formatMillis(req, "yyyy-MM-dd HH:mm:ss");
        // 获取当前的用户
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String args = Arrays.toString(joinPoint.getArgs());
        asOperLog = new AsOperLog().setOperIp(ip)
                .setOperLocation(location)
                .setOperTime(DateFormatUtil.millisToDate(req))
                .setOperParam(args)
                .setOperUrl(request.getRequestURL().toString())
                .setRequestMethod(request.getMethod())
                .setMethod(method.getName())
                .setOperationType(apiLog.operationType().toString())
                .setTitle(apiLog.title());
        if (!Objects.equals(principal, CommonConstants.ANONYMOUS_USER)) {
            LoginUser loginUser = (LoginUser) principal;
            asOperLog.setOperName(loginUser.getUserCode());
        }
        log.info("\n==> 【拦截到请求】\n==> 模块标题：{}\n==> 业务类型：{}\n==> 请求者IP：{}\n==> 请求时间：{}\n==> 请求接口：{} {}\n==> 请求方法：{}\n==> 参数内容：{}", asOperLog.getTitle(), asOperLog.getOperationType(), ip, reqTime, asOperLog.getRequestMethod(), asOperLog.getOperUrl(), asOperLog.getMethod(), args);
    }

    /**
     * 返回信息后，打印应答报文的日志
     *
     * @param: [joinPoint]
     */
    @AfterReturning(value = "@annotation(apiLog)", returning = "result")
    public void printResponseDatagram(Object result, ApiLog apiLog) {
        long respTime = System.currentTimeMillis() - req;
        String jsonResult = JSON.toJSONString(result);
        asOperLog.setStatus(CommonConstants.SUCCESS).setFinishTime(DateFormatUtil.millisToDate(respTime + req)).setCostTime(respTime).setJsonResult(jsonResult);
        AsyncService.me().insertOperationRecord(asOperLog);
        log.info("\n<== 【应答的信息】\n<== 应答时间：{}\n<== 应答耗时：{}毫秒\n<== 应答内容：{}", reqTime, respTime, jsonResult);
    }

    /**
     * 异常报错后，打印报错的日志
     *
     * @param: [joinPoint]
     */
    @AfterThrowing(value = "@annotation(apiLog)", throwing = "ex")
    public void printErrorDatagram(ApiLog apiLog, Exception ex) {
        String data = null;
        if (ex instanceof BaseException) {
            data = JSON.toJSONString(((BaseException) ex).getData());
        }
        String errorMsg = ex.getMessage();
        long respTime = System.currentTimeMillis() - req;
        asOperLog.setStatus(CommonConstants.FAIL).setFinishTime(DateFormatUtil.millisToDate(respTime + req)).setErrorMsg(errorMsg).setCostTime(respTime).setJsonResult(data);
        AsyncService.me().insertOperationRecord(asOperLog);
        log.error("\n<== 【报错的异常】\n<== 错误时间：{}\n<== 错误耗时：{}毫秒\n<== 错误描述：{}\n{}", reqTime, respTime, errorMsg, data != null ? "<== 错误内容：" + data + "\n" : "");
    }

}
