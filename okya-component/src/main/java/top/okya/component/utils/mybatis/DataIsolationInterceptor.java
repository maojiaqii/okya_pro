package top.okya.component.utils.mybatis;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.values.ValuesStatement;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;
import top.okya.component.config.OkyaConfig;
import top.okya.component.constants.CharacterConstants;
import top.okya.component.constants.SqlConstants;
import top.okya.component.domain.LoginUser;
import top.okya.component.domain.dto.AsUser;
import top.okya.component.global.Global;
import top.okya.component.utils.common.DateFormatUtil;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author: maojiaqi
 * @Date: 2025/3/7 16:08
 * @describe:
 */

@Component
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataIsolationInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        //确保只有拦截的目标对象是 StatementHandler 类型时才执行特定逻辑
        if (target instanceof StatementHandler) {
            StatementHandler statementHandler = (StatementHandler) target;
            // 获取 BoundSql 对象，包含原始 SQL 语句
            BoundSql boundSql = statementHandler.getBoundSql();
            String originalSql = boundSql.getSql();
            String newSql = setEnvToStatement(originalSql);
            setConstantToSql(newSql);
            // 使用MetaObject对象将新的SQL语句设置到BoundSql对象中
            MetaObject metaObject = SystemMetaObject.forObject(boundSql);
            metaObject.setValue("sql", newSql);
        }
        // 执行SQL
        return invocation.proceed();
    }

    private void setConstantToSql(String sql) {
        if(Global.getLoginUser() != null){
            AsUser loginUser = Global.getLoginUser().getAsUser();
            sql = sql.replace("${userId}", String.format("'%s'", loginUser.getUserId()))
                    .replace("${deptId}", String.format("'%s'", loginUser.getDeptId()));
        }
    }

    private void divideSelectWhereExpression(PlainSelect selectBody) {
        if (selectBody.getFromItem() instanceof Table) {
            Expression newWhereExpression = selectBody.getWhere();
            if (selectBody.getJoins() == null || selectBody.getJoins().isEmpty()) {
                Table mainTable = selectBody.getFromItem(Table.class);
                String mainTableAlias = Objects.isNull(mainTable.getAlias()) ? null : mainTable.getAlias().getName();
                if(startsWithAny(mainTable.getName())) {
                    newWhereExpression = setEnvToWhereExpression(selectBody.getWhere(), mainTableAlias);
                }
            } else {
                // 如果是多表关联查询，在关联查询中新增每个表的环境变量条件
                newWhereExpression = multipleTableJoinWhereExpression(selectBody);
            }
            // 将新的where设置到Select中
            selectBody.setWhere(newWhereExpression);
        } else if (selectBody.getFromItem() instanceof SubSelect) {
            // 如果是子查询，在子查询中新增环境变量条件
            SubSelect subSelect = (SubSelect) selectBody.getFromItem();
            PlainSelect subSelectBody = subSelect.getSelectBody(PlainSelect.class);
            divideSelectWhereExpression(subSelectBody);
        }
    }

    private String setEnvToStatement(String originalSql) {
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(originalSql);
        } catch (JSQLParserException e) {
            throw new RuntimeException("EnvironmentVariableInterceptor::SQL语句解析异常：" + originalSql);
        }
        if (statement instanceof Select) {
            Select select = (Select) statement;
            PlainSelect selectBody = select.getSelectBody(PlainSelect.class);
            divideSelectWhereExpression(selectBody);
            // 获得修改后的语句
            return select.toString();
        } else if (statement instanceof Insert) {
            Insert insert = (Insert) statement;
            Table table = insert.getTable();
            if(startsWithAny(table.getName())){
                setEnvToInsert(insert);
            }
            return insert.toString();
        } else if (statement instanceof Update) {
            Update update = (Update) statement;
            Table table = update.getTable();
            String mainTableAlias = Objects.isNull(table.getAlias()) ? null : table.getAlias().getName();
            if(startsWithAny(table.getName())){
                setEnvToUpdate(update);
                Expression newWhereExpression = setEnvToWhereExpression(update.getWhere(), mainTableAlias);
                // 将新的where设置到Update中
                update.setWhere(newWhereExpression);
            }
            return update.toString();
        } else if (statement instanceof Delete) {
            Delete delete = (Delete) statement;
            Table table = delete.getTable();
            String mainTableAlias = Objects.isNull(table.getAlias()) ? null : table.getAlias().getName();
            if(startsWithAny(table.getName())){
                Expression newWhereExpression = setEnvToWhereExpression(delete.getWhere(), mainTableAlias);
                // 将新的where设置到delete中
                delete.setWhere(newWhereExpression);
            }
            return delete.toString();
        }
        return originalSql;
    }

    /**
     * 将需要隔离的字段加入到SQL的Where语法树中
     *
     * @param whereExpression SQL的Where语法树
     * @param alias           表别名
     * @return 新的SQL Where语法树
     */
    private Expression setEnvToWhereExpression(Expression whereExpression, String alias) {
        AndExpression andExpression = new AndExpression();
        EqualsTo envEquals = new EqualsTo();
        if(Objects.equals(OkyaConfig.getDeletionType(), "L")){
            envEquals.setLeftExpression(new Column(StringUtils.isNotBlank(alias) ? alias + CharacterConstants.PERIOD + SqlConstants.is_delete : SqlConstants.is_delete));
            envEquals.setRightExpression(new StringValue("0"));
        } else {
            return whereExpression;
        }
        if (whereExpression == null) {
            return envEquals;
        } else {
            // 将新的where条件加入到原where条件的右分支树
            andExpression.setRightExpression(envEquals);
            andExpression.setLeftExpression(whereExpression);
            return andExpression;
        }
    }

    /**
     * 多表关联查询时，给关联的所有表加入环境隔离条件
     *
     * @param selectBody select语法树
     * @return 新的SQL Where语法树
     */
    private Expression multipleTableJoinWhereExpression(PlainSelect selectBody) {
        Table mainTable = selectBody.getFromItem(Table.class);
        String mainTableAlias = Objects.isNull(mainTable.getAlias()) ? null : mainTable.getAlias().getName();
        Expression newWhereExpression = selectBody.getWhere();
        if(startsWithAny(mainTable.getName())){
            // 将 t1.env = ENV 的条件添加到where中
            newWhereExpression = setEnvToWhereExpression(selectBody.getWhere(), mainTableAlias);
        }
        List<Join> joins = selectBody.getJoins();
        for (Join join : joins) {
            FromItem joinRightItem = join.getRightItem();
            if (joinRightItem instanceof Table) {
                Table joinTable = (Table) joinRightItem;
                String joinTableAlias = Objects.isNull(joinTable.getAlias()) ? null : joinTable.getAlias().getName();
                if(startsWithAny(joinTable.getName())){
                    // 将每一个join的 tx.env = ENV 的条件添加到where中
                    newWhereExpression = setEnvToWhereExpression(newWhereExpression, joinTableAlias);
                }
            } else if (joinRightItem instanceof SubSelect) {
                // 如果是子查询，在子查询中新增环境变量条件
                PlainSelect subSelectBody = ((SubSelect) joinRightItem).getSelectBody(PlainSelect.class);
                divideSelectWhereExpression(subSelectBody);
            }
        }
        return newWhereExpression;
    }

    /**
     * 新增数据时，插入create_by,create_time字段
     *
     * @param insert Insert 语法树
     */
    private void setEnvToInsert(Insert insert) {
        LoginUser loginUser1 = Global.getLoginUser();
        // 用户登录登出时，记录as_login_record表，无法获取到登录用户
        if(Objects.isNull(loginUser1)){
            return;
        }
        AsUser loginUser = loginUser1.getAsUser();
        // 添加env列
        List<Column> columns = insert.getColumns();
        boolean exists1 = columns.stream()
                .noneMatch(c -> c.getColumnName().equalsIgnoreCase(SqlConstants.create_by));
        boolean exists2 = columns.stream()
                .noneMatch(c -> c.getColumnName().equalsIgnoreCase(SqlConstants.create_time));
        if(exists1){
            columns.add(new Column(SqlConstants.create_by));
        }
        if(exists2){
            columns.add(new Column(SqlConstants.create_time));
        }
        if(Objects.equals(OkyaConfig.getDeletionType(), "L")){
            boolean exists3 = columns.stream()
                    .noneMatch(c -> c.getColumnName().equalsIgnoreCase(SqlConstants.create_by));
        }
        // values中添加环境变量值
        List<SelectBody> selects = insert.getSelect().getSelectBody(SetOperationList.class).getSelects();
        for (SelectBody select : selects) {
            if (select instanceof ValuesStatement) {
                ValuesStatement valuesStatement = (ValuesStatement) select;
                ExpressionList expressions = (ExpressionList) valuesStatement.getExpressions();
                List<Expression> values = expressions.getExpressions();
                for (Expression expression : values) {
                    if (expression instanceof RowConstructor) {
                        RowConstructor rowConstructor = (RowConstructor) expression;
                        ExpressionList exprList = rowConstructor.getExprList();
                        if(exists1){
                            exprList.addExpressions(new StringValue(loginUser.getUserId()));
                        }
                        if(exists2){
                            exprList.addExpressions(new StringValue(DateFormatUtil.formatNow()));
                        }
                    }
                }
            }
        }
    }

    /**
     * 修改数据时，插入update_by,update_time字段
     *
     * @param update Update 语法树
     */
    private void setEnvToUpdate(Update update) {
        AsUser loginUser = Global.getLoginUser().getAsUser();
        // 添加env列
        update.addUpdateSet(new Column(SqlConstants.update_by), new StringValue(loginUser.getUserId()));
        update.addUpdateSet(new Column(SqlConstants.update_time), new StringValue(DateFormatUtil.formatNow()));
    }

    private boolean startsWithAny(String target) {
        if (target == null) {
            return false;
        }
        for (String prefix : SqlConstants.TABLE_PREFIX) {
            if (target.toLowerCase().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}