package top.okya.system.dao;

import top.okya.system.domain.AsUserRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 用户和角色关联表(AsUserRole)表数据库访问层
 *
 * @author mjq
 * @since 2023-08-28 16:26:41
 */
@Mapper
public interface AsUserRoleMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    AsUserRole queryById(Long userId);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AsUserRole> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param asUserRole 实例对象
     * @return 对象列表
     */
    List<AsUserRole> queryAll(AsUserRole asUserRole);

    /**
     * 新增数据
     *
     * @param asUserRole 实例对象
     * @return 影响行数
     */
    int insert(AsUserRole asUserRole);

    /**
     * 修改数据
     *
     * @param asUserRole 实例对象
     * @return 影响行数
     */
    int update(AsUserRole asUserRole);

    /**
     * 通过主键删除数据
     *
     * @param userId 主键
     * @return 影响行数
     */
    int deleteById(Long userId);

}
