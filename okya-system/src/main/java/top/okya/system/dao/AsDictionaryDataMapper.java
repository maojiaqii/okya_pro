package top.okya.system.dao;

import top.okya.system.domain.AsDictionaryData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

/**
 * 字典数据表(AsDictionaryData)表数据库访问层
 *
 * @author mjq
 * @since 2023-11-08 10:23:53
 */
@Mapper
public interface AsDictionaryDataMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param dictCode     字典编码
     * @param sqlCondition
     * @return 对象列表
     */
    List<Map<String, Object>> queryByCode(@Param("dictCode") String dictCode, @Param("sqlCondition") String sqlCondition);

    /**
     * 通过表名查询数据
     *
     * @param dictSource 表名
     * @param sqlCondition 查询条件
     * @return 查询结果
     */
    List<Map<String, Object>> queryByTable(@Param("dictSource") String dictSource, @Param("sqlCondition") String sqlCondition);
}
