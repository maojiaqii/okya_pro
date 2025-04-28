package top.okya.system.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.okya.component.config.OkyaConfig;
import top.okya.component.domain.DictData;
import top.okya.component.domain.TableData;
import top.okya.component.domain.vo.DictDataVo;
import top.okya.component.domain.vo.FormDataVo;
import top.okya.component.domain.vo.TableDataVo;
import top.okya.component.domain.vo.others.table.TablePageVo;
import top.okya.component.enums.exception.ServiceExceptionType;
import top.okya.component.exception.ServiceException;
import top.okya.component.exception.check.CheckAndThrowExceptions;
import top.okya.component.utils.common.IdUtil;
import top.okya.component.utils.mybatis.JsonResultHandler;
import top.okya.system.dao.AsDictionaryMapper;
import top.okya.system.dao.AsFormMapper;
import top.okya.system.dao.AsTableMapper;
import top.okya.system.dao.SqlProviderMapper;
import top.okya.system.domain.AsDictionary;
import top.okya.system.domain.AsForm;
import top.okya.system.domain.AsTable;
import top.okya.system.service.DataService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: maojiaqi
 * @Date: 2024/12/6 22:09
 * @describe: 数据接口实现类
 */

@Service
@Slf4j
public class DataServiceImpl implements DataService {

    @Autowired
    AsTableMapper asTableMapper;
    @Autowired
    SqlProviderMapper sqlProviderMapper;
    @Autowired
    AsDictionaryMapper asDictionaryMapper;
    @Autowired
    private AsFormMapper asFormMapper;
    @Autowired
    CheckAndThrowExceptions checkAndThrowExceptions;

    @Override
    public TableData getTableData(TableDataVo tableDataVo) {
        String tableCode = tableDataVo.getTableCode();
        AsTable asTable = asTableMapper.queryByCode(tableCode);
        checkAndThrowExceptions.checkDbResult(asTable);
        String tableSource = asTable.getTableSource();
        // 分页
        TablePageVo tablePageVo = tableDataVo.getPage();
        if (!Objects.isNull(tablePageVo)) {
            PageHelper.startPage(tablePageVo.getCurrentPage(), tablePageVo.getPageSize());
        }

        // 查询
        Map<String, Object> query = tableDataVo.getParams();
        query.put("sqlToExecute", tableSource);
        query.put("orderToAppend", tableDataVo.getOrder());
        List<Map<String, Object>> tableData = sqlProviderMapper.query(query);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(tableData);
        long total = pageInfo.getTotal();
        return new TableData(total, tableData);
    }

    @Override
    public DictData getDictData(DictDataVo dictDataVo) {
        String dictCode = dictDataVo.getDictCode();
        AsDictionary asDictionary = asDictionaryMapper.queryByCode(dictCode);
        checkAndThrowExceptions.checkDbResult(asDictionary);
        String dictSource = asDictionary.getDictSource();
        // 查询
        Map<String, Object> query = dictDataVo.getParams();
        query.put("sqlToExecute", dictSource);
        List<Map<String, Object>> dictData = sqlProviderMapper.query(query);
        return new DictData(dictCode, asDictionary.getDictValue(), asDictionary.getDictLabel(), asDictionary.getDictPid(), asDictionary.getShowStyle(), dictData);
    }

    @Override
    public Map<String, Object> getFormData(FormDataVo formDataVo) {
        List<Map<String, Object>> data = formDataVo.getData();
        if (data == null || data.isEmpty()) {
            throw new ServiceException(ServiceExceptionType.SERVER_EXCEPTION, "data is empty");
        }
        AsForm asForm = getAsForm(formDataVo.getFormCode());
        JSONObject dbMapping = asForm.getDbMapping();
        dbMapping.put("content", data.get(0));
        JsonResultHandler jsonResultHandler = new JsonResultHandler(dbMapping);
        sqlProviderMapper.getFormData(dbMapping, jsonResultHandler);
        List<Map<String, Object>> resultList = jsonResultHandler.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveData(FormDataVo formDataVo) {
        AsForm asForm = getAsForm(formDataVo.getFormCode());
        JSONObject dbMapping = asForm.getDbMapping();
        List<JSONObject> mapping = dbMapping.getList("mapping", JSONObject.class);
        List<Map<String, Object>> data = formDataVo.getData();
        for (Map<String, Object> a : data) {
            dbMapping.put("content", a);
            AtomicBoolean isUpdate = new AtomicBoolean(false);
            mapping.forEach(v -> {
                if (v.containsKey("isKey") && (boolean) v.get("isKey")) {
                    String formField = (String) v.get("formField");
                    if(a.containsKey(formField) && !Objects.isNull(a.get(formField))){
                        isUpdate.set(true);
                    } else {
                        a.put(formField, IdUtil.randomUUID());
                    }
                }
            });
            if(isUpdate.get()){
                sqlProviderMapper.update(dbMapping);
            } else {
                sqlProviderMapper.insert(dbMapping);
            }
        }
        return "保存成功！";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteData(FormDataVo formDataVo) {
        AsForm asForm = getAsForm(formDataVo.getFormCode());
        JSONObject dbMapping = asForm.getDbMapping();
        List<JSONObject> mapping = dbMapping.getList("mapping", JSONObject.class);
        List<Map<String, Object>> data = formDataVo.getData();
        for (Map<String, Object> a : data) {
            dbMapping.put("content", a);
            if(Objects.equals(OkyaConfig.getDeletionType(), "L")){
                sqlProviderMapper.deleteLogic(dbMapping);
            } else {
                sqlProviderMapper.delete(dbMapping);
            }
        }
        return "删除成功！";
    }

    private AsForm getAsForm(String formCode) {
        AsForm asForm = asFormMapper.queryByCode(formCode);
        checkAndThrowExceptions.checkDbResult(asForm);
        JSONObject dbMapping = asForm.getDbMapping();
        if (dbMapping == null) {
            throw new ServiceException(ServiceExceptionType.DESIGN_ERROR, "表单", "db_mapping数据库映射关系为空！");
        }
        return asForm;
    }
}
