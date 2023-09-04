package top.okya.system.dao;

import top.okya.system.domain.AsLoginRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 系统登录记录(AsLoginRecord)表数据库访问层
 *
 * @author mjq
 * @since 2023-07-21 16:11:42
 */
@Mapper
public interface AsLoginRecordMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param infoId 主键
     * @return 实例对象
     */
    AsLoginRecord queryById(Long infoId);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<AsLoginRecord> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param asLoginRecord 实例对象
     * @return 对象列表
     */
    List<AsLoginRecord> queryAll(AsLoginRecord asLoginRecord);

    /**
     * 新增数据
     *
     * @param asLoginRecord 实例对象
     * @return 影响行数
     */
    int insert(AsLoginRecord asLoginRecord);

    /**
     * 修改数据
     *
     * @param asLoginRecord 实例对象
     * @return 影响行数
     */
    int update(AsLoginRecord asLoginRecord);

    /**
     * 通过主键删除数据
     *
     * @param infoId 主键
     * @return 影响行数
     */
    int deleteById(Long infoId);

}
