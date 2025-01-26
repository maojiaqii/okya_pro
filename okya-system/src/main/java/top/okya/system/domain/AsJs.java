package top.okya.system.domain;
 
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
 
/**
 * js代码块(AsJs)实体类
 *
 * @author makejava
 * @since 2025-01-10 14:38:24
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsJs implements Serializable {
    private static final long serialVersionUID = 372243090039585301L;
 
    private String jsId;
 
    private String jsCode;
 
    private String js;
 
    private String remark;
}
