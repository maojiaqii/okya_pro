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
     * 通过userID查询单条数据
     *
     * @param userId 用户编号
     * @return 实例对象
     */
    AsDept queryByUserId(String userId);
}
