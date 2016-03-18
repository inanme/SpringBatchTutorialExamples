package org.inanme.springbatch;

import org.inanme.ProcessingResources;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@Import(ProcessingResources.class)
@ImportResource("/job5.xml")
public class Job5 {
//
//    public static class Reader implements ItemReader<CustomPojo> {
//
//        final int[] numberList = IntStream.range(0, 5).toArray();
//
//        final List<CustomPojo> customPojoList =
//            Arrays.stream(numberList).mapToObj(CustomPojo::new).collect(Collectors.toList());
//
//        final Iterator<CustomPojo> iter1 = customPojoList.iterator();
//
//        @Override
//        public CustomPojo read() {
//            if (iter1.hasNext()) {
//                CustomPojo next = iter1.next();
//                System.out.println("read :" + next);
//                return next;
//            } else {
//                System.out.println("finished reading");
//                return null;
//            }
//        }
//    }

    public static class ExecutionContextLoader implements Tasklet {

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            System.out.println("ok");
            return RepeatStatus.FINISHED;
        }
    }
}
