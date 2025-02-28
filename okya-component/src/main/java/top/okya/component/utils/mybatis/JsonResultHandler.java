package top.okya.component.utils.mybatis;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: maojiaqi
 * @Date: 2025/2/28 16:17
 * @describe:
 */

public class JsonResultHandler implements ResultHandler<Map<String, Object>> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Map<String, Object>> resultList;
    private JSONObject dbMapping;

    public JsonResultHandler(JSONObject dbMapping) {
        this.dbMapping = dbMapping;
        resultList = new ArrayList<>();
    }

    @Override
    public void handleResult(ResultContext<? extends Map<String, Object>> resultContext) {
        List<Map<String, Object>> mappings = (List<Map<String, Object>>) dbMapping.get("mapping");
        Map<String, Object> row = resultContext.getResultObject();
        for(Map<String, Object> mapping : mappings){
            String formField = (String) mapping.get("formField");
            String dbColumn = (String) mapping.get("dbColumn");
            boolean isJson = !Objects.isNull(mapping.get("isJson")) && (boolean) mapping.get("isJson");
            if(row.containsKey(dbColumn)){
                try {
                    if(!row.containsKey(formField) && !Objects.equals(formField, dbColumn)){
                        row.put(formField, isJson ? objectMapper.readValue((String) row.get(dbColumn), Object.class) : row.get(dbColumn));
                    } else {
                        row.put(dbColumn, isJson ? objectMapper.readValue((String) row.get(dbColumn), Object.class) : row.get(dbColumn));
                    }
                } catch (JsonProcessingException e) {
                    // 解析失败时保留原始值
                }
            }
        }
        resultList.add(row);
    }

    public List<Map<String, Object>> getResultList() {
        return resultList;
    }
}
