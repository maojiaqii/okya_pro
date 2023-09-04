package top.okya.component.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author: maojiaqi
 * @Date: 2023/7/11 10:51
 * @describe： 读取项目自定义配置
 */

@Component
@ConfigurationProperties(prefix = "okya")
public class OkyaConfig {

    /**
     * 项目名称
     */
    private String name;

    /**
     * 版本
     */
    private String version;

    /**
     * 版权年份
     */
    private String copyrightYear;

    /**
     * 上传路径
     */
    private String fileFolder;

    /**
     * 获取地址开关
     */
    private String iconUrlPrefix;

    /**
     * 验证码类型
     */
    private static String captchaType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCopyrightYear() {
        return copyrightYear;
    }

    public void setCopyrightYear(String copyrightYear) {
        this.copyrightYear = copyrightYear;
    }

    public String getFileFolder() {
        return fileFolder;
    }

    public void setFileFolder(String fileFolder) {
        this.fileFolder = fileFolder;
    }

    public String getIconUrlPrefix() {
        return iconUrlPrefix;
    }

    public void setIconUrlPrefix(String iconUrlPrefix) {
        this.iconUrlPrefix = iconUrlPrefix;
    }

    public static String getCaptchaType() {
        return captchaType;
    }

    public void setCaptchaType(String captchaType) {
        OkyaConfig.captchaType = Optional.ofNullable(captchaType).orElse("none").toUpperCase();
    }

}
