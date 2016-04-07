package org.inanme.springbatch;


import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.*;

@ContextConfiguration(
    classes = {Job5.class},
    loader = AnnotationConfigContextLoader.class)
public class Job5Test {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    Job job5, job6, job7;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    JobOperator jobOperator;

    @Autowired
    JobExplorer jobExplorer;

    @Test
    public void job5Test() throws Exception {
        JobExecution jobExecution = jobLauncher
            .run(job5, new JobParametersBuilder().addLong("from", 10l).addLong("to", 100l).toJobParameters());

        //assertEquals(BatchStatus.FAILED, jobExecution.getStatus());

        //  JobExecution execution = jobExplorer.getJobExecution(jobExecution.getJobId());

        //TimeUnit.MINUTES.sleep(1l);
    }

    @Test
    public void job6TestRestart() throws Exception {
        JobParameters jobParameters1 = new JobParametersBuilder().addLong("id", 1l).toJobParameters();
        JobParameters jobParameters2 = new JobParametersBuilder().addLong("id", 2l).toJobParameters();

        JobExecution jobExecution1 = jobLauncher.run(job6, jobParameters1);
        assertEquals(BatchStatus.FAILED, jobExecution1.getStatus());

        JobExecution jobExecution2 = jobLauncher.run(job6, jobParameters1);
        assertEquals(BatchStatus.COMPLETED, jobExecution2.getStatus());

        JobExecution jobExecution3 = jobLauncher.run(job6, jobParameters2);
        assertEquals(BatchStatus.FAILED, jobExecution3.getStatus());
    }

    @Test(expected = JobInstanceAlreadyCompleteException.class)
    public void job6JobInstanceAlreadyCompleteException() throws Exception {
        JobParameters jobParameters1 = new JobParametersBuilder().addLong("id", 1l).toJobParameters();

        JobExecution jobExecution1 = jobLauncher.run(job6, jobParameters1);
        assertEquals(BatchStatus.FAILED, jobExecution1.getStatus());

        JobExecution jobExecution2 = jobLauncher.run(job6, jobParameters1);
        assertEquals(BatchStatus.COMPLETED, jobExecution2.getStatus());

        jobLauncher.run(job6, jobParameters1);
    }

    @Test
    public void job7Test() throws Exception {
        JobParameters jobParameters1 = new JobParametersBuilder().addLong("id", 1l).toJobParameters();

        JobExecution jobExecution1 = jobLauncher.run(job7, jobParameters1);
        assertEquals(BatchStatus.FAILED, jobExecution1.getStatus());

        JobExecution jobExecution2 = jobLauncher.run(job7, jobParameters1);
        assertEquals(BatchStatus.FAILED, jobExecution2.getStatus());

        JobExecution jobExecution3 = jobLauncher.run(job7, jobParameters1);
        assertEquals(BatchStatus.FAILED, jobExecution3.getStatus());

        JobExecution jobExecution4 = jobLauncher.run(job7, jobParameters1);
        assertEquals(BatchStatus.FAILED, jobExecution4.getStatus());
    }
}
