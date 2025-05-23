package top.okya.system.dao;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import top.okya.component.utils.mybatis.JsonResultHandler;
import top.okya.component.utils.mybatis.SqlProvider;

import java.util.List;
import java.util.Map;

/**
 * 自定义sql
 *
 * @author makejava
 * @since 2024-12-06 22:29:58
 */
@Mapper
public interface SqlProviderMapper {

    @SelectProvider(type = SqlProvider.class, method = "selectSql")
    List<Map<String, Object>> query(Map<String, Object> map);

    @SelectProvider(type = SqlProvider.class, method = "queryWorkFlowTodo")
    List<Map<String, Object>> queryWorkFlowTodo(Map<String, Object> map);

    @SelectProvider(type = SqlProvider.class, method = "queryWorkFlowDone")
    List<Map<String, Object>> queryWorkFlowDone(Map<String, Object> map);

    @SelectProvider(type = SqlProvider.class, method = "queryWorkFlowBelongToMe")
    List<Map<String, Object>> queryWorkFlowBelongToMe(Map<String, Object> map);

    @InsertProvider(type = SqlProvider.class, method = "insertSql")
    int insert(Map<String, Object> map);

    @DeleteProvider(type = SqlProvider.class, method = "deleteSql")
    void delete(Map<String, Object> map);

    @UpdateProvider(type = SqlProvider.class, method = "deleteSqlLogic")
    void deleteLogic(Map<String, Object> map);

    @SelectProvider(type = SqlProvider.class, method = "getFormData")
    @ResultType(Map.class)
    void getFormData(Map<String, Object> map, JsonResultHandler jsonResultHandler);

    @UpdateProvider(type = SqlProvider.class, method = "updateSql")
    void update(Map<String, Object> map);
}
