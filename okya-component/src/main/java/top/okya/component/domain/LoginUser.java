package top.okya.component.domain;

import com.alibaba.fastjson2.annotation.JSONField;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import top.okya.component.domain.dto.AsDept;
import top.okya.component.domain.dto.AsRole;
import top.okya.component.domain.dto.AsUser;

import java.util.Collection;
import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2023/7/14 18:05
 * @describe:
 */

public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    private String userCode;

    private String id;

    /**
     * 登录时间
     */
    private String loginTime;

    /**
     * 过期时间
     */
    private String expireTime;

    /**
     * 登录IP地址
     */
    private String ip;

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
     * 用户信息
     */
    private AsUser asUser;

    /**
     * 部门信息
     */
    private AsDept asDept;

    /**
     * 角色信息
     */
    private List<AsRole> asRoles;

    public LoginUser() {
    }

    public LoginUser(String userCode, AsUser asUser, AsDept asDep, List<AsRole> asRoles) {
        this.userCode = userCode;
        this.asUser = asUser;
        this.asDept = asDep;
        this.asRoles = asRoles;
    }

    public List<AsRole> getAsRoles() {
        return asRoles;
    }

    public void setAsRoles(List<AsRole> asRoles) {
        this.asRoles = asRoles;
    }

    public AsUser getAsUser() {
        return asUser;
    }

    public void setAsUser(AsUser asUser) {
        this.asUser = asUser;
    }

    public AsDept getAsDept() {
        return asDept;
    }

    public void setAsDept(AsDept asDept) {
        this.asDept = asDept;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() { return asUser.getUserName(); }

    @JSONField(serialize = false)
    @Override
    public String getPassword()
    {
        return asUser.getPassword();
    }

    /**
     * 账户是否未过期,过期无法验证
     */
    @JSONField(serialize = false)
    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    /**
     * 指定用户是否解锁,锁定的用户无法进行身份验证
     *
     * @return
     */
    @JSONField(serialize = false)
    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    /**
     * 指示是否已过期的用户的凭据(密码),过期的凭据防止认证
     *
     * @return
     */
    @JSONField(serialize = false)
    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    /**
     * 是否可用 ,禁用的用户不能身份验证
     *
     * @return
     */
    @JSONField(serialize = false)
    @Override
    public boolean isEnabled()
    {
        return true;
    }

}
