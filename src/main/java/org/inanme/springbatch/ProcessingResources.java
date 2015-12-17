package org.inanme.springbatch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.TimeUnit;

@Configuration
class ProcessingResources {

    @Bean
    TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setThreadGroupName("Processing Threads");
        return taskExecutor;
    }

    @Bean
    @Scope("prototype")
    Runnable waitingTask() {
        return () -> {
            try {
                TimeUnit.HOURS.sleep(1l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }
}
