package top.okya.system.dao;

import top.okya.system.domain.AsDictionary;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 字典表(AsDictionary)表数据库访问层
 *
 * @author mjq
 * @since 2023-11-08 10:22:49
 */
@Mapper
public interface AsDictionaryMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param dictCode 字典编码
     * @return 实例对象
     */
    AsDictionary queryByCode(String dictCode);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AsDictionary> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param asDictionary 实例对象
     * @return 对象列表
     */
    List<AsDictionary> queryAll(AsDictionary asDictionary);

    /**
     * 新增数据
     *
     * @param asDictionary 实例对象
     * @return 影响行数
     */
    int insert(AsDictionary asDictionary);

    /**
     * 修改数据
     *
     * @param asDictionary 实例对象
     * @return 影响行数
     */
    int update(AsDictionary asDictionary);

    /**
     * 通过主键删除数据
     *
     * @param dictId 主键
     * @return 影响行数
     */
    int deleteById(Long dictId);

}
