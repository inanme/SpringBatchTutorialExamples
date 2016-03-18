package org.inanme.springbatch;

import org.inanme.ProcessingResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@Import(ProcessingResources.class)
@ImportResource("/job5.xml")
public class Job5 {

    final static Logger LOGGER = LoggerFactory.getLogger(Job5.class);

    public static class TryAgainException extends RuntimeException {
        public TryAgainException(int id) {
            super("Failed for " + id);
        }
    }

    public static class Reader implements ItemReader<CustomPojo> {

        final int[] numberList = IntStream.range(0, 100).toArray();

        final List<CustomPojo> customPojoList =
            Arrays.stream(numberList).mapToObj(CustomPojo::new).collect(Collectors.toList());

        final Iterator<CustomPojo> iter1 = customPojoList.iterator();

        public Reader() {
            LOGGER.debug("New Reader");
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

        public Processor(ErrorCache cache) {
            this.cache = cache;
        }

        @Override
        public CustomPojo process(CustomPojo item) throws Exception {
            if (rng.nextInt(5) == 0) {
                cache.map.compute(item.id, (k, v) -> (v == null) ? 1 : v + 1);
                LOGGER
                    .debug("======================UNLUCKY " + item.id + "===========================================");
                throw new TryAgainException(item.id);
            }
            LOGGER.debug("======================PROCESSED " + item.id + "===========================================");
            return item;
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


    public static class ExecutionContextLoader implements Tasklet {

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
            LOGGER.debug(getClass().getSimpleName());
            return RepeatStatus.FINISHED;
        }
    }

    public static class ErrorCache {
        public Map<Integer, Integer> map = new HashMap<>();
    }

    public static class JEL implements JobExecutionListener {

        private final ErrorCache cache;

        public JEL(ErrorCache cache) {
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