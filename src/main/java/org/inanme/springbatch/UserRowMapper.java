package org.inanme.springbatch;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        String id = rs.getString("id");
        String name = rs.getString("name");
        user.setId(id);
        user.setName(name);
        return user;
    }
}
