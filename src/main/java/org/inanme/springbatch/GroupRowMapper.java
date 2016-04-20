package org.inanme.springbatch;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupRowMapper implements RowMapper<Group1> {

    @Override
    public Group1 mapRow(ResultSet rs, int rowNum) throws SQLException {
        Group1 mappedGroup = new Group1();
        String id = rs.getString("id");
        String name = rs.getString("name");
        mappedGroup.setId(id);
        mappedGroup.setName(name);

        return mappedGroup;
    }
}
