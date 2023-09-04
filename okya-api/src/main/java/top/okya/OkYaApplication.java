package top.okya;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

/**
 * @author: maojiaqi
 * @Date: 2023/7/13 17:09
 * @describe：项目主启动类
 */

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan("top.okya.**.dao")
public class OkYaApplication {
    public static void main(String[] args) {
        Long startTime = System.currentTimeMillis();
        SpringApplication.run(OkYaApplication.class, args);
        System.out.println("项目启动完成，总耗时：" + Double.parseDouble(String.valueOf(System.currentTimeMillis() - startTime)) / 1000 + "s \n" +
                " ██████╗ ██╗  ██╗██╗   ██╗ █████╗ \n" +
                "██╔═══██╗██║ ██╔╝╚██╗ ██╔╝██╔══██╗\n" +
                "██║   ██║█████╔╝  ╚████╔╝ ███████║\n" +
                "██║   ██║██╔═██╗   ╚██╔╝  ██╔══██║\n" +
                "╚██████╔╝██║  ██╗   ██║   ██║  ██║\n" +
                " ╚═════╝ ╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝");
    }

    @Bean(name = "ipRegion")
    public byte[] ipRegion() {
        String dbPath = "ip2region.xdb";
        byte[] vIndex = new byte[0];
        try {
            vIndex = FileCopyUtils.copyToByteArray(new ClassPathResource(dbPath).getInputStream());
        } catch (Exception e) {
            System.out.printf("failed to load vector index from `%s`: %s\n", dbPath, e);
        }
        return vIndex;
    }
}
