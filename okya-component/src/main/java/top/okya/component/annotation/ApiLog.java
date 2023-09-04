package top.okya.component.annotation;

import top.okya.component.enums.OperationType;
import top.okya.component.enums.OperatorType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: maojiaqi
 * @Date: 2022/5/13 11:43
 * @describe： 用于打印请求和响应日志的注解
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLog {

    /**
     * 模块
     */
    public String title() default "";

    /**
     * 业务类型
     */
    public OperationType operationType() default OperationType.OTHER;
}