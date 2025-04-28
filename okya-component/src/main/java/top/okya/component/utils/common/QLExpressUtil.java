package top.okya.component.utils.common;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * QLExpress表达式工具类
 */
@Slf4j
@Component
public class QLExpressUtil {

    private static ExpressRunner expressRunner;

    @PostConstruct
    public void init() {
        log.info("初始化QLExpress表达式引擎");
        expressRunner = new ExpressRunner();

        try {
            // 注册自定义操作符
            registerOperators();

            // 注册自定义函数
            registerFunctions();

            log.info("QLExpress表达式引擎初始化完成");
        } catch (Exception e) {
            log.error("初始化QLExpress表达式引擎失败: {}", e.getMessage());
        }
    }

    /**
     * 注册自定义操作符
     */
    private void registerOperators() throws Exception {
        // 注册日期比较操作符
        expressRunner.addOperator("dateGt", new DateCompareOperator(">"));
        expressRunner.addOperator("dateLt", new DateCompareOperator("<"));
        expressRunner.addOperator("dateGe", new DateCompareOperator(">="));
        expressRunner.addOperator("dateLe", new DateCompareOperator("<="));
        expressRunner.addOperator("dateEq", new DateCompareOperator("=="));
        expressRunner.addOperator("dateNe", new DateCompareOperator("!="));

        // 注册字符串包含操作符
        expressRunner.addOperator("contains", new StringContainsOperator());
        expressRunner.addOperator("startsWith", new StringStartsWithOperator());
        expressRunner.addOperator("endsWith", new StringEndsWithOperator());
    }

    /**
     * 注册自定义函数
     */
    private void registerFunctions() throws Exception {
        // 注册日期函数
        expressRunner.addFunction("now", new DateNowFunction());
        expressRunner.addFunction("parseDate", new ParseDateFunction());
        expressRunner.addFunction("formatDate", new FormatDateFunction());

        // 注册字符串函数
        expressRunner.addFunction("isEmpty", new StringIsEmptyFunction());
        expressRunner.addFunction("isNotEmpty", new StringIsNotEmptyFunction());
        expressRunner.addFunction("length", new StringLengthFunction());

        // 注册数值函数 - 注意：round是QLExpress的内置函数，不需要重复注册
        expressRunner.addFunction("ceil", new MathCeilFunction());
        expressRunner.addFunction("floor", new MathFloorFunction());
    }

    /**
     * 执行表达式
     *
     * @param expression 表达式
     * @param context    上下文
     * @return 表达式执行结果
     */
    public static Object execute(String expression, IExpressContext<String, Object> context) {
        try {
            return expressRunner.execute(expression, context, null, true, false);
        } catch (Exception e) {
            log.error("执行表达式失败: {}, 错误: {}", expression, e.getMessage());
            return null;
        }
    }

    /**
     * 执行布尔表达式
     *
     * @param expression 表达式
     * @param variables  变量
     * @return 布尔结果
     */
    public static boolean executeBoolean(String expression, Map<String, Object> variables) {
        if (expression == null || expression.trim().isEmpty()) {
            return true; // 默认为真
        }

        try {
            // 处理${var}格式的表达式
            if (expression.contains("${")) {
                expression = expression.substring(2, expression.length() - 1);
            }

            IExpressContext<String, Object> context = new DefaultContext<>();
            if (variables != null) {
                variables.forEach(context::put);
            }
            Object result = execute(expression, context);
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            return false;
        } catch (Exception e) {
            log.error("执行布尔表达式失败: {}, 错误: {}", expression, e.getMessage());
            return false;
        }
    }

    /**
     * 处理${var}格式的表达式占位符
     *
     * @param expression 表达式
     * @param variables  变量
     * @return 处理后的表达式
     */
    private static String processPlaceholders(String expression, Map<String, Object> variables) {
        String result = expression;
        // 匹配${varName}格式
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\$\\{([^}]+)\\}");
        java.util.regex.Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            String placeholder = matcher.group(0); // 完整的${varName}
            String varName = matcher.group(1);     // 提取varName

