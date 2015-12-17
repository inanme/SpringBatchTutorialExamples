package org.inanme.springbatch;

import com.google.common.primitives.Longs;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;

import java.util.Iterator;
import java.util.stream.LongStream;

public class Job2 extends JobBase {

    @Bean
    public Job job2() {
        return jobBuilderFactory
                .get("job2")
                .start(prepareList())
                .next(processList())
                .build();
    }

    Step prepareList() {
        return stepBuilderFactory.get("job2.step1").tasklet((stepContribution, chunkContext) -> {
            JobParameters parameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
            long from = parameters.getLong("from");
            long to = parameters.getLong("to");
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
                    .put("data", LongStream.range(from, to).toArray());
            return RepeatStatus.FINISHED;
        }).build();
    }


    Step processList() {
        return stepBuilderFactory
                .get("job2.step2")
                .<Long, Long>chunk(3)
                .reader(listReader())
                .processor(listProcessor())
                .writer(listWriter())
                .taskExecutor(processingResources.taskExecutor())
                .build();
    }

    public ItemReader<Long> listReader() {
        return new ItemReader<Long>() {

            private Iterator<Long> iterator;

            @BeforeStep
            public void beforeStep(StepExecution stepExecution) {
                long[] data = (long[]) stepExecution.getJobExecution().getExecutionContext().get("data");
                iterator = Longs.asList(data).iterator();
            }

            @Override
            public Long read() {
                return iterator.hasNext() ? iterator.next() : null;
            }
        };
    }

    public ItemProcessor<Long, Long> listProcessor() {
        return pojo -> pojo * 10;
    }

    public ItemWriter<Long> listWriter() {
        return pojoList -> System.out.println(Thread.currentThread().getName() + " " + JOINER.join
                (pojoList));
    }
}
