package org.inanme;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class ProcessingResources {

    @Bean
    public TaskExecutor mte() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setThreadGroupName("Multi Threads");
        return taskExecutor;
    }

    @Bean
    public TaskExecutor ste() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setMaxPoolSize(1);
        taskExecutor.setThreadGroupName("Single Threads");
        return taskExecutor;
    }

    private static final String H2_JDBC_FILE_URL = "jdbc:h2:~/h2/spring-batch";

    private static final String H2_JDBC_MEM_URL = "jdbc:h2:mem:spring-batch";

    @Value("classpath:org/springframework/batch/core/schema-h2.sql")
    private Resource H2_BATCH_SCHEMA_CREATE;

    @Value("classpath:test-data.sql")
    private Resource H2_TEST_DATA_SCRIPT;

    @Value("classpath:org/springframework/batch/core/schema-drop-h2.sql")
    private Resource H2_BATCH_SCHEMA_DROP;

    @Bean
    public DataSource dataSource() {
        return simpleDataSource();
    }

    private DataSource simpleDataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                                            .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
                                            .build();
    }

    private DataSource createH2DataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(H2_JDBC_MEM_URL);
        ds.setUser("sa");
        ds.setPassword("");
        return ds;
    }

    public DataSourceInitializer dataSourceInitializer(DataSource dataSource, DatabasePopulator databasePopulator) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator);
        //initializer.setDatabaseCleaner(databaseCleaner());
        return initializer;
    }

    public DatabasePopulator databasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(H2_BATCH_SCHEMA_DROP);
        populator.addScript(H2_BATCH_SCHEMA_CREATE);
        //populator.addScript(H2_DATA_SCRIPT);
        return populator;
    }

    private DatabasePopulator databaseCleaner() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(H2_BATCH_SCHEMA_DROP);
        return populator;
    }

    @Bean
    public PlatformTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    @Bean
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Properties jpaProperties)
        throws Exception {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceUnitName("sample");
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("org.inanme.springdata.domain");
        factory.setDataSource(dataSource);

        factory.setJpaProperties(jpaProperties);
        factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());

        return factory;
    }

    @Bean
    Properties jpaProperties() {
        Properties props = new Properties();
        props.put("hibernate.query.substitutions", "true 'Y', false 'N'");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        return props;
    }
}
