package top.okya.component.domain;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: maojiaqi
 * @Date: 2024/7/26 17:15
 * @describe：权限自定义属性类
 */

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionMeta {
    /**
     * 路由参数
     */
    private JSONObject params;
    /**
     * 当设置 true 的时候该路由不会再侧边栏出现 如404，login等页面(默认 false)
     */
    private boolean hidden;
    /**
     * 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式，只有一个时，会将那个子路由当做根路由显示在侧边栏，若你想不管路由下面的 children 声明的个数都显示你的根路由，你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由(默认 false)
     */
    private boolean alwaysShow;
    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;
    /**
     * 设置该路由的图标
     */
    private String icon;
    /**
     * 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
     */
    private boolean noCache;
    /**
     * 如果设置为false，则不会在breadcrumb面包屑中显示(默认 true)
     */
    private boolean breadcrumb;
    /**
     * 如果设置为true，则会一直固定在tag项中(默认 false)
     */
    private boolean affix;
    /**
     * 如果设置为true，则不会出现在tag中(默认 false)
     */
    private boolean noTagsView;
    /**
     * 在iframe组件中打开的外链
     */
    private String outerLink;
    /**
     * 显示高亮的路由路径
     */
    private String activeMenu;
    /**
     * 设置为true即使hidden为true，也依然可以进行路由跳转(默认 false)
     */
    private boolean canTo;
}
