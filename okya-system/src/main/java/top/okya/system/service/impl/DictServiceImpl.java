package top.okya.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.okya.component.constants.CharacterConstants;
import top.okya.component.constants.CommonConstants;
import top.okya.component.constants.DictConstants;
import top.okya.component.constants.LoginConstants;
import top.okya.component.domain.LoginUser;
import top.okya.component.domain.vo.DictDataVo;
import top.okya.component.enums.UseStatus;
import top.okya.component.enums.exception.ServiceExceptionType;
import top.okya.component.exception.ServiceException;
import top.okya.component.global.Global;
import top.okya.system.dao.AsDictionaryDataMapper;
import top.okya.system.dao.AsDictionaryMapper;
import top.okya.system.domain.AsDictionary;
import top.okya.system.service.DictService;

import java.util.*;

/**
 * @author: maojiaqi
 * @Date: 2023/11/9 15:22
 * @describe: 字典Service实现
 */

@Service
@Slf4j
public class DictServiceImpl implements DictService {
    /**
     * 字典类型 静态列表
     */
    private static final int DICT_TYPE_LIST = 0;
    /**
     * 字典类型 动态数据库表格
     */
    private static final int DICT_TYPE_TABLE = 1;

    @Autowired
    AsDictionaryMapper asDictionaryMapper;
    @Autowired
    AsDictionaryDataMapper asDictionaryDataMapper;

    @Override
    public Map<String, Object> getDictData(DictDataVo dictDataVo) {
        LoginUser loginUser = Global.getLoginUser();
        String dictCode = dictDataVo.getDictCode();
        AsDictionary asDictionary = asDictionaryMapper.queryByCode(dictCode);
        if (asDictionary != null) {
            int status = asDictionary.getStatus();
            if (Objects.equals(status, UseStatus.OK.getCode())) {
                // 配置在as_dictionary表中的固定的查询条件
                String sqlCondition = Optional.ofNullable(asDictionary.getDictParams()).orElse("");
                if (StringUtils.isNotBlank(asDictionary.getDictParams())) {
                    sqlCondition = CharacterConstants.BRACES_LEFT + sqlCondition.replace(LoginConstants.USER_CODE, loginUser.getUserCode()).replace(LoginConstants.DEPT_CODE, loginUser.getAsDept().getDeptCode()) + CharacterConstants.BRACES_RIGHT;
                }
                // 过滤搜索查询条件
                String filterSqlPrefix = asDictionary.getFilterSqlPrefix();
                if (StringUtils.isNotBlank(filterSqlPrefix)) {
                    sqlCondition += (StringUtils.isNotBlank(asDictionary.getDictParams()) ? (CharacterConstants.BLANK_SPACE + CommonConstants.AND + CharacterConstants.BLANK_SPACE) : "") + filterSqlPrefix.replace(DictConstants.FILTER_VALUE, dictDataVo.getFilterValue());
                }
                int pageNum = dictDataVo.getPageNum();
                if (!Objects.equals(pageNum, CommonConstants.ZERO_PAGE)) {
                    PageHelper.startPage(pageNum, dictDataVo.getPageSize());
                }
                List<Map<String, Object>> dictData = new ArrayList<>();
                List<Map<String, Object>> dictSelectedData = new ArrayList<>();
                Object selectedId = dictDataVo.getSelectedId();
                List<String> selectedIds = dictDataVo.getSelectedIds();
                String sqlConditionsForSelectedData = "";
                if(!Objects.isNull(selectedId)){
                    sqlConditionsForSelectedData = String.format("%s='%s'", asDictionary.getDictValue(), selectedId);
                } else if (!Objects.isNull(selectedIds)){
                    sqlConditionsForSelectedData = String.format("%s in ('%s')", asDictionary.getDictValue(), String.join("', '", selectedIds));
                }
                int dictType = asDictionary.getDictType();
                if (Objects.equals(dictType, DICT_TYPE_LIST)) {
                    dictData = asDictionaryDataMapper.queryByCode(dictCode, sqlCondition);
                    if(StringUtils.isNotBlank(sqlConditionsForSelectedData)){
                        dictSelectedData = asDictionaryDataMapper.queryByCode(dictCode, sqlConditionsForSelectedData);
                    }
                } else if (Objects.equals(dictType, DICT_TYPE_TABLE)) {
                    dictData = asDictionaryDataMapper.queryByTable(asDictionary.getDictSource(), sqlCondition);
                } else {
                    throw new ServiceException(ServiceExceptionType.SERVER_EXCEPTION, String.format("未知的字典类型：【%s】", asDictionary.getDictType()));
                }
                return ImmutableMap.of("dictCode", dictCode, "dictValue", asDictionary.getDictValue(), "dictLabel", asDictionary.getDictLabel(), "dictStyle", asDictionary.getShowStyle(), "data", dictData, "selectedData", dictSelectedData);
            } else if (Objects.equals(status, UseStatus.DISABLED.getCode())) {
                throw new ServiceException(ServiceExceptionType.DISABLED_DICTIONARY);
            } else if (Objects.equals(status, UseStatus.DELETED.getCode())) {
                throw new ServiceException(ServiceExceptionType.DELETED_DICTIONARY);
            }
            throw new ServiceException(ServiceExceptionType.SERVER_EXCEPTION, String.format("未知的字典状态：【%s】", asDictionary.getStatus()));
        }
        throw new ServiceException(ServiceExceptionType.UNKNOWN_DICTIONARY, dictCode);
    }

}
