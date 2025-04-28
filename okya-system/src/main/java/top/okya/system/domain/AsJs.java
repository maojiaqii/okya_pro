package top.okya.system.domain;
 
import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.okya.component.domain.dto.base.Basic;

/**
 * js代码块(AsJs)实体类
 *
 * @author makejava
 * @since 2025-01-10 14:38:24
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsJs extends Basic implements Serializable {
    private static final long serialVersionUID = 372243090039585301L;
 
    private String jsId;
 
    private String jsCode;
 
    private String js;
}
