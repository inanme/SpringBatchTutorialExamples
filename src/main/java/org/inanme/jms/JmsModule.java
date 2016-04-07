package org.inanme.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
public class JmsModule {

    @Component
    public static class ExampleListener {

        final static Logger LOGGER = LoggerFactory.getLogger(JmsModule.class);

        @ServiceActivator(inputChannel = "inboundMessageChannel", outputChannel = "nullChannel")
        public void onMessage(Integer message) throws InterruptedException {
            LOGGER.debug(message.toString());
            TimeUnit.SECONDS.sleep(5l);
        }
    }

    @Component("serverId")
    public static class ServerId implements FactoryBean<String> {

        private String serverId = UUID.randomUUID().toString();

        @Override
        public String getObject() throws Exception {
            return serverId;
        }

        @Override
        public Class<?> getObjectType() {
            return String.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    @Bean
    public TaskExecutor jmsTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(2);
        taskExecutor.setThreadGroupName("jmsTaskExecutor");
        return taskExecutor;
    }
}
