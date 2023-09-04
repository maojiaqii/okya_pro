package top.okya.system.dao;

import top.okya.system.domain.AsOperLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 操作日志记录(AsOperLog)表数据库访问层
 *
 * @author mjq
 * @since 2023-07-25 17:41:46
 */
@Mapper
public interface AsOperLogMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param operId 主键
     * @return 实例对象
     */
    AsOperLog queryById(Long operId);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AsOperLog> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param asOperLog 实例对象
     * @return 对象列表
     */
    List<AsOperLog> queryAll(AsOperLog asOperLog);

    /**
     * 新增数据
     *
     * @param asOperLog 实例对象
     * @return 影响行数
     */
    int insert(AsOperLog asOperLog);

    /**
     * 修改数据
     *
     * @param asOperLog 实例对象
     * @return 影响行数
     */
    int update(AsOperLog asOperLog);

    /**
     * 通过主键删除数据
     *
     * @param operId 主键
     * @return 影响行数
     */
    int deleteById(Long operId);

}
