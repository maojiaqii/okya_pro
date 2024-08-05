package top.okya.system.domain;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 系统登录记录(AsLoginRecord)实体类
 *
 * @author mjq
 * @since 2023-07-21 16:11:42
 */

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsLoginRecord implements Serializable {
    private static final long serialVersionUID = 441039050835580703L;
    /**
    * 访问ID
    */
    private Long infoId;
    /**
    * 用户账号
    */
    private String userCode;
    /**
    * 登录IP地址
    */
    private String ipaddr;
    /**
    * 登录地点
    */
    private String loginLocation;
    /**
    * 浏览器类型
    */
    private String browser;
    /**
    * 操作系统
    */
    private String os;
    /**
    * 登录状态（0成功 1失败）
    */
    private int status;
    /**
    * 提示消息
    */
    private String msg;
    /**
    * 访问时间
    */
    private Date loginTime;

}
