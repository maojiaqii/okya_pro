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
    List<AsPermission> queryPermissionsByUserId(@Param("userId") String userId, @Param("currentTenancy") String currentTenancy);

    /**
     * 通过用户id查询数据
     *
     * @param userId         用户id
     * @return 按钮权限标识列表
     */
    List<String> queryButtonsByUserId(@Param("userId") String userId);

    /**
     * 通过用户id查询数据
     *
     * @param userId         用户id
     * @return 表格列权限标识列表
     */
    List<String> queryColumnsByUserId(@Param("userId") String userId);

    /**
     * 通过用户id查询数据
     *
     * @param userId         用户id
     * @return 表单字段权限标识列表
     */
    List<String> queryFieldsByUserId(@Param("userId") String userId);

    /**
     * 通过角色ID查询租户
     *
     * @param  roleId
     * @return 实例对象
     */
    List<String> queryTenancysByRoleId(String roleId);

    /**
     * 通过角色ID查询菜单
     *
     * @param  roleId
     * @return 实例对象
     */
    List<String> queryPermissionsByRoleId(String roleId);

    /**
     * 通过角色ID查询按钮
     *
     * @param  roleId
     * @return 实例对象
     */
    List<String> queryButtonsByRoleId(String roleId);

    /**
     * 通过角色ID查询表格列
     *
     * @param  roleId
     * @return 实例对象
     */
    List<String> queryColumnsByRoleId(String roleId);

    /**
     * 通过角色ID查询表单字段
     *
     * @param  roleId
     * @return 实例对象
     */
    List<String> queryFieldsByRoleId(String roleId);

    int deleteTenancyByRoleId(String roleId);

    int deletePermissionByRoleId(String roleId);

    int deleteTableByRoleId(String roleId);

    int deleteButtonByRoleId(String roleId);

    int deleteFormByRoleId(String roleId);

    int saveRoleTenancy(@Param("roleId") String roleId, @Param("tenancy") String tenancy);

    int saveRolePermission(@Param("roleId") String roleId, @Param("permission") String permission);

    int saveRoleTable(@Param("roleId") String roleId, @Param("table") String table);

    int saveRoleButton(@Param("roleId") String roleId, @Param("button") String button);

    int saveRoleForm(@Param("roleId") String roleId, @Param("form") String form);
}
