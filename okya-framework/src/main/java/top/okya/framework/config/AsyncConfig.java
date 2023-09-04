package top.okya.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: maojiaqi
 * @Date: 2023/7/14 15:55
 * @describe： 线程池配置类
 */

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    @Value("${spring.async.corePoolSize:#{null}}")
    private int asyncCorePoolSize;
    @Value("${spring.async.maxPoolSize:#{null}}")
    private int asyncMaxPoolSize;
    @Value("${spring.async.queueCapacity:#{null}}")
    private int asyncQueueCapacity;
    @Value("${spring.async.keepAliveSeconds:#{null}}")
    private int asyncKeepAliveSeconds;

    @Bean
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(asyncCorePoolSize);
        //配置最大线程数
        executor.setMaxPoolSize(asyncMaxPoolSize);
        //配置队列大小
        executor.setQueueCapacity(asyncQueueCapacity);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("async-");
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(asyncKeepAliveSeconds);
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        log.info("\n<== 线程池初始化完成\n"
                + "<== 核心线程数【{}】\n"
                + "<== 最大线程数【{}】\n"
                + "<== 队列大小【{}】\n"
                + "<== 线程活跃时间【{}】（秒）", asyncCorePoolSize, asyncMaxPoolSize, asyncQueueCapacity, asyncKeepAliveSeconds);
        return executor;
    }
}
