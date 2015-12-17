package org.inanme.springbatch;

import com.google.common.primitives.Longs;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Configuration
public class Job3 extends JobBase {

    @Bean
    @DependsOn
    public Job job3() {
        return jobBuilderFactory
                .get("job3")
                .start(prepareList())
                .next(partition())
                .build();
    }

    Step partition() {
        return stepBuilderFactory
                .get("job3.master")
                .partitioner(slaveStep().getName(), partitioner())
                .gridSize(2)
                .build();
    }

    @Bean
    Step slaveStep() {
        return stepBuilderFactory
                .get("job3.slave")
                .<Long, Long>chunk(3)
                .reader(listReader())
                .processor(listProcessor())
                .writer(listWriter())
                .build();
    }

    Partitioner partitioner() {
        return (gridSize) ->
                IntStream.range(0, gridSize).mapToObj(i -> {
                            ExecutionContext context = new ExecutionContext();
                            context.putString("name", "Thread" + i);
                            return context;
                        }
                ).collect(Collectors.toMap(it -> it.getString("name"), Function.identity()));
    }

    Step prepareList() {
        return stepBuilderFactory.get("job3.step1").tasklet((stepContribution, chunkContext) -> {
            JobParameters parameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
            long from = parameters.getLong("from");
            long to = parameters.getLong("to");
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
                    .put("data", LongStream.range(from, to).toArray());
            return RepeatStatus.FINISHED;
        }).build();
    }

    ItemReader<Long> listReader() {
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

    ItemProcessor<Long, Long> listProcessor() {
        return pojo -> pojo * 10;
    }

    ItemWriter<Long> listWriter() {
        return pojoList -> System.out.println(Thread.currentThread().getName() + " " + JOINER.join
                (pojoList));
    }
}
