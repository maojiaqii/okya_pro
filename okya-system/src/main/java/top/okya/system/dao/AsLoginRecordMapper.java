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
     * 新增数据
     *
     * @param asLoginRecord 实例对象
     * @return 影响行数
     */
    int insert(AsLoginRecord asLoginRecord);
}
