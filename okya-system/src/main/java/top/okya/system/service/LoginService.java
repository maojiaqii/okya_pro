package top.okya.system.service;

import top.okya.component.domain.vo.LoginBody;
import top.okya.system.domain.AsLoginRecord;

/**
 * @author: maojiaqi
 * @Date: 2023/7/21 16:38
 * @describe: 登录相关Service
 */

public interface LoginService {

    /**
     * 用户登录
     *
     * @param loginBody 登录信息体
     * @return token
     */
    String login(LoginBody loginBody);

    /**
     * 新增系统登录日志
     *
     * @param asLoginRecord 登录日志对象
     * @return 插入条数
     */
    int insertLoginRecord(AsLoginRecord asLoginRecord);

}
