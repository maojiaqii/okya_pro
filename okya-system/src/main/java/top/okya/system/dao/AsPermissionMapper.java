package top.okya.system.dao;
 
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.okya.component.domain.dto.AsPermission;

import java.util.List;
import java.util.Map;

/**
 * 菜单权限表(AsPermission)表数据库访问层
 *
 * @author mjq
 * @since 2024-07-26 16:55:22
 */
@Mapper
public interface AsPermissionMapper {

    /**
     * 通过UserIdID查询用户的租户
     *
     * @param  userId 用户id
     * @return 实例对象
     */
    List<Map<String, Object>> queryTenancysByUserId(String userId);

    /**
     * 通过用户id查询数据
     *
     * @param userId         用户id
     * @param currentTenancy 当前租户
     * @return 对象列表
     */
    List<AsPermission> queryByUserId(@Param("userId") String userId, @Param("currentTenancy") String currentTenancy);
}
