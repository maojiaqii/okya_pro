package top.okya.component.constants;

/**
 * @author: maojiaqi
 * @Date: 2023/11/9 17:03
 * @describe: 字典常量
 */

public class SqlConstants {

    public static final String script_start = "<script>";
    public static final String script_end = "</script>";
    public static final String where = "where";
    public static final String and = "and";
    public static final String set = "set";
    public static final String is_delete = "is_delete";
    public static final String set_is_deleted = "is_delete = '1'";
    public static final String json_handler = ",typeHandler=top.okya.component.utils.mybatis.JsonTypeHandler";
    public static final String[] TABLE_PREFIX = new String[]{"as_", "bs_"};
    public static final String create_by = "create_by";
    public static final String create_time = "create_time";
    public static final String update_by = "update_by";
    public static final String update_time = "update_time";
}
