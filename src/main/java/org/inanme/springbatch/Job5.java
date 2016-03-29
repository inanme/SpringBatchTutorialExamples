package org.inanme.springbatch;

import org.inanme.ProcessingResources;
import org.inanme.spring.ConstantStringSupplier;
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@Import(ProcessingResources.class)
@ImportResource("/job5.xml")
public class Job5 {

    final static Logger LOGGER = LoggerFactory.getLogger(Job5.class);

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

        private Integer from;

        private Integer to;

        private Iterator<CustomPojo> iter1;

        @Override
        public void afterPropertiesSet() throws Exception {
            final int[] numberList = IntStream.range(from, to).toArray();
            final List<CustomPojo> customPojoList =
                Arrays.stream(numberList).mapToObj(CustomPojo::new).collect(Collectors.toList());
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

        public void setFrom(Integer from) {
            this.from = from;
        }

        public void setTo(Integer to) {
            this.to = to;
        }
    }

    public static class Processor implements ItemProcessor<CustomPojo, CustomPojo> {

        final Random rng = new Random(System.currentTimeMillis());

        private final ErrorCache cache;

        public Processor(ErrorCache cache) {
            this.cache = cache;
        }

        private Boolean fail;

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

        public void setFail(boolean fail) {
            this.fail = fail;
        }
    }

    public static class Writer implements ItemWriter<CustomPojo> {

        private final ErrorCache cache;

        public Writer(ErrorCache cache) {
            this.cache = cache;
        }

        @Override
        public void write(List<? extends CustomPojo> items) throws Exception {
            String itemsStr = items.stream().map(CustomPojo::toString).collect(Collectors.joining(","));
            LOGGER.debug("writer : " + itemsStr);
            LOGGER.debug("======================END OF CHUNK===========================================");
            cache.map.clear();
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
