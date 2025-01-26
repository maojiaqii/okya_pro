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
}
