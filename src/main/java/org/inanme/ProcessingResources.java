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
import java.util.concurrent.TimeUnit;

@Configuration
public class ProcessingResources {

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setThreadGroupName("Processing Threads");
        return taskExecutor;
    }

    @Bean
    public Runnable waitingTask() {
        return () -> {
            try {
                TimeUnit.HOURS.sleep(1l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    DataSource simpleDataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                                            .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
                                            .build();
    }

    private static final String H2_JDBC_URL_TEMPLATE = "jdbc:h2:~/h2/spring-batch";

    @Value("classpath:org/springframework/batch/core/schema-h2.sql")
    private Resource H2_SCHEMA_SCRIPT;

    @Value("classpath:test-data.sql")
    private Resource H2_DATA_SCRIPT;

    @Value("classpath:org/springframework/batch/core/schema-drop-h2.sql")
    private Resource H2_CLEANER_SCRIPT;

    @Bean
    public DataSource dataSource() {
        return createH2DataSource();
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        //initializer.setDatabaseCleaner(databaseCleaner());
        return initializer;
    }


    private DatabasePopulator databasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(H2_CLEANER_SCRIPT);
        populator.addScript(H2_SCHEMA_SCRIPT);
        //populator.addScript(H2_DATA_SCRIPT);
        return populator;
    }

    private DatabasePopulator databaseCleaner() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(H2_CLEANER_SCRIPT);
        return populator;
    }

    private DataSource createH2DataSource() {
        String jdbcUrl = String.format(H2_JDBC_URL_TEMPLATE, System.getProperty("user.dir"));
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(jdbcUrl);
        ds.setUser("sa");
        ds.setPassword("");
        return ds;
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
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Properties jpaProperties) throws Exception {
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
