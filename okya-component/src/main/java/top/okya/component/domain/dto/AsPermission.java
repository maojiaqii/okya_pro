package top.okya.component.domain.dto;
 
import java.util.Date;
import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.domain.child.PermissionMetaPermis;

/**
 * 菜单权限表(AsPermission)实体类
 *
 * @author mjq
 * @since 2024-07-26 16:55:20
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsPermission implements Serializable {
    private static final long serialVersionUID = 567085548603545177L;
    /**
     * 权限编号
     */ 
    private String permissionId;
    /**
     * 菜单类型（M菜单 B按钮）
     */ 
    private String permissionType;
    /**
     * 父级权限编号，默认为0没有父级
     */ 
    private String parentId;
    /**
     * 路由地址，如果为外链，则会在新窗口打开
     */ 
    private String path;
    /**
     * 组件
     */ 
    private String component;
    /**
     * 路由参数
     */
    private JSONObject params;
    /**
     * 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
     */ 
    private String name;
    /**
     * 当设置 noredirect 的时候该路由在面包屑导航中不可被点击
     */ 
    private String redirect;
    /**
     * 显示顺序
     */ 
    private Integer orderNum;
    /**
     * 当设置 true 的时候该路由不会再侧边栏出现 如404，login等页面(默认 false)
     */ 
    private Integer hidden;
    /**
     * 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式，只有一个时，会将那个子路由当做根路由显示在侧边栏，若你想不管路由下面的 children 声明的个数都显示你的根路由，你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由(默认 false)
     */ 
    private Integer alwaysShow;
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
    private Integer noCache;
    /**
     * 如果设置为false，则不会在breadcrumb面包屑中显示(默认 true)
     */ 
    private Integer breadcrumb;
    /**
     * 如果设置为true，则会一直固定在tag项中(默认 false)
     */ 
    private Integer affix;
    /**
     * 如果设置为true，则不会出现在tag中(默认 false)
     */ 
    private Integer noTagsView;
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
    private Integer canTo;
    /**
     * 菜单状态（0正常 1停用）
     */ 
    private Integer status;
    /**
     * 创建者
     */ 
    private String createBy;
    /**
     * 创建时间
     */ 
    private Date createTime;
    /**
     * 更新者
     */ 
    private String updateBy;
    /**
     * 更新时间
     */ 
    private Date updateTime;
    /**
     * 备注
     */ 
    private String remark;
    /**
     * 权限集合
     */
    private PermissionMetaPermis permission;
}
