package top.okya.component.enums;

/**
 * @author: maojiaqi
 * @Date: 2023/7/11 10:51
 * @describe： 限流类型
 */

public enum LimitType {
    /**
     * 默认策略全局限流
     */
    DEFAULT,

    /**
     * 根据请求者IP进行限流
     */
    IP
}
