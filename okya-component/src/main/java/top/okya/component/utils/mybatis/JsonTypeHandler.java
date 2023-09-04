package top.okya.component.utils.mybatis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import top.okya.component.constants.CharacterConstants;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author: maojiaqi
 * @Date: 2023/7/13 14:40
 * @describe： mybatis JSON数据工具类
 */

@Slf4j
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {

    private static ObjectMapper objectMapper;
    private Class<T> type;

    static {
        objectMapper = new ObjectMapper();
    }

    public JsonTypeHandler(Class<T> type) {
        if (log.isInfoEnabled()) {
            log.info("JsonTypeHandler(" + type + ")");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    private T parse(String json) {
        try {
            if (json == null || json.length() == 0) {
                return null;
            }
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            return (T) json;
        }
    }

    private String toJsonString(Object obj) {
        try {
            String value = objectMapper.writeValueAsString(obj);
            if (value.startsWith(CharacterConstants.DOUBLE_QUOTATION_MARK) && value.endsWith(CharacterConstants.DOUBLE_QUOTATION_MARK) && value.length() > 2) {
                return value.substring(1, value.length() - 1);
            } else if(Objects.equals(value, CharacterConstants.BRACES_LEFT + CharacterConstants.BRACES_RIGHT)) {
                return "";
            } else {
                return value;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return (T) parse(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return (T) parse(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return (T) parse(cs.getString(columnIndex));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int columnIndex, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(columnIndex, toJsonString(parameter));

    }

}
