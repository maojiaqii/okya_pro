package top.okya.component.utils.common;

import org.apache.commons.lang3.StringUtils;
import top.okya.component.constants.SqlPlaceholderConstants;
import top.okya.component.domain.LoginUser;
import top.okya.component.global.Global;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: maojiaqi
 * @Date: 2024/12/2 17:23
 * @describe：根据Map中的值替换SQL中的占位符
 */
public class SqlRender {

    private static final String NULL_STR = "NULL";

    /**
     * 替换SQL模板中的占位符
     * @param sqlTemplate SQL模板，包含占位符如 ${departmentId}
     * @param params 查询参数，包含占位符的实际值
     * @return 替换后的完整SQL语句
     */
    public String processSql(String sqlTemplate, Map<String, String> params) {
        // 使用正则表达式查找所有的占位符（如 ${xxx}）
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(sqlTemplate);
        StringBuilder finalSql = new StringBuilder();
        int lastEnd = 0;

        // 遍历所有匹配到的占位符
        while (matcher.find()) {
            // 获取占位符的名称
            String placeholder = matcher.group(1);

            // 获取该占位符的实际值
            String value = params.get(placeholder);

            // 处理替换逻辑
            if (value != null) {
                // 如果值存在，则替换占位符
                finalSql.append(sqlTemplate, lastEnd, matcher.start());
                finalSql.append(value);
            }
            // 如果占位符没有对应的值，则忽略该条件，跳过占位符
            lastEnd = matcher.end();
        }

        // 如果还有剩余的 SQL 内容，添加到最终结果中
        finalSql.append(sqlTemplate.substring(lastEnd));

        // 清理多余的 AND 或 OR 条件
        return cleanSql(finalSql.toString());
    }

    /**
     * 清理 SQL 语句中的多余的 AND 或 OR 语句，确保 SQL 是有效的
     * @param sql 完整的SQL语句
     * @return 清理后的SQL语句
     */
    private String cleanSql(String sql) {
        // 去掉尾部多余的 AND 或 OR
        sql = sql.replaceAll("\\s+AND\\s*$", "");
        sql = sql.replaceAll("\\s+OR\\s*$", "");

        // 去掉 WHERE 1=1 的无效条件
        sql = sql.replaceAll("WHERE 1=1\\s+AND", "WHERE");

        // 替换系统级参数
        LoginUser loginUser = Global.getLoginUser();

        return sql.replace(SqlPlaceholderConstants.USER_CODE, loginUser.getUserCode())
                .replace(SqlPlaceholderConstants.DEPT_CODE, loginUser.getAsDept().getDeptCode()).trim();
    }

    /**
     * 根据Map中的值替换SQL中的占位符
     *
     * @param sqlTemplate 原始SQL模板
     * @param params 替换的参数
     * @return 替换后的SQL
     */
    public static String render(String sqlTemplate, Map<String, Object> params) {
        if (StringUtils.isBlank(sqlTemplate)) {
            return sqlTemplate;
        }
        // 遍历Map，逐一替换占位符
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String placeholder = SqlPlaceholderConstants.FILTER_VALUE.replace("*", entry.getKey());
                String value = entry.getValue() == null ? NULL_STR : entry.getValue().toString();
                // 防止 SQL 注入
                value = escapeSql(value);
                sqlTemplate = sqlTemplate.replace(placeholder, value);
            }
        }
        LoginUser loginUser = Global.getLoginUser();
        return sqlTemplate.replace(SqlPlaceholderConstants.USER_CODE, loginUser.getUserCode())
                .replace(SqlPlaceholderConstants.DEPT_CODE, loginUser.getAsDept().getDeptCode());
    }

    /**
     * 简单的SQL值转义，防止SQL注入
     * @param value 需要转义的值
     * @return 转义后的值
     */
    private static String escapeSql(String value) {
        return value.replace("'", "''");
    }
}