            // 从变量映射中获取值
            Object value = variables.get(varName);

            if (value != null) {
                String replacement;
                if (value instanceof String) {
                    // 字符串需要添加引号
                    replacement = "'" + value.toString() + "'";
                } else {
                    // 数字、布尔值等不需要引号
                    replacement = value.toString();
                }
                result = result.replace(placeholder, replacement);
            } else {
                // 如果变量不存在，替换为null
                result = result.replace(placeholder, "null");
            }
        }

        return result;
    }

    /**
     * 日期比较操作符
     */
    static class DateCompareOperator extends Operator {
        private final String operatorType;

        public DateCompareOperator(String operatorType) {
            this.operatorType = operatorType;
        }

        @Override
        public Object executeInner(Object[] objects) throws Exception {
            if (objects.length != 2) {
                throw new Exception("日期比较操作符需要两个参数");
            }

            Date date1 = convertToDate(objects[0]);
            Date date2 = convertToDate(objects[1]);

            if (date1 == null || date2 == null) {
                return false;
            }

            int result = date1.compareTo(date2);

            switch (operatorType) {
                case ">":
                    return result > 0;
                case "<":
                    return result < 0;
                case ">=":
                    return result >= 0;
                case "<=":
                    return result <= 0;
                case "==":
                    return result == 0;
                case "!=":
                    return result != 0;
                default:
                    return false;
            }
        }

        private Date convertToDate(Object obj) {
            if (obj instanceof Date) {
                return (Date) obj;
            } else if (obj instanceof String) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return sdf.parse((String) obj);
                } catch (Exception e) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        return sdf.parse((String) obj);
                    } catch (Exception ex) {
                        return null;
                    }
                }
            } else if (obj instanceof Long) {
                return new Date((Long) obj);
            }
            return null;
        }
    }

    /**
     * 字符串包含操作符
     */
    static class StringContainsOperator extends Operator {
        @Override
        public Object executeInner(Object[] objects) throws Exception {
            if (objects.length != 2) {
                throw new Exception("字符串包含操作符需要两个参数");
            }

            if (objects[0] == null || objects[1] == null) {
                return false;
            }

            return objects[0].toString().contains(objects[1].toString());
        }
    }

    /**
     * 字符串开始于操作符
     */
    static class StringStartsWithOperator extends Operator {
        @Override
        public Object executeInner(Object[] objects) throws Exception {
            if (objects.length != 2) {
                throw new Exception("字符串开始于操作符需要两个参数");
            }

            if (objects[0] == null || objects[1] == null) {
                return false;
            }

            return objects[0].toString().startsWith(objects[1].toString());
        }
    }

    /**
     * 字符串结束于操作符
     */
    static class StringEndsWithOperator extends Operator {
        @Override
        public Object executeInner(Object[] objects) throws Exception {
            if (objects.length != 2) {
                throw new Exception("字符串结束于操作符需要两个参数");
            }

            if (objects[0] == null || objects[1] == null) {
                return false;
            }

            return objects[0].toString().endsWith(objects[1].toString());
        }
    }

    /**
     * 获取当前日期函数
     */
    static class DateNowFunction extends Operator {
        @Override
        public Object executeInner(Object[] objects) {
            return new Date();
        }
    }

    /**
     * 解析日期函数
     */
    static class ParseDateFunction extends Operator {
        @Override
        public Object executeInner(Object[] objects) throws Exception {
            if (objects.length < 1) {
                throw new Exception("parseDate函数至少需要一个参数");
            }

            if (objects[0] == null) {
                return null;
            }

            String dateStr = objects[0].toString();
            String format = "yyyy-MM-dd HH:mm:ss";

            if (objects.length > 1 && objects[1] != null) {
                format = objects[1].toString();
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.parse(dateStr);
            } catch (Exception e) {
                throw new Exception("日期解析失败: " + dateStr + ", 格式: " + format);
            }
        }
    }

    /**
     * 格式化日期函数
     */
    static class FormatDateFunction extends Operator {
        @Override
        public Object executeInner(Object[] objects) throws Exception {
            if (objects.length < 1) {
                throw new Exception("formatDate函数至少需要一个参数");
            }

            if (objects[0] == null) {
                return null;
            }

            Date date;
            if (objects[0] instanceof Date) {
                date = (Date) objects[0];
            } else if (objects[0] instanceof Long) {
                date = new Date((Long) objects[0]);
            } else {
                throw new Exception("formatDate函数第一个参数必须是日期类型");
            }

            String format = "yyyy-MM-dd HH:mm:ss";

            if (objects.length > 1 && objects[1] != null) {
                format = objects[1].toString();
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.format(date);
            } catch (Exception e) {
                throw new Exception("日期格式化失败: " + date + ", 格式: " + format);
            }
        }
    }

    /**
     * 字符串是否为空函数
     */
    static class StringIsEmptyFunction extends Operator {
        @Override
        public Object executeInner(Object[] objects) throws Exception {
            if (objects.length < 1) {
                throw new Exception("isEmpty函数需要一个参数");
            }

            if (objects[0] == null) {
                return true;
            }

            return objects[0].toString().isEmpty();
        }
    }

    /**
     * 字符串是否不为空函数
     */
    static class StringIsNotEmptyFunction extends Operator {
        @Override
        public Object executeInner(Object[] objects) throws Exception {
            if (objects.length < 1) {
                throw new Exception("isNotEmpty函数需要一个参数");
            }

            if (objects[0] == null) {
                return false;
            }

            return !objects[0].toString().isEmpty();
        }
    }

    /**
     * 字符串长度函数
     */
    static class StringLengthFunction extends Operator {
        @Override
        public Object executeInner(Object[] objects) throws Exception {
            if (objects.length < 1) {
                throw new Exception("length函数需要一个参数");
            }

            if (objects[0] == null) {
                return 0;
            }

            return objects[0].toString().length();
        }
    }

    /**
     * 向上取整函数
     */
    static class MathCeilFunction extends Operator {
        @Override
        public Object executeInner(Object[] objects) throws Exception {
            if (objects.length < 1) {
                throw new Exception("ceil函数需要一个参数");
            }

            if (objects[0] == null) {
                return 0;
            }

            if (objects[0] instanceof Number) {
                double value = ((Number) objects[0]).doubleValue();
                return Math.ceil(value);
            }

            throw new Exception("ceil函数参数必须是数值类型");
        }
    }

    /**
     * 向下取整函数
     */
    static class MathFloorFunction extends Operator {
        @Override
        public Object executeInner(Object[] objects) throws Exception {
            if (objects.length < 1) {
                throw new Exception("floor函数需要一个参数");
            }

            if (objects[0] == null) {
                return 0;
            }

            if (objects[0] instanceof Number) {
                double value = ((Number) objects[0]).doubleValue();
                return Math.floor(value);
            }

            throw new Exception("floor函数参数必须是数值类型");
        }
    }

    /**
     * 四舍五入函数
     */
    static class MathRoundFunction extends Operator {
        @Override
        public Object executeInner(Object[] objects) throws Exception {
            if (objects.length < 1) {
                throw new Exception("round函数需要一个参数");
            }

            if (objects[0] == null) {
                return 0;
            }

            if (objects[0] instanceof Number) {
                double value = ((Number) objects[0]).doubleValue();

                if (objects.length > 1 && objects[1] instanceof Number) {
                    int scale = ((Number) objects[1]).intValue();
                    double factor = Math.pow(10, scale);
                    return Math.round(value * factor) / factor;
                }

                return Math.round(value);
            }

            throw new Exception("round函数参数必须是数值类型");
        }
    }
} 