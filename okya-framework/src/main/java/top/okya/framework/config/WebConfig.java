package top.okya.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.okya.component.annotation.TestController;
import top.okya.component.annotation.UnAuthController;

/**
 * @author: maojiaqi
 * @Date: 2023/7/14 17:07
 * @describe: 分类请求加前缀
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.api.path.test-prefix:#{null}}")
    private String testPrefix;

    @Value("${spring.api.path.unAuth-prefix:#{null}}")
    private String uaPrefix;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(testPrefix, c -> c.isAnnotationPresent(TestController.class))
                .addPathPrefix(uaPrefix, c -> c.isAnnotationPresent(UnAuthController.class));;
    }
}
