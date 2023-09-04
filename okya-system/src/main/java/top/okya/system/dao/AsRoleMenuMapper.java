package top.okya.system.dao;

import top.okya.system.domain.AsRoleMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 角色和菜单关联表(AsRoleMenu)表数据库访问层
 *
 * @author mjq
 * @since 2023-08-28 16:26:40
 */
@Mapper
public interface AsRoleMenuMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param roleId 主键
     * @return 实例对象
     */
    AsRoleMenu queryById(Long roleId);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AsRoleMenu> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param asRoleMenu 实例对象
     * @return 对象列表
     */
    List<AsRoleMenu> queryAll(AsRoleMenu asRoleMenu);

    /**
     * 新增数据
     *
     * @param asRoleMenu 实例对象
     * @return 影响行数
     */
    int insert(AsRoleMenu asRoleMenu);

    /**
     * 修改数据
     *
     * @param asRoleMenu 实例对象
     * @return 影响行数
     */
    int update(AsRoleMenu asRoleMenu);

    /**
     * 通过主键删除数据
     *
     * @param roleId 主键
     * @return 影响行数
     */
    int deleteById(Long roleId);

}
