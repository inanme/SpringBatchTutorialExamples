package org.inanme.springbatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = App.class, loader = AnnotationConfigContextLoader.class)
public class SpringBatchUnitTest {

    @Autowired
    @Qualifier("job1")
    Job job1;

    @Autowired
    @Qualifier("job2")
    Job job2;


    @Autowired
    @Qualifier("job3")
    Job job3;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    ProcessingResources processingResources;

    @Test
    public void testJob1() throws Exception {
        final JobExecution jobExecution = jobLauncher.run(job1, new JobParameters());

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @Test
    public void testJob2() throws Exception {

        final JobExecution jobExecution = jobLauncher
                .run(job2, new JobParametersBuilder()
                        .addLong("from", 10l)
                        .addLong("to", 100l)
                        .toJobParameters());

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @Test
    public void testJob3() throws Exception {

        final JobExecution jobExecution = jobLauncher
                .run(job3, new JobParametersBuilder()
                        .addLong("from", 10l)
                        .addLong("to", 100l)
                        .toJobParameters());

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
}
