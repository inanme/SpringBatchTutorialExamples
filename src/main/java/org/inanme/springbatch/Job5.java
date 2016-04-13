package org.inanme.springbatch;

import org.inanme.ProcessingResources;
import org.inanme.spring.ConstantStringSupplier;
import org.inanme.springdata.domain.CustomPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@Import(ProcessingResources.class)
@ImportResource("/job5.xml")
@EnableJpaRepositories(transactionManagerRef = "jtaTransactionManager")
@EnableTransactionManagement
public class Job5 {

    final static Logger LOGGER = LoggerFactory.getLogger(Job5.class);

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

    public static class ExecutionContextLoader implements Tasklet {

        private final String message;

        public ExecutionContextLoader(ConstantStringSupplier stringSupplier) {
            this.message = stringSupplier.get();
        }

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
            String stepName = chunkContext.getStepContext().getStepName();
            LOGGER.debug(stepName + " " + message);
            return RepeatStatus.FINISHED;
        }
    }

    public static class PersistEntity implements Tasklet {

        private final CustomPojoRepository customPojoRepository;

        public PersistEntity(CustomPojoRepository customPojoRepository) {
            this.customPojoRepository = customPojoRepository;
        }

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
            //customPojoRepository.save(new CustomPojo(27));
            return RepeatStatus.FINISHED;
        }
    }

    public static class ErrorProneStep implements Tasklet {

        private static final String KEY = "failedBefore";

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
            String stepName = chunkContext.getStepContext().getStepName();
            ExecutionContext executionContext =
                chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
            Boolean failedBefore = (Boolean) executionContext.get(KEY);
            if (!Boolean.TRUE.equals(failedBefore)) {
                executionContext.put(KEY, true);
                throw new IllegalStateException("Failing on purpose");
            }
            return RepeatStatus.FINISHED;
        }
    }

    public static class ConstantlyFailingStep implements Tasklet {

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
            throw new IllegalStateException("Failing on purpose");
        }
    }

    public static class TryAgainException extends RuntimeException {
        public TryAgainException(int id) {
            super("Failed for " + id);
        }
    }

    public static class Reader implements ItemReader<CustomPojo>, InitializingBean {

        private final Integer from;

        private final Integer to;

        private Iterator<CustomPojo> iter1;

        public Reader(Integer from, Integer to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            final int[] numberList = IntStream.range(from, to).toArray();
            final List<CustomPojo> customPojoList = Arrays.stream(numberList).mapToObj(i -> {
                if (i == 27) {
                    return new CustomPojo(i, "12345");
                } else {
                    return new CustomPojo(i);
                }
            }).collect(Collectors.toList());
            iter1 = customPojoList.iterator();
        }

        @Override
        public CustomPojo read() {
            if (iter1.hasNext()) {
                CustomPojo next = iter1.next();
                LOGGER.debug("read :" + next);
                return next;
            } else {
                LOGGER.debug("finished reading");
                return null;
            }
        }
    }

    public static class Processor implements ItemProcessor<CustomPojo, CustomPojo> {

        final Random rng = new Random(System.currentTimeMillis());

        private final ErrorCache cache;

        private final Boolean fail;

        public Processor(ErrorCache cache, Boolean fail) {
            this.cache = cache;
            this.fail = fail;
        }

        @Override
        public CustomPojo process(CustomPojo item) throws Exception {
            if (Boolean.TRUE.equals(fail) && rng.nextInt(5) == 0) {
                cache.map.compute(item.id, (k, v) -> (v == null) ? 1 : v + 1);
                LOGGER.debug("======================UNLUCKY " + item.id + "========================================");
                throw new TryAgainException(item.id);
            }
            LOGGER.debug("======================PROCESSED " + item.id + "======================================");
            return item;
        }
    }

    public static class Writer implements ItemWriter<CustomPojo> {

        private final ErrorCache cache;

        private final CustomPojoRepository customPojoRepository;

        private final EntityManager entityManager;

        public Writer(ErrorCache cache, CustomPojoRepository customPojoRepository, EntityManager entityManager) {
            Assert.notNull(customPojoRepository);
            this.cache = cache;
            this.customPojoRepository = customPojoRepository;
            this.entityManager = entityManager;
        }

        @Override
        public void write(List<? extends CustomPojo> items) throws Exception {
            String itemsStr = items.stream().map(CustomPojo::toString).collect(Collectors.joining(","));
            LOGGER.debug("writer : " + itemsStr);
            LOGGER.debug("======================END OF CHUNK===========================================");
            cache.map.clear();
            items.stream().forEach(entityManager::persist);
        }
    }

    public static class ErrorCache {
        public Map<Integer, Integer> map = new ConcurrentHashMap<>();
    }

    public static class MyJobExecutionListener implements JobExecutionListener {

        private final ErrorCache cache;

        public MyJobExecutionListener(ErrorCache cache) {
            this.cache = cache;
        }

        @Override
        public void beforeJob(JobExecution jobExecution) {

        }

        @Override
        public void afterJob(JobExecution jobExecution) {
            LOGGER.debug("Map is: '{}'", cache.map.toString());
        }
    }
}
