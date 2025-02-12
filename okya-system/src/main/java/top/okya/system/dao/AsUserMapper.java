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
     * 通过用户id修改密码
     *
     * @param userId 用户id
     * @param pwd    密码
     */
    int updatePwdByUserId(@Param("userId") String userId, @Param("pwd") String pwd);

    int updateStatusByUserId(String userId);
}
