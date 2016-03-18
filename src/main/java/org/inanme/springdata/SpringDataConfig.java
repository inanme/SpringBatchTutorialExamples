package org.inanme.springdata;

import org.inanme.ProcessingResources;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(transactionManagerRef = "jpaTransactionManager")
@EnableTransactionManagement
@Import({ProcessingResources.class})
public class SpringDataConfig {

}
