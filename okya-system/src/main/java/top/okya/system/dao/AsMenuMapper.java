package top.okya.system.dao;

import top.okya.component.domain.dto.AsMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 菜单权限表(AsMenu)表数据库访问层
 *
 * @author mjq
 * @since 2023-08-28 16:26:40
 */
@Mapper
public interface AsMenuMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param menuId 主键
     * @return 实例对象
     */
    AsMenu queryById(Long menuId);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AsMenu> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param asMenu 实例对象
     * @return 对象列表
     */
    List<AsMenu> queryAll(AsMenu asMenu);

    /**
     * 新增数据
     *
     * @param asMenu 实例对象
     * @return 影响行数
     */
    int insert(AsMenu asMenu);

    /**
     * 修改数据
     *
     * @param asMenu 实例对象
     * @return 影响行数
     */
    int update(AsMenu asMenu);

    /**
     * 通过主键删除数据
     *
     * @param menuId 主键
     * @return 影响行数
     */
    int deleteById(Long menuId);

}
