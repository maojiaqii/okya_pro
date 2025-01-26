package top.okya.system.dao;

import com.alibaba.fastjson2.JSONObject;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
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

    @InsertProvider(type = SqlProvider.class, method = "insertSql")
    int insert(Map<String, Object> map);

    @DeleteProvider(type = SqlProvider.class, method = "deleteSql")
    void delete(Map<String, Object> map);

    @SelectProvider(type = SqlProvider.class, method = "getFormData")
    Map<String, Object> getFormData(Map<String, Object> map);

    @UpdateProvider(type = SqlProvider.class, method = "updateSql")
    void update(Map<String, Object> map);
}
