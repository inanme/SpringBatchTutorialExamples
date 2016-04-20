package org.inanme.springbatch;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Job8Support {

    @Entity(name = "group1")
    public static class Group1 {

        @Id
        private String id;

        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class GroupRowMapper implements RowMapper<Group1> {

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

    @Repository("groupRepository")
    public interface GroupRepository extends JpaRepository<Group1, String> {

    }

    @Entity(name = "user")
    public static class User {

        @Id
        private String id;

        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class UserRowMapper implements RowMapper<User> {

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


    @Repository("userRepository")
    public interface UserRepository extends JpaRepository<User, String> {

    }

    public static class ErrorProneProcessor implements ItemProcessor<Group1, Group1> {

        private static final String KEY = "failedBefore";

        @Value("#{stepExecution}")
        private StepExecution stepExecution;

        @Override
        public Group1 process(Group1 item) throws Exception {
            ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
            Boolean failedBefore = (Boolean) executionContext.get(KEY);
            if (item.getId().equals("4")) {
                if (!Boolean.TRUE.equals(failedBefore)) {
                    executionContext.put(KEY, true);
                    throw new IllegalStateException("Failing on purpose");
                }
            }
            return item;
        }
    }
}
