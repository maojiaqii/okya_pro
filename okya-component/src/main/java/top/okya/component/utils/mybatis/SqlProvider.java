package top.okya.component.utils.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.okya.component.constants.CharacterConstants;
import top.okya.component.domain.vo.others.table.TableOrderVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: maojiaqi
 * @Date: 2024/12/7 22:31
 * @describe:
 */

@Component
@Slf4j
public class SqlProvider {

    private static final String script_start = "<script>";
    private static final String script_end = "</script>";
    private static final String where = "where";
    private static final String and = "and";
    private static final String set = "set";
    private static final String json_handler = ",typeHandler=top.okya.component.utils.mybatis.JsonTypeHandler";

    public String selectSql(HashMap<String, Object> map) {
        assert !Objects.isNull(map.get("sqlToExecute"));
        StringBuilder sql = new StringBuilder(map.get("sqlToExecute").toString());
        if (!Objects.isNull(map.get("orderToAppend"))) {
            sql.append(" order by ");
            List<TableOrderVo> tableOrderVos = (List<TableOrderVo>) map.get("orderToAppend");
            for (TableOrderVo tableOrderVo : tableOrderVos) {
                sql.append(tableOrderVo.getCol()).append(CharacterConstants.BLANK_SPACE).append(tableOrderVo.getOrder()).append(CharacterConstants.COMMA);
            }
            sql.deleteCharAt(sql.length() - 1);
        }
        String mybatisSql = script_start + sql + script_end;
        log.info("待执行的myBatisSql：{}", mybatisSql);
        return mybatisSql;
    }

    public String insertSql(HashMap<String, Object> map) {
        Map<String, Object> content = (Map<String, Object>) map.get("content");
        StringBuilder insertSql = new StringBuilder("insert into ");
        StringBuilder insertValuesSql = new StringBuilder("values (");
        insertSql.append(map.get("dbTableName")).append(CharacterConstants.BLANK_SPACE).append(CharacterConstants.EXTENSION_LEFT);
        ((List<Map<String, Object>>) map.get("mapping")).forEach(v -> {
            String formField = (String) v.get("formField");
            boolean isJson = !Objects.isNull(v.get("isJson")) && (boolean) v.get("isJson");
            if (content.containsKey(formField) && !Objects.isNull(content.get(formField))) {
                insertSql.append(v.get("dbColumn")).append(CharacterConstants.COMMA).append(CharacterConstants.BLANK_SPACE);
                insertValuesSql.append(CharacterConstants.POUND + CharacterConstants.BRACES_LEFT + "content.").append(formField).append(isJson ? json_handler : "").append(CharacterConstants.BRACES_RIGHT).append(CharacterConstants.COMMA).append(CharacterConstants.BLANK_SPACE);
            }
        });
        insertSql.delete(insertSql.length() - 2, insertSql.length());
        insertValuesSql.delete(insertValuesSql.length() - 2, insertValuesSql.length());
        insertSql.append(CharacterConstants.EXTENSION_RIGHT);
        insertValuesSql.append(CharacterConstants.EXTENSION_RIGHT);
        String mybatisSql = script_start + insertSql + insertValuesSql + script_end;
        log.info("待执行的myBatisSql：{}", mybatisSql);
        return mybatisSql;
    }

    public String deleteSql(HashMap<String, Object> map) {
        Map<String, Object> content = (Map<String, Object>) map.get("content");
        StringBuilder deleteSql = new StringBuilder("delete from ");
        deleteSql.append(map.get("dbTableName")).append(CharacterConstants.BLANK_SPACE).append(where).append(CharacterConstants.BLANK_SPACE);
        ((List<Map<String, Object>>) map.get("mapping")).forEach(v -> {
            if (v.containsKey("isKey") && (boolean) v.get("isKey")) {
                String formField = (String) v.get("formField");
                if (content.containsKey(formField) && !Objects.isNull(content.get(formField))) {
                    deleteSql.append(v.get("dbColumn")).append(CharacterConstants.EQUAL).append(CharacterConstants.POUND).append(CharacterConstants.BRACES_LEFT).append("content.").append(formField).append(CharacterConstants.BRACES_RIGHT).append(CharacterConstants.BLANK_SPACE).append(and).append(CharacterConstants.BLANK_SPACE);
                }
            }
        });
        deleteSql.delete(deleteSql.length() - 5, deleteSql.length());
        String mybatisSql = script_start + deleteSql + script_end;
        log.info("待执行的myBatisSql：{}", mybatisSql);
        return mybatisSql;
    }

