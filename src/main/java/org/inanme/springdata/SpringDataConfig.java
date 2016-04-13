package org.inanme.springdata;

import org.h2.jdbcx.JdbcDataSource;
import org.inanme.ProcessingResources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static org.inanme.ProcessingResources.*;

@Configuration
@EnableJpaRepositories(transactionManagerRef = "jpaTransactionManager")
@EnableTransactionManagement
@Import(ProcessingResources.class)
public class SpringDataConfig {

    private static final String H2_JDBC_FILE_URL = "jdbc:h2:~/h2/spring-batch";

    @Bean
    public DataSource dataSource() {
        return memoryDatabase();
    }

    private DataSource memoryDatabase() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript(H2_BATCH_SCHEMA_CREATE_SQL)
                                            .build();
    }

    private DataSource fileBasedDatabase() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(H2_JDBC_FILE_URL);
        ds.setUser("sa");
        ds.setPassword("");
        return ds;
    }

    @Bean
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public PlatformTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
