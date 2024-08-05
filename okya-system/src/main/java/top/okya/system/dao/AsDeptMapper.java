package top.okya.system.dao;

import top.okya.component.domain.dto.AsDept;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 部门表(AsDept)表数据库访问层
 *
 * @author mjq
 * @since 2023-08-28 16:25:16
 */
@Mapper
public interface AsDeptMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param deptId 主键
     * @return 实例对象
     */
    AsDept queryById(Long deptId);

    /**
     * 通过userID查询单条数据
     *
     * @param userId 用户编号
     * @return 实例对象
     */
    AsDept queryByUserId(Long userId);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AsDept> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param asDept 实例对象
     * @return 对象列表
     */
    List<AsDept> queryAll(AsDept asDept);

    /**
     * 新增数据
     *
     * @param asDept 实例对象
     * @return 影响行数
     */
    int insert(AsDept asDept);

    /**
     * 修改数据
     *
     * @param asDept 实例对象
     * @return 影响行数
     */
    int update(AsDept asDept);

    /**
     * 通过主键删除数据
     *
     * @param deptId 主键
     * @return 影响行数
     */
    int deleteById(Long deptId);

}
