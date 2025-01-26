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
     * 通过userID查询数据
     *
     * @param userId 用户编号
     * @return 实例对象
     */
    List<AsRole> queryByUserId(String userId);

}
