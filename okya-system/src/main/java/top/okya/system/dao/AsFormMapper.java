package top.okya.system.dao;
 
import org.apache.ibatis.annotations.Mapper;
import top.okya.system.domain.AsForm;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;
 
/**
 * 表单(AsForm)表数据库访问层
 *
 * @author makejava
 * @since 2025-01-08 14:24:52
 */
@Mapper
public interface AsFormMapper {
 
    /**
     * 通过formCode查询单条数据
     *
     * @param formCode 代码
     * @return 实例对象
     */
    AsForm queryByCode(String formCode);
}
