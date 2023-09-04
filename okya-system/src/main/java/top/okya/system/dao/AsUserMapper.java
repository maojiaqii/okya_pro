package top.okya.system.dao;

import top.okya.component.domain.dto.AsUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 用户信息表(AsUser)表数据库访问层
 *
 * @author mjq
 * @since 2023-07-19 15:20:52
 */
@Mapper
public interface AsUserMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param userCode 用户代码
     * @return 实例对象
     */
    AsUser queryByUserCode(String userCode);

    /**
     * 通过ID查询单条数据
     *
     * @param userId 主键
     * @return 实例对象
     */
    AsUser queryById(Long userId);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AsUser> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param asUser 实例对象
     * @return 对象列表
     */
    List<AsUser> queryAll(AsUser asUser);

    /**
     * 新增数据
     *
     * @param asUser 实例对象
     * @return 影响行数
     */
    int insert(AsUser asUser);

    /**
     * 修改数据
     *
     * @param asUser 实例对象
     * @return 影响行数
     */
    int update(AsUser asUser);

    /**
     * 通过主键删除数据
     *
     * @param userId 主键
     * @return 影响行数
     */
    int deleteById(Long userId);

}
