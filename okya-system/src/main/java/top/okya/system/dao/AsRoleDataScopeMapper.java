package top.okya.system.dao;

import top.okya.system.domain.AsRoleDataScope;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * (AsRoleDataScope)表数据库访问层
 *
 * @author mjq
 * @since 2023-08-28 17:42:25
 */
@Mapper
public interface AsRoleDataScopeMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param roleId 主键
     * @return 实例对象
     */
    AsRoleDataScope queryById(Long roleId);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AsRoleDataScope> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param asRoleDataScope 实例对象
     * @return 对象列表
     */
    List<AsRoleDataScope> queryAll(AsRoleDataScope asRoleDataScope);

    /**
     * 新增数据
     *
     * @param asRoleDataScope 实例对象
     * @return 影响行数
     */
    int insert(AsRoleDataScope asRoleDataScope);

    /**
     * 修改数据
     *
     * @param asRoleDataScope 实例对象
     * @return 影响行数
     */
    int update(AsRoleDataScope asRoleDataScope);

    /**
     * 通过主键删除数据
     *
     * @param roleId 主键
     * @return 影响行数
     */
    int deleteById(Long roleId);

}
