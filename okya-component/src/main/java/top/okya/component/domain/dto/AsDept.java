package top.okya.component.domain.dto;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 部门表(AsDept)实体类
 *
 * @author mjq
 * @since 2023-08-28 16:25:16
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsDept implements Serializable {
    private static final long serialVersionUID = -70954527232739460L;
    /**
    * 部门id
    */
    private Long deptId;
    /**
    * 部门code
    */
    private String deptCode;
    /**
    * 父部门id
    */
    private Long parentId;
    /**
    * 部门名称
    */
    private String deptName;
    /**
    * 显示顺序
    */
    private Integer orderNum;
    /**
    * 负责人
    */
    private String leader;
    /**
    * 联系电话
    */
    private String phone;
    /**
    * 邮箱
    */
    private String email;
    /**
    * 部门状态（0正常 1停用 2删除）
    */
    private String status;
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

}
