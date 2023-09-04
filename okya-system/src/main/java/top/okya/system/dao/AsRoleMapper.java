package top.okya.system.dao;

import top.okya.component.domain.dto.AsRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 角色信息表(AsRole)表数据库访问层
 *
 * @author mjq
 * @since 2023-08-28 16:26:41
 */
@Mapper
public interface AsRoleMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param roleId 主键
     * @return 实例对象
     */
    AsRole queryById(Long roleId);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AsRole> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param asRole 实例对象
     * @return 对象列表
     */
    List<AsRole> queryAll(AsRole asRole);

    /**
     * 新增数据
     *
     * @param asRole 实例对象
     * @return 影响行数
     */
    int insert(AsRole asRole);

    /**
     * 修改数据
     *
     * @param asRole 实例对象
     * @return 影响行数
     */
    int update(AsRole asRole);

    /**
     * 通过主键删除数据
     *
     * @param roleId 主键
     * @return 影响行数
     */
    int deleteById(Long roleId);

}