    public String updateSql(HashMap<String, Object> map) {
        Map<String, Object> content = (Map<String, Object>) map.get("content");
        StringBuilder updateSql = new StringBuilder("update ");
        updateSql.append(map.get("dbTableName"))
                .append(CharacterConstants.BLANK_SPACE)
                .append(set)
                .append(CharacterConstants.BLANK_SPACE);
        StringBuilder updateWhereSql = new StringBuilder(" where ");
        ((List<Map<String, Object>>) map.get("mapping")).forEach(v -> {
            String formField = (String) v.get("formField");
            boolean isJson = !Objects.isNull(v.get("isJson")) && (boolean) v.get("isJson");
            if (content.containsKey(formField) && !Objects.isNull(content.get(formField))) {
                if (v.containsKey("isKey") && (boolean) v.get("isKey")) {
                    updateWhereSql.append(v.get("dbColumn")).append(CharacterConstants.EQUAL).append(CharacterConstants.POUND).append(CharacterConstants.BRACES_LEFT).append("content.").append(formField).append(CharacterConstants.BRACES_RIGHT).append(CharacterConstants.BLANK_SPACE).append(and).append(CharacterConstants.BLANK_SPACE);
                } else {
                    updateSql.append(v.get("dbColumn")).append(CharacterConstants.EQUAL).append(CharacterConstants.POUND).append(CharacterConstants.BRACES_LEFT).append("content.").append(formField).append(isJson ? json_handler : "").append(CharacterConstants.BRACES_RIGHT).append(CharacterConstants.COMMA).append(CharacterConstants.BLANK_SPACE);
                }
            }
        });
        updateSql.delete(updateSql.length() - 2, updateSql.length());
        updateWhereSql.delete(updateWhereSql.length() - 5, updateWhereSql.length());
        String mybatisSql = script_start + updateSql + updateWhereSql + script_end;
        log.info("待执行的myBatisSql：{}", mybatisSql);
        return mybatisSql;
    }

    public String getFormData(HashMap<String, Object> map) {
        Map<String, Object> content = (Map<String, Object>) map.get("content");
        StringBuilder seleteSql = new StringBuilder("select * from ");
        seleteSql.append(map.get("dbTableName"))
                .append(CharacterConstants.BLANK_SPACE)
                .append(where)
                .append(CharacterConstants.BLANK_SPACE);
        ((List<Map<String, Object>>) map.get("mapping")).forEach(v -> {
            if (v.containsKey("isKey") && (boolean) v.get("isKey")) {
                String formField = (String) v.get("formField");
                if (content.containsKey(formField) && !Objects.isNull(content.get(formField))) {
                    seleteSql.append(v.get("dbColumn")).append(CharacterConstants.EQUAL).append(CharacterConstants.POUND).append(CharacterConstants.BRACES_LEFT).append("content.").append(formField).append(CharacterConstants.BRACES_RIGHT).append(CharacterConstants.BLANK_SPACE).append(and).append(CharacterConstants.BLANK_SPACE);
                }
            }
        });
        seleteSql.delete(seleteSql.length() - 5, seleteSql.length());
        String mybatisSql = script_start + seleteSql + script_end;
        log.info("待执行的myBatisSql：{}", mybatisSql);
        return mybatisSql;
    }

}
