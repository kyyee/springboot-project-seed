package com.kyyee.sps.mapper.primary.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.kyyee.framework.common.utils.SpringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class ListLongHandler extends BaseTypeHandler<List<Long>> {
    @SneakyThrows
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Long> parameter, JdbcType jdbcType) throws SQLException {
        PGobject object = new PGobject();
        object.setType("jsonb");
        ObjectMapper objectMapper = SpringUtils.getBean("objectMapper", ObjectMapper.class);
        if (!ObjectUtils.isEmpty(objectMapper)) {
            object.setValue(objectMapper.writeValueAsString(parameter));
        }
        ps.setObject(i, object);
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return getList(json);
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return getList(json);
    }

    @Override
    public List<Long> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return getList(json);
    }

    @SneakyThrows
    private List<Long> getList(String json) {
        ObjectMapper objectMapper = SpringUtils.getBean("objectMapper", ObjectMapper.class);
        if (StringUtils.hasLength(json) && !ObjectUtils.isEmpty(objectMapper)) {
            CollectionType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, String.class);
            return objectMapper.readValue(json, javaType);
        }
        return null;
    }
}
