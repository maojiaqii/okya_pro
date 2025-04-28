package top.okya.framework.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.okya.component.annotation.DataSource;
import top.okya.framework.datasource.DynamicDataSourceContextHolder;

import java.lang.reflect.Method;

/**
 * @author: maojiaqi
 * @Date: 2023/7/12 11:44
 * @describe: 实现DataSource的功能，即切换数据源
 */

@Aspect
@Order(1)
@Component
public class DataSourceAspect {
    @Pointcut("@annotation(top.okya.component.annotation.DataSource)"
            + "|| @within(top.okya.component.annotation.DataSource)")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        DataSource dataSource = method.getAnnotation(DataSource.class);
        if (dataSource != null) {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name());
        }
        try {
            return point.proceed();
        } finally {
            // 销毁数据源 在执行方法之后
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

}
