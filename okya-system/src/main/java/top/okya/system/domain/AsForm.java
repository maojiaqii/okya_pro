package top.okya.system.domain;
 
import java.util.Date;
import java.io.Serializable;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.domain.dto.base.Basic;

/**
 * 表单(AsForm)实体类
 *
 * @author makejava
 * @since 2025-01-08 14:24:52
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsForm extends Basic implements Serializable {
    private static final long serialVersionUID = 972689727496951576L;
    /**
     * 表单id
     */ 
    private String formId;
    /**
     * 表单代码
     */ 
    private String formCode;
    /**
     * 表单名称
     */ 
    private String formName;
    /**
     * 表单属性
     */ 
    private JSONObject props;
    /**
     * 字段信息
     */ 
    private JSONArray formItems;
    /**
     * 表单校验
     */ 
    private JSONObject formValidators;
    /**
     * 生命周期函数
     */
    private JSONObject lifecycle;
    /**
     * 数据库映射关系
     */
    private JSONObject dbMapping;
}
