package top.okya.component.domain;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2024/7/26 16:05
 * @describe：权限主体类
 */

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Permission {
    /**
     * 路由地址，如果为外链，则会在新窗口打开
     */
    private String path;
    /**
     * 组件
     */
    private String component;
    /**
     * 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
     */
    private String name;
    /**
     * 当设置 noredirect 的时候该路由在面包屑导航中不可被点击
     */
    private String redirect;

    private PermissionMeta meta;

    private List<Permission> children;
}
