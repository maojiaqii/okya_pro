package top.okya.system.dao;

import top.okya.component.domain.dto.AsMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
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
     * 通过用户id查询菜单
     *
     * @param userId 用户id
     * @return 查询结果
     */
    List<AsMenu> selectMenuListByUserId(Long userId);

    /**
     * 通过用户id查询按钮
     *
     * @param userId 用户id
     * @return 查询结果
     */
    List<AsMenu> selectButtonListByUserId(Long userId);
}
