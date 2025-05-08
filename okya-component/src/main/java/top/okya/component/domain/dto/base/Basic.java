package top.okya.component.domain.dto.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author: maojiaqi
 * @Date: 2025/4/21 22:50
 * @describe:
 */

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Basic {
    /**
     * 是否删除（0:否 1:是）
     */
    private Integer isDelete;
    /**
     * 状态（0正常 1停用）
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
}
