package top.okya.system.service.impl;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.okya.component.enums.exception.ServiceExceptionType;
import top.okya.component.exception.ServiceException;
import top.okya.system.dao.AsFormMapper;
import top.okya.system.dao.AsJsMapper;
import top.okya.system.dao.AsTableMapper;
import top.okya.system.domain.AsForm;
import top.okya.system.domain.AsJs;
import top.okya.system.domain.AsTable;
import top.okya.system.service.UiService;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    public Map<String, Object> getTableInfo(String tableCode) {
        AsTable asTable = asTableMapper.queryByCode(tableCode);
        if (asTable == null) {
            throw new ServiceException(ServiceExceptionType.UNKNOWN, "表格", tableCode);
        }
        JSONObject jsonObject = asTable.getProps();
        jsonObject.put("buttons", asTable.getButtons());
        jsonObject.put("columns", asTable.getColumns());
        return jsonObject;
    }

    @Override
    public Map<String, Object> getFormInfo(String formCode) {
        AsForm asForm = asFormMapper.queryByCode(formCode);
        if (asForm == null) {
            throw new ServiceException(ServiceExceptionType.UNKNOWN, "表单", formCode);
        }
        JSONObject jsonObject = asForm.getProps();
        if (!jsonObject.containsKey("schema")){
            throw new ServiceException(ServiceExceptionType.DESIGN_ERROR, "表单", "缺少schema节点");
        }
        Map<String, Object> schema = (HashMap<String, Object>) jsonObject.get("schema");
        schema.put("formItems", asForm.getFormItems());
        schema.put("formValidators", asForm.getFormValidators());
        schema.put("lifecycle", asForm.getLifecycle());
        return jsonObject;
    }

    @Override
    public AsJs getJsInfo(String jsCode) {
        AsJs asJs = asJsMapper.queryByCode(jsCode);
        if (asJs == null) {
            throw new ServiceException(ServiceExceptionType.UNKNOWN, "js代码", jsCode);
        }
        return asJs;
    }
}
