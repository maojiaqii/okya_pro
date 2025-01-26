package top.okya.system.dao;
 
import top.okya.system.domain.AsJs;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;
 
/**
 * js代码块(AsJs)表数据库访问层
 *
 * @author makejava
 * @since 2025-01-10 14:38:24
 */
public interface AsJsMapper {
 
    /**
     * 通过ID查询单条数据
     *
     * @param jsCode
     * @return 实例对象
     */
    AsJs queryByCode(String jsCode);
 
}
