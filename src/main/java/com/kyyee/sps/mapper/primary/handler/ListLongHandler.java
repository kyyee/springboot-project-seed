package com.kyyee.sps.mapper.primary.handler;

import com.kyyee.sps.common.utils.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;
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
        object.setValue(JSON.toString(parameter));
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
        if (StringUtils.hasLength(json)) {
            return JSON.toList(json, Long.class);
        }
        return null;
    }
}
