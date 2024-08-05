package top.okya.system.dao;

import top.okya.system.domain.AsUserDept;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 用户与岗位关联表(AsUserDept)表数据库访问层
 *
 * @author mjq
 * @since 2023-08-28 16:26:41
 */
@Mapper
public interface AsUserDeptMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    AsUserDept queryById(Long userId);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AsUserDept> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param asUserDept 实例对象
     * @return 对象列表
     */
    List<AsUserDept> queryAll(AsUserDept asUserDept);

    /**
     * 新增数据
     *
     * @param asUserDept 实例对象
     * @return 影响行数
     */
    int insert(AsUserDept asUserDept);

    /**
     * 修改数据
     *
     * @param asUserDept 实例对象
     * @return 影响行数
     */
    int update(AsUserDept asUserDept);

    /**
     * 通过主键删除数据
     *
     * @param userId 主键
     * @return 影响行数
     */
    int deleteById(Long userId);

}
