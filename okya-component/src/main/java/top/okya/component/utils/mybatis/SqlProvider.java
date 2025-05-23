package top.okya.component.utils.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.okya.component.constants.CharacterConstants;
import top.okya.component.constants.FlowConstants;
import top.okya.component.constants.SqlConstants;
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

    public String selectSql(HashMap<String, Object> map) {
        assert !Objects.isNull(map.get("sqlToExecute"));
        StringBuilder sql = new StringBuilder(map.get("sqlToExecute").toString());
        return getOrderString(map, sql);
    }

    public String queryWorkFlowTodo(HashMap<String, Object> map) {
        assert !Objects.isNull(map.get("sqlToExecute"));
        String sqlToExecute = map.get("sqlToExecute").toString();
        String sql = "select flow_business_detail_select.*,\n" +
                "flow_afbi.proc_inst_id, flow_afbi.business_table, flow_afbi.flow_code, flow_afbi.flow_version, flow_afbi.flow_current_node_id, flow_afbi.flow_current_node_name, flow_afbi.flow_start_time, flow_afbi.flow_end_time, \n" +
                "flow_art.ID_ task_id, flow_art.CREATE_TIME_ task_start_time, flow_art.DESCRIPTION_ task_in_method,\n" +
                "flow_start_au.user_id flow_start_user_id, flow_start_au.user_name flow_start_user_name, flow_start_ad.dept_id flow_start_dept_id, flow_start_ad.dept_name flow_start_dept_name,\n" +
                "flow_current_au.user_id flow_current_user_id, flow_current_au.user_name flow_current_user_name, flow_current_ad.dept_id flow_current_dept_id, flow_current_ad.dept_name flow_current_dept_name from\n" +
                "(%s) flow_business_detail_select\n" +
                "left join as_flow_business_info flow_afbi\n" +
                "on flow_afbi.flow_business_key = flow_business_detail_select.flow_business_key \n" +
                "left join as_user flow_start_au\n" +
                "on flow_start_au.user_id = flow_afbi.start_user_id \n" +
                "left join as_dept flow_start_ad\n" +
                "on flow_start_ad.dept_id = flow_start_au.dept_id\n" +
                "left join ACT_RU_TASK flow_art\n" +
                "on flow_art.PROC_INST_ID_ = flow_afbi.proc_inst_id\n" +
                "and flow_art.SUSPENSION_STATE_ = 1\n" +
                "left join as_user flow_current_au\n" +
                "on flow_current_au.user_id = flow_art.ASSIGNEE_\n" +
                "left join as_dept flow_current_ad\n" +
                "on flow_current_ad.dept_id = flow_current_au.dept_id\n" +
                "<where>\n" +
                "\t((flow_afbi.proc_inst_id is null \n" +
                "\t<if test=\"flowCurrentUser != null and flowCurrentUser != ''\">\n" +
                "        and flow_business_detail_select.create_by = #{flowCurrentUser}\n" +
                "    </if>)\n" +
                "\tor (flow_art.ID_ is not null\n" +
                "\t<if test=\"flowCurrentUser != null and flowCurrentUser != ''\">\n" +
                "        and flow_current_au.user_id = #{flowCurrentUser}\n" +
                "    </if>\n" +
                "\t<if test=\"flowStartUser != null and flowStartUser != ''\">\n" +
                "        and (flow_start_au.user_name like concat('%', #{flowStartUser}, '%') or flow_start_au.user_code = #{flowStartUser})\n" +
                "    </if>\n" +
                "    <if test=\"minFlowStartTime != null and minFlowStartTime != ''\">\n" +
                "        and flow_afbi.flow_start_time &gt;= #{minFlowStartTime}\n" +
                "    </if>\n" +
                "    <if test=\"maxFlowStartTime != null and maxFlowStartTime != ''\">\n" +
                "        and flow_afbi.flow_start_time &lt;= #{maxFlowStartTime}\n" +
                "    </if>\n" +
                "    <if test=\"flowIsReject == 0\">\n" +
                "        and flow_art.DESCRIPTION_ = '" + FlowConstants.FROM_APPROVE + "'\n" +
                "    </if>\n" +
                "    <if test=\"flowIsReject == 1\">\n" +
                "        and flow_art.DESCRIPTION_ = '" + FlowConstants.FROM_REJECT + "'\n" +
                "    </if>\n" +
                "\t))\n" +
                "</where>";
        return getOrderString(map, new StringBuilder(sql.replace("%s", sqlToExecute)));
    }

    public String queryWorkFlowDone(HashMap<String, Object> map) {
        assert !Objects.isNull(map.get("sqlToExecute"));
        String sqlToExecute = map.get("sqlToExecute").toString();
        String sql = "select flow_business_detail_select.*,\n" +
                "flow_afbi.proc_inst_id, flow_afbi.business_table, flow_afbi.flow_code, flow_afbi.flow_version, flow_afbi.flow_current_node_id, flow_afbi.flow_current_node_name, flow_afbi.flow_start_time, flow_afbi.flow_end_time, \n" +
                "flow_aht.ID_ task_id, flow_aht.name_ task_name, flow_aht.DESCRIPTION_ task_in_method, flow_ahc.MESSAGE_ task_comment, flow_aht.START_TIME_ task_start_time, flow_aht.end_TIME_ task_end_time,\n" +
                "flow_start_au.user_id flow_start_user_id, flow_start_au.user_name flow_start_user_name, flow_start_ad.dept_id flow_start_dept_id, flow_start_ad.dept_name flow_start_dept_name,\n" +
                "flow_complete_au.user_id flow_complete_user_id, flow_complete_au.user_name flow_complete_user_name, flow_complete_ad.dept_id flow_complete_dept_id, flow_complete_ad.dept_name flow_complete_dept_name from\n" +
                "(%s) flow_business_detail_select\n" +
                "left join as_flow_business_info flow_afbi\n" +
                "on flow_afbi.flow_business_key = flow_business_detail_select.flow_business_key \n" +
                "left join as_user flow_start_au\n" +
                "on flow_start_au.user_id = flow_afbi.start_user_id \n" +
                "left join as_dept flow_start_ad\n" +
                "on flow_start_ad.dept_id = flow_start_au.dept_id\n" +
                "left join act_hi_taskinst flow_aht\n" +
                "on flow_aht.PROC_INST_ID_ = flow_afbi.proc_inst_id\n" +
                "and flow_aht.DELETE_REASON_ = 'completed'\n" +
                "left join act_hi_comment flow_ahc \n" +
                "on flow_ahc.TASK_ID_ = flow_aht.ID_ \n" +
                "left join as_user flow_complete_au\n" +
                "on flow_complete_au.user_id = flow_aht.ASSIGNEE_\n" +
                "left join as_dept flow_complete_ad\n" +
                "on flow_complete_ad.dept_id = flow_complete_au.dept_id\n" +
                "<where>\n" +
                "\t<if test=\"flowCurrentUser != null and flowCurrentUser != ''\">\n" +
                "        and flow_complete_au.user_id = #{flowCurrentUser}\n" +
                "    </if>\n" +
                "\t<if test=\"flowStartUser != null and flowStartUser != ''\">\n" +
                "        and (flow_start_au.user_name like concat('%', #{flowStartUser}, '%') or flow_start_au.user_code = #{flowStartUser})\n" +
                "    </if>\n" +
                "    <if test=\"minFlowStartTime != null and minFlowStartTime != ''\">\n" +
                "        and flow_afbi.flow_start_time &gt;= #{minFlowStartTime}\n" +
                "    </if>\n" +
                "    <if test=\"maxFlowStartTime != null and maxFlowStartTime != ''\">\n" +
                "        and flow_afbi.flow_start_time &lt;= #{maxFlowStartTime}\n" +
                "    </if>\n" +
                "    <if test=\"flowIsEnd == 0\">\n" +
                "        and flow_afbi.flow_end_time is null\n" +
                "    </if>\n" +
                "    <if test=\"flowIsEnd == 1\">\n" +
                "        and flow_afbi.flow_end_time is not null\n" +
                "    </if>\n" +
                "</where>";
        return getOrderString(map, new StringBuilder(sql.replace("%s", sqlToExecute)));
    }

    public String queryWorkFlowBelongToMe(HashMap<String, Object> map) {
        assert !Objects.isNull(map.get("sqlToExecute"));
        String sqlToExecute = map.get("sqlToExecute").toString();
        String sql = "select flow_business_detail_select.*,\n" +
                "flow_afbi.proc_inst_id, flow_afbi.business_table, flow_afbi.flow_code, flow_afbi.flow_version, flow_afbi.flow_current_node_id, flow_afbi.flow_current_node_name, flow_afbi.flow_start_time, flow_afbi.flow_end_time, \n" +
                "flow_art.ID_ task_id,\n" +
                "flow_start_au.user_id flow_start_user_id, flow_start_au.user_name flow_start_user_name, flow_start_ad.dept_id flow_start_dept_id, flow_start_ad.dept_name flow_start_dept_name,\n" +
                "flow_current_au.user_id flow_current_user_id, flow_current_au.user_name flow_current_user_name, flow_current_ad.dept_id flow_current_dept_id, flow_current_ad.dept_name flow_current_dept_name from\n" +
                "(%s) flow_business_detail_select\n" +
                "left join as_flow_business_info flow_afbi\n" +
                "on flow_afbi.flow_business_key = rrrrrrr12rrrrrrrrrrrrrr222rrreeeew334342rrrrrrrrrrrrrrrrrr.flow_business_key \n" +
                "left join as_user flow_start_au\n" +
                "on flow_start_au.user_id = flow_afbi.start_user_id \n" +
                "left join as_dept flow_start_ad\n" +
                "on flow_start_ad.dept_id = flow_start_au.dept_id\n" +
                "left join ACT_RU_TASK flow_art\n" +
                "on flow_art.PROC_INST_ID_ = flow_afbi.proc_inst_id\n" +
                "and flow_art.SUSPENSION_STATE_ = 1\n" +
                "left join as_user flow_current_au\n" +
                "on flow_current_au.user_id = flow_art.ASSIGNEE_\n" +
                "left join as_dept flow_current_ad\n" +
                "on flow_current_ad.dept_id = flow_current_au.dept_id\n";
        return getOrderString(map, new StringBuilder(sql.replace("%s", sqlToExecute)));
    }

    private String getOrderString(HashMap<String, Object> map, StringBuilder sql) {
        if (!Objects.isNull(map.get("orderToAppend"))) {
            sql.append(" order by ");
            List<TableOrderVo> tableOrderVos = (List<TableOrderVo>) map.get("orderToAppend");
            for (TableOrderVo tableOrderVo : tableOrderVos) {
                sql.append(tableOrderVo.getCol()).append(CharacterConstants.BLANK_SPACE).append(tableOrderVo.getOrder()).append(CharacterConstants.COMMA);
            }
            sql.deleteCharAt(sql.length() - 1);
        }
        String mybatisSql = SqlConstants.script_start + sql + SqlConstants.script_end;
        log.info("待执行的sql：{}", mybatisSql);
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
                insertValuesSql.append(CharacterConstants.POUND + CharacterConstants.BRACES_LEFT + "content.").append(formField).append(isJson ? SqlConstants.json_handler : "").append(CharacterConstants.BRACES_RIGHT).append(CharacterConstants.COMMA).append(CharacterConstants.BLANK_SPACE);
            }
        });
        insertSql.delete(insertSql.length() - 2, insertSql.length());
        insertValuesSql.delete(insertValuesSql.length() - 2, insertValuesSql.length());
        insertSql.append(CharacterConstants.EXTENSION_RIGHT);
        insertValuesSql.append(CharacterConstants.EXTENSION_RIGHT);
        String mybatisSql = SqlConstants.script_start + insertSql + insertValuesSql + SqlConstants.script_end;
        log.info("待执行的sql：{}", mybatisSql);
        return mybatisSql;
    }

    public String deleteSql(HashMap<String, Object> map) {
        Map<String, Object> content = (Map<String, Object>) map.get("content");
        StringBuilder deleteSql = new StringBuilder("delete from ");
        deleteSql.append(map.get("dbTableName")).append(CharacterConstants.BLANK_SPACE).append(SqlConstants.where).append(CharacterConstants.BLANK_SPACE);
        ((List<Map<String, Object>>) map.get("mapping")).forEach(v -> {
            if (v.containsKey("isKey") && (boolean) v.get("isKey")) {
                String formField = (String) v.get("formField");
                if (content.containsKey(formField) && !Objects.isNull(content.get(formField))) {
                    deleteSql.append(v.get("dbColumn")).append(CharacterConstants.EQUAL).append(CharacterConstants.POUND).append(CharacterConstants.BRACES_LEFT).append("content.").append(formField).append(CharacterConstants.BRACES_RIGHT).append(CharacterConstants.BLANK_SPACE).append(SqlConstants.and).append(CharacterConstants.BLANK_SPACE);
                }
            }
        });
        deleteSql.delete(deleteSql.length() - 5, deleteSql.length());
        String mybatisSql = SqlConstants.script_start + deleteSql + SqlConstants.script_end;
        log.info("待执行的sql：{}", mybatisSql);
        return mybatisSql;
    }

    public String deleteSqlLogic(HashMap<String, Object> map) {
        Map<String, Object> content = (Map<String, Object>) map.get("content");
        StringBuilder deleteSql = new StringBuilder("update ");
        deleteSql.append(map.get("dbTableName"))
                .append(CharacterConstants.BLANK_SPACE)
                .append(SqlConstants.set)
                .append(CharacterConstants.BLANK_SPACE)
                .append(SqlConstants.set_is_deleted)
                .append(CharacterConstants.BLANK_SPACE)
                .append(SqlConstants.where).append(CharacterConstants.BLANK_SPACE);
        ((List<Map<String, Object>>) map.get("mapping")).forEach(v -> {
            if (v.containsKey("isKey") && (boolean) v.get("isKey")) {
                String formField = (String) v.get("formField");
                if (content.containsKey(formField) && !Objects.isNull(content.get(formField))) {
                    deleteSql.append(v.get("dbColumn")).append(CharacterConstants.EQUAL).append(CharacterConstants.POUND).append(CharacterConstants.BRACES_LEFT).append("content.").append(formField).append(CharacterConstants.BRACES_RIGHT).append(CharacterConstants.BLANK_SPACE).append(SqlConstants.and).append(CharacterConstants.BLANK_SPACE);
                }
            }
        });
        deleteSql.delete(deleteSql.length() - 5, deleteSql.length());
        String mybatisSql = SqlConstants.script_start + deleteSql + SqlConstants.script_end;
        log.info("待执行的sql：{}", mybatisSql);
        return mybatisSql;
    }

    public String updateSql(HashMap<String, Object> map) {
        Map<String, Object> content = (Map<String, Object>) map.get("content");
        StringBuilder updateSql = new StringBuilder("update ");
        updateSql.append(map.get("dbTableName"))
                .append(CharacterConstants.BLANK_SPACE)
                .append(SqlConstants.set)
                .append(CharacterConstants.BLANK_SPACE);
        StringBuilder updateWhereSql = new StringBuilder(" where ");
        ((List<Map<String, Object>>) map.get("mapping")).forEach(v -> {
            String formField = (String) v.get("formField");
            boolean isJson = !Objects.isNull(v.get("isJson")) && (boolean) v.get("isJson");
            if (content.containsKey(formField) && !Objects.isNull(content.get(formField))) {
                if (v.containsKey("isKey") && (boolean) v.get("isKey")) {
                    updateWhereSql.append(v.get("dbColumn")).append(CharacterConstants.EQUAL).append(CharacterConstants.POUND).append(CharacterConstants.BRACES_LEFT).append("content.").append(formField).append(CharacterConstants.BRACES_RIGHT).append(CharacterConstants.BLANK_SPACE).append(SqlConstants.and).append(CharacterConstants.BLANK_SPACE);
                } else {
                    updateSql.append(v.get("dbColumn")).append(CharacterConstants.EQUAL).append(CharacterConstants.POUND).append(CharacterConstants.BRACES_LEFT).append("content.").append(formField).append(isJson ? SqlConstants.json_handler : "").append(CharacterConstants.BRACES_RIGHT).append(CharacterConstants.COMMA).append(CharacterConstants.BLANK_SPACE);
                }
            }
        });
        updateSql.delete(updateSql.length() - 2, updateSql.length());
        updateWhereSql.delete(updateWhereSql.length() - 5, updateWhereSql.length());
        String mybatisSql = SqlConstants.script_start + updateSql + updateWhereSql + SqlConstants.script_end;
        log.info("待执行的sql：{}", mybatisSql);
        return mybatisSql;
    }

    public String getFormData(HashMap<String, Object> map) {
        Map<String, Object> content = (Map<String, Object>) map.get("content");
        StringBuilder seleteSql = new StringBuilder("select * from ");
        seleteSql.append(map.get("dbTableName"))
                .append(CharacterConstants.BLANK_SPACE)
                .append(SqlConstants.where)
                .append(CharacterConstants.BLANK_SPACE);
        ((List<Map<String, Object>>) map.get("mapping")).forEach(v -> {
            if (v.containsKey("isKey") && (boolean) v.get("isKey")) {
                String formField = (String) v.get("formField");
                if (content.containsKey(formField) && !Objects.isNull(content.get(formField))) {
                    seleteSql.append(v.get("dbColumn")).append(CharacterConstants.EQUAL).append(CharacterConstants.POUND).append(CharacterConstants.BRACES_LEFT).append("content.").append(formField).append(CharacterConstants.BRACES_RIGHT).append(CharacterConstants.BLANK_SPACE).append(SqlConstants.and).append(CharacterConstants.BLANK_SPACE);
                }
            }
        });
        seleteSql.delete(seleteSql.length() - 5, seleteSql.length());
        String mybatisSql = SqlConstants.script_start + seleteSql + SqlConstants.script_end;
        log.info("待执行的sql：{}", mybatisSql);
        return mybatisSql;
    }

}
