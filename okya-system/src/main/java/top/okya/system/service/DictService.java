package top.okya.system.service;

import top.okya.component.domain.vo.DictDataVo;

import java.util.Map;

/**
 * @author: maojiaqi
 * @Date: 2023/11/9 14:38
 * @describe: 字典Service
 */
public interface DictService {

    /**
     * 获取字典数据集
     *
     * @param dictDataVo
     * @return
     */
    Map<String, Object> getDictData(DictDataVo dictDataVo);
}
