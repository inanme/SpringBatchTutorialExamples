package org.inanme.springbatch;

import org.inanme.ProcessingResources;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableBatchProcessing
@Import({Job1.class, Job2.class, Job3.class, ProcessingResources.class})
public class SpringBatchConfig {

    @Bean
    public JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

    @Bean(autowire = Autowire.BY_TYPE)
    public JobOperator jobOperator() {
        return new SimpleJobOperator();
    }
}
