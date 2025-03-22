package top.okya.system.async;

import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.okya.component.utils.common.IdUtil;
import top.okya.component.utils.ip.IpUtil;
import top.okya.component.utils.spring.SpringUtil;
import top.okya.system.domain.AsLoginRecord;
import top.okya.system.domain.AsOperLog;
import top.okya.system.service.LoginService;
import top.okya.system.service.OperationService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author: maojiaqi
 * @Date: 2023/7/21 15:48
 * @describe: 多线程的公用业务类
 */

@Slf4j
@Component
public class AsyncService {

    private Executor executor = new DelegatingSecurityContextExecutor(SpringUtil.getBean("asyncServiceExecutor"));

    /**
     * 单例模式
     */
    private AsyncService() {
    }

    private static AsyncService me = new AsyncService();

    public static AsyncService me() {
        return me;
    }

    public void insertLoginRecord(AsLoginRecord asLoginRecord) {
        UserAgent userAgent = UserAgent.parseUserAgentString(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("User-Agent"));
        asLoginRecord.setIpaddr(IpUtil.getIpAddr())
                .setLoginLocation(IpUtil.ip2Region(IpUtil.getIpAddr()))
                .setBrowser(userAgent.getBrowser().getName())
                .setOs(userAgent.getOperatingSystem().getName())
                .setInfoId(IdUtil.randomUUID());
        CompletableFuture.runAsync(() -> {
            SpringUtil.getBean(LoginService.class).insertLoginRecord(asLoginRecord);
        }, executor);
    }

    public void insertOperationRecord(AsOperLog asOperLog) {
        asOperLog.setOperId(IdUtil.randomUUID());
        CompletableFuture.runAsync(() -> {
            SpringUtil.getBean(OperationService.class).insertOperationRecord(asOperLog);
        }, executor);
    }
}
