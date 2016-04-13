package org.inanme;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.SharedEntityManagerBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManager;
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
        taskExecutor.setThreadGroupName("Single Threads");
        return taskExecutor;
    }


    public static final String H2_BATCH_SCHEMA_CREATE_SQL = "classpath:org/springframework/batch/core/schema-h2.sql";

    public static final String H2_BATCH_SCHEMA_DROP_SQL = "classpath:org/springframework/batch/core/schema-drop-h2.sql";

    @Value(H2_BATCH_SCHEMA_CREATE_SQL)
    public Resource H2_BATCH_SCHEMA_CREATE_RESOURCE;

    @Value(H2_BATCH_SCHEMA_DROP_SQL)
    public Resource H2_BATCH_SCHEMA_DROP_RESOURCE;

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource, DatabasePopulator databasePopulator) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator);
        //initializer.setDatabaseCleaner(databaseCleaner());
        return initializer;
    }

    @Bean
    public DatabasePopulator databasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(H2_BATCH_SCHEMA_DROP_RESOURCE);
        populator.addScript(H2_BATCH_SCHEMA_CREATE_RESOURCE);
        //populator.addScript(H2_DATA_SCRIPT);
        return populator;
    }

    private DatabasePopulator databaseCleaner() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(H2_BATCH_SCHEMA_DROP_RESOURCE);
        return populator;
    }

    @Bean
    public SharedEntityManagerBean entityManager(EntityManagerFactory managerFactory) {
        SharedEntityManagerBean sharedEntityManagerBean = new SharedEntityManagerBean();
        sharedEntityManagerBean.setEntityManagerFactory(managerFactory);
        return sharedEntityManagerBean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
                                                                       Properties jpaProperties) {

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        //factory.setDataSource(dataSource);
        factory.setJtaDataSource(dataSource);
        factory.setPersistenceUnitName("sample");
        factory.setPackagesToScan("org.inanme.springdata.domain");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);
        factory.setJpaVendorAdapter(vendorAdapter);

        factory.setJpaProperties(jpaProperties);
        //factory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());

        return factory;
    }

    @Bean
    Properties jpaProperties() {
        Properties props = new Properties();
//        props.put("hibernate.query.substitutions", "true 'Y', false 'N'");
//        props.put("hibernate.hbm2ddl.auto", "create-drop");
//        props.put("hibernate.show_sql", "false");
//        props.put("hibernate.format_sql", "true");
//        props.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        props.put("hibernate.autoReconnect", true);
        props.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.generate_statistics", false);
        props.put("hibernate.hbm2ddl.auto", "create");
        props.put("hibernate.jdbc.use_scrollable_resultset", true);
        props.put("hibernate.query.substitutions", true);
        props.put("hibernate.show_sql", true);
        props.put("hibernate.use_sql_comments", true);
        props.put("hibernate.default_schema", "PUBLIC");

        props.put("hibernate.connection.characterEncoding", "UTF-8");
        props.put("hibernate.connection.charSet", "UTF-8");
        props.put("hibernate.connection.useUnicode", true);
        props.put("hibernate.connection.defaultNChar", true);

        props.put("hibernate.implicit_naming_strategy",
                  "org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl");
        props.put("javax.persistence.validation.mode", "none");
        props.put("org.hibernate.envers.audit_table_suffix", "_rev");
        props.put("hibernate.transaction.jta.platform",
                  "org.hibernate.service.jta.platform.internal.BitronixJtaPlatform");

        return props;
    }
}
