package top.okya.component.utils.mybatis;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;
import top.okya.component.config.OkyaConfig;

import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * @author: maojiaqi
 * @Date: 2025/4/10 21:18
 * @describe: 数据库查询结果返回前的拦截器
 */

@Component
@Intercepts(@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}))
public class MapKeyInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 执行原有逻辑，获取结果集
        List<Object> results = (List<Object>) invocation.proceed();
        // 处理结果集中的每个Map
        results.forEach(result -> {
            if (result instanceof List) {
                ((List<?>) result).forEach(this::processItem);
            } else {
                processItem(result);
            }
        });
        return results;
    }

    private void processItem(Object item) {
        if (item instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) item;
            // 转换所有键为小写
            Set<String> keys = new HashSet<>(map.keySet());
            keys.forEach(key -> {
                Object value = map.get(key);
                map.remove(key);
                map.put(Objects.equals(OkyaConfig.getMapKeyType(), "U") ? key.toUpperCase() : key.toLowerCase(), value);
            });
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
