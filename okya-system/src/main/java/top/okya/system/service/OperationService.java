package top.okya.system.service;

import top.okya.system.domain.AsOperLog;

import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2023/7/25 17:46
 * @describe: 操作日志相关业务
 */

public interface OperationService {

    /**
     * 新增系统操作日志
     *
     * @param asOperLog 操作日志对象
     * @return 插入条数
     */
    int insertOperationRecord(AsOperLog asOperLog);

}
