package top.okya.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.okya.component.utils.spring.SpringUtil;
import top.okya.system.dao.AsOperLogMapper;
import top.okya.system.domain.AsOperLog;
import top.okya.system.service.OperationService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author: maojiaqi
 * @Date: 2023/7/25 17:47
 * @describe: 操作日志相关Service实现
 */

@Service
public class OperationServiceImpl implements OperationService {

    @Autowired
    AsOperLogMapper asOperLogMapper;

    @Override
    public int insertOperationRecord(AsOperLog asOperLog) {
        return asOperLogMapper.insert(asOperLog);
    }

    @Override
    public List<AsOperLog> queryAll() {
        return asOperLogMapper.queryAll(new AsOperLog());
    }
}
