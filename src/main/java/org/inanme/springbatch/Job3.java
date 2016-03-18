package org.inanme.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Configuration
public class Job3 extends JobBase {

    @Override
    public String getJobName() {
        return "job3";
    }

    @Bean
    public Job job3() {
        return jobBuilderFactory
                .get(getJobName())
                .start(partition())
                .build();
    }

    Step partition() {
        return stepBuilderFactory
                .get(getJobName() + ".master")
                .partitioner(slaveStep())
                .partitioner(slaveStep().getName(), partitioner())
                .gridSize(2)
                .taskExecutor(processingResources.taskExecutor())
                .build();
    }

    @Bean
    Step slaveStep() {
        return stepBuilderFactory
                .get(getJobName() + ".slave")
                .<Long, Long>chunk(12)
                .reader(listReader())
                .processor(listProcessor())
                .writer(listWriter())
                .build();
    }

    @Bean
    @StepScope
    ListReader listReader(){
        return new ListReader();
    }

    Partitioner partitioner() {
        return (gridSize) ->
                IntStream.range(0, gridSize).mapToObj(i -> {
                            ExecutionContext context = new ExecutionContext();
                            context.putInt("counter", i);
                            context.putInt("siblings", gridSize);
                            context.putString("xman", "<<<<<<<<<<<Thread" + i);
                            return context;
                        }
                ).collect(Collectors.toMap(it -> it.getString("xman"), Function.identity()));
    }

    ItemProcessor<Long, Long> listProcessor() {
        return pojo -> pojo * 10;
    }

    ItemWriter<Long> listWriter() {
        return pojoList -> System.out.println(Thread.currentThread().getName() + " " + JOINER.join
                (pojoList));
    }
}

class ListReader implements ItemReader<Long> {

    private Iterator<Long> iterator;

    {
        System.out.println("init");
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext stepContext = stepExecution.getExecutionContext();
        JobParameters parameters = stepExecution.getJobParameters();
        long counter = stepContext.getInt("counter");
        long siblings = stepContext.getInt("siblings");
        long from = parameters.getLong("from");
        long to = parameters.getLong("to");

        long range = (to - from) / siblings;
        long pageFrom = from + (range * counter);
        long pageTo = pageFrom + range;
        iterator = LongStream.range(pageFrom, pageTo).iterator();
    }

    @Override
    public Long read() {
        return iterator.hasNext() ? iterator.next() : null;
    }
}
