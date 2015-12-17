package org.inanme.springbatch;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
public class Job1 extends JobBase {

    final int[] numberList = IntStream.range(0, 9).toArray();

    final List<CustomPojo> customPojoList =
            Arrays.stream(numberList)
                    .mapToObj(CustomPojo::new)
                    .collect(Collectors.toList());

    final Iterator<CustomPojo> iter1 = customPojoList.iterator();

    final ItemProcessListener<CustomPojo, JobResult> processListener =
            new ItemProcessListener<CustomPojo, JobResult>() {
                @Override
                public void beforeProcess(CustomPojo item) {
                    System.out.println("Before :" + item);
                }

                @Override
                public void afterProcess(CustomPojo item, JobResult result) {
                    System.out.println("after :" + result);
                }

                @Override
                public void onProcessError(CustomPojo item, Exception e) {
                    e.printStackTrace();
                }
            };

    @Bean
    public Job job1() {
        return jobBuilderFactory
                .get("job1")
                .start(validation())
                .next(createGroups())
                .next(report())
                .build();
    }

    public Step validation() {
        return stepBuilderFactory.get("job1.step2").tasklet((cont, context) -> {
            System.out.println("this is validation");
            return RepeatStatus.FINISHED;
        }).build();
    }

    public Step createGroups() {
        return stepBuilderFactory.get("job1.step1").<CustomPojo, JobResult>chunk(3)
                .reader(reader())
                .processor(processor())
                .listener(processListener)
                .writer(writer())
                .faultTolerant()
                .skipLimit(1)
                .skip(IllegalArgumentException.class)
                .build();
    }

    public ItemReader<CustomPojo> reader() {
        return () -> iter1.hasNext() ? iter1.next() : null;
    }

    public ItemProcessor<CustomPojo, JobResult> processor() {
        return pojo -> {
            if (pojo.id == 7) {
                throw new IllegalArgumentException(pojo.toString());
            } else {
                return new JobResult(pojo, JobStatus.SUCCESS);
            }
        };
    }

    public ItemWriter<JobResult> writer() {
        return pojoList -> System.out.println("writing Pojo " + JOINER.join(pojoList));
    }

    public Step report() {
        return stepBuilderFactory.get("job1.step2").tasklet((cont, context) -> {
            System.out.println("This is a reporting step");
            return RepeatStatus.FINISHED;
        }).build();
    }

}
