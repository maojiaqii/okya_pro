package top.okya.component.utils.ip;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.okya.component.constants.CharacterConstants;
import top.okya.component.constants.CommonConstants;
import top.okya.component.utils.spring.SpringUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: maojiaqi
 * @Date: 2023/7/13 14:40
 * @describe： ip工具类
 */

@Slf4j
public class IpUtil {

    private static final byte[] IP_REGIONS = SpringUtil.getBean("ipRegion");

    private static final String REGX_0_255 = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
    /**
     * 匹配 ip
     */
    private static final String REGX_IP = "((" + REGX_0_255 + "\\.){3}" + REGX_0_255 + ")";

    /**
     * 获取客户端IP
     *
     * @return IP地址
     */
    public static String getIpAddr() {
        return getIpAddr(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    /**
     * 获取客户端IP
     *
     * @param request 请求对象
     * @return IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return CommonConstants.UNKNOWN;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || CommonConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || CommonConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || CommonConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || CommonConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || CommonConstants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip) ? "127.0.0.1" : getMultistageReverseProxyIp(ip);
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    public static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(CharacterConstants.COMMA) > 0) {
            final String[] ips = ip.trim().split(CharacterConstants.COMMA);
            for (String subIp : ips) {
                if (false == isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return ip.substring(0, 255);
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    public static boolean isUnknown(String checkString) {
        return StringUtils.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    public static String ip2Region(String ip) {
        if (IP_REGIONS != null) {
            try {
                String region = Searcher.newWithBuffer(IP_REGIONS).search(ip);
                return region;
            } catch (Exception e) {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return CommonConstants.UNKNOWN;
    }
}
