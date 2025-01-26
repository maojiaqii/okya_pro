package top.okya.system.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import top.okya.component.utils.mybatis.SqlProvider;
import top.okya.system.domain.AsTable;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 表格表(AsTable)表数据库访问层
 *
 * @author makejava
 * @since 2024-12-06 22:29:58
 */
@Mapper
public interface AsTableMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param tableCode 表格代码
     * @return 实例对象
     */
    AsTable queryByCode(String tableCode);

}
