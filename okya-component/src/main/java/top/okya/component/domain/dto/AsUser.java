package top.okya.component.domain.dto;

import java.util.Date;
import java.io.Serializable;
import java.util.Objects;

import com.alibaba.fastjson2.JSONArray;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.constants.CommonConstants;

/**
 * 用户信息表(AsUser)实体类
 *
 * @author mjq
 * @since 2023-07-19 15:21:57
 */

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsUser implements Serializable {
    private static final long serialVersionUID = -47265548766516876L;
    /**
    * 用户ID
    */
    private String userId;
    /**
    * 用户代码
    */
    private String userCode;
    /**
    * 部门ID
    */
    private String deptId;
    /**
    * 用户账号
    */
    private String userName;
    /**
    * 用户类型（00管理员）
    */
    private String userType;
    /**
    * 用户邮箱
    */
    private String email;
    /**
    * 手机号码
    */
    private String phoneNumber;
    /**
    * 用户性别（0女 1男 2未知）
    */
    private Integer sex;
    /**
    * 头像地址
    */
    private String avatar;
    /**
    * 密码
    */
    private String password;
    /**
     * 角色
     */
    private JSONArray rolesJson;
    /**
     * 分管部门
     */
    private JSONArray deptsJson;
    /**
    * 帐号状态（0正常 1停用）
    */
    private Integer status;
    /**
     * 是否删除（0否 1是）
     */
    private Integer isDelete;
    /**
    * 最后登录IP
    */
    private String loginIp;
    /**
    * 最后登录时间
    */
    private Date loginDate;
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

    public boolean isAdmin(){
        return Objects.equals(this.userType, CommonConstants.ADMIN);
    }

}
