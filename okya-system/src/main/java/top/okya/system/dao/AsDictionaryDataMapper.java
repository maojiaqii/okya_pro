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
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AsDictionaryData> queryAllByLimit(@Param("dictCode") String dictCode, @Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param asDictionaryData 实例对象
     * @return 对象列表
     */
    List<AsDictionaryData> queryAll(AsDictionaryData asDictionaryData);

    /**
     * 新增数据
     *
     * @param asDictionaryData 实例对象
     * @return 影响行数
     */
    int insert(AsDictionaryData asDictionaryData);

    /**
     * 修改数据
     *
     * @param asDictionaryData 实例对象
     * @return 影响行数
     */
    int update(AsDictionaryData asDictionaryData);

    /**
     * 通过主键删除数据
     *
     * @param dictId 主键
     * @return 影响行数
     */
    int deleteById(Long dictId);

    List<Map<String, Object>> queryByTable(@Param("tableName") String dictSource, @Param("sqlCondition") String sqlCondition);
}
