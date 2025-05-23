package top.okya.system.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import top.okya.component.enums.exception.ServiceExceptionType;
import top.okya.component.exception.ServiceException;
import top.okya.component.exception.check.CheckAndThrowExceptions;
import top.okya.system.dao.AsFormMapper;
import top.okya.system.dao.AsJsMapper;
import top.okya.system.dao.AsTableMapper;
import top.okya.system.domain.AsForm;
import top.okya.system.domain.AsJs;
import top.okya.system.domain.AsTable;
import top.okya.system.service.UiService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author: maojiaqi
 * @Date: 2025/1/8 10:58
 * @describe: 页面Ui接口实现类
 */
@Service
@Slf4j
public class UiServiceImpl implements UiService {

    @Autowired
    AsTableMapper asTableMapper;
    @Autowired
    AsFormMapper asFormMapper;
    @Autowired
    AsJsMapper asJsMapper;
    @Autowired
    CheckAndThrowExceptions checkAndThrowExceptions;

    @Override
    public Map<String, Object> getTableInfo(String tableCode) {
        AsTable asTable = asTableMapper.queryByCode(tableCode);
        checkAndThrowExceptions.checkDbResult(asTable);
        JSONObject jsonObject = asTable.getProps();
        jsonObject.put("buttons", asTable.getButtons());
        JSONArray columns = Optional.ofNullable(asTable.getColumns()).orElse(new JSONArray());
        Integer workflowType = asTable.getWorkflowType();
        String flowJsonFile = "";
        switch (workflowType) {
            case 0:
                // 普通表格
                break;
            case 1:
                flowJsonFile = "classpath:json/table_todo.json";
                break;
            case 2:
                flowJsonFile = "classpath:json/table_done.json";
                break;
            case 3:
                flowJsonFile = "classpath:json/table_myOwn.json";
                break;
            default:
                throw new ServiceException(ServiceExceptionType.UNKNOWN_TYPE, "表格", "workflow_type", workflowType);
        }
        if (!flowJsonFile.isEmpty()) {
            try {
                File file = ResourceUtils.getFile(flowJsonFile);
                String json = FileUtils.readFileToString(file, "UTF-8");
                JSONArray flowColumns = JSONArray.parseArray(json);
                int operationIndex = columns.size();
                for (int j = 0; j < columns.size(); j++) {
                    if (Objects.equals(columns.getJSONObject(j).getString("type"), "operation")) {
                        operationIndex = j;
                        break;
                    }
                }
                columns.addAll(operationIndex, flowColumns);
            } catch (IOException e) {
                throw new ServiceException(ServiceExceptionType.SERVER_EXCEPTION, e);
            }
        }
        jsonObject.put("columns", columns);
        return jsonObject;
    }

    @Override
    public Map<String, Object> getFormInfo(String formCode) {
        AsForm asForm = asFormMapper.queryByCode(formCode);
        checkAndThrowExceptions.checkDbResult(asForm);
        JSONObject jsonObject = asForm.getProps();
        if (!jsonObject.containsKey("schema")) {
            throw new ServiceException(ServiceExceptionType.DESIGN_ERROR, "表单", "缺少schema节点");
        }
        JSONObject schema = jsonObject.getJSONObject("schema");
        JSONArray formItems = Optional.ofNullable(asForm.getFormItems()).orElse(new JSONArray());
        Integer workflowType = asForm.getWorkflowType();
        String flowJsonFile = "";
        switch (workflowType) {
            case 0:
                break;
            case 1:
                flowJsonFile = "classpath:json/form_todo.json";
                break;
            case 2:
                flowJsonFile = "classpath:json/form_done.json";
                break;
            case 3:
                flowJsonFile = "classpath:json/form_myOwn.json";
                break;
            default:
                throw new ServiceException(ServiceExceptionType.UNKNOWN_TYPE, "表单", "workflow_type", workflowType);
        }
        if (!flowJsonFile.isEmpty()) {
            try {
                File file = ResourceUtils.getFile(flowJsonFile);
                String json = FileUtils.readFileToString(file, "UTF-8");
                JSONArray flowFields = JSONArray.parseArray(json);
                formItems.addAll(flowFields);
            } catch (IOException e) {
                throw new ServiceException(ServiceExceptionType.SERVER_EXCEPTION, e);
            }
        }
        schema.put("formItems", formItems);
        schema.put("formValidators", asForm.getFormValidators());
        schema.put("lifecycle", asForm.getLifecycle());
        return jsonObject;
    }

    @Override
    public AsJs getJsInfo(String jsCode) {
        AsJs asJs = asJsMapper.queryByCode(jsCode);
        checkAndThrowExceptions.checkDbResult(asJs);
        return asJs;
    }
}
