package top.okya.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * @author: maojiaqi
 * @Date: 2023/7/11 14:57
 * @describe:redis配置类
 */

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWaitMillis;

    @Value("${spring.redis.database}")
    private int database;

    /**
     * 初始化Redis连接池
     */
    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWait(Duration.ofMillis(maxWaitMillis));
        // 连接耗尽时是否阻塞, false报异常,true阻塞直到超时, 默认true
        poolConfig.setBlockWhenExhausted(Boolean.TRUE);
        JedisPool jedisPool = new JedisPool(poolConfig, host, port, timeout, !"".equals(password) ? password : null, database);
        log.info("\n<== Redis连接池初始化完成\n"
                + "<== 地址【{}】\n"
                + "<== 数据库序号【{}】\n"
                + "<== 最大连接数【{}】\n"
                + "<== 最大阻塞等待时间【{}】\n"
                + "<== 最大空闲连接【{}】\n"
                + "<== 最小空闲连接【{}】", "redis://" + host + ":" + port, database, maxActive, maxWaitMillis == -1 ? "无限制" : maxWaitMillis, maxIdle, minIdle);
        return jedisPool;
    }

    @Bean
    RedissonClient redissonSingle() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setConnectionPoolSize(maxIdle)
                .setConnectionMinimumIdleSize(minIdle)
                .setDatabase(database)
                .setConnectTimeout(timeout);
        if (password != null && !"".equals(password)) {
            serverConfig.setPassword(password);
        }
        RedissonClient rc = Redisson.create(config);
        log.info("<== Redis分布式初始化完成");
        return rc;
    }
}
