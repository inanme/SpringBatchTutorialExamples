package org.inanme.springbatch;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {SpringBatchConfig.class, SpringBatchUnitTest.TestConfig.class},
    loader = AnnotationConfigContextLoader.class)
public class SpringBatchUnitTest {

    @Autowired
    Job job1, job2, job3;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    JobExplorer jobExplorer;

    @Autowired
    JobRegistry jobRegistry;

    @Autowired
    JobOperator jobOperator;

    @Autowired
    TestConfig testConfig;

    JobLauncherTestUtils jobLauncher1 = new JobLauncherTestUtils();

    @Before
    public void init() {
        jobLauncher1.setJob(job1);
        jobLauncher1.setJobLauncher(jobLauncher);
        jobLauncher1.setJobRepository(jobRepository);
    }

    @Test
    public void testJob1() throws Exception {
        JobExecution jobExecution = jobLauncher1.launchJob();
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        boolean jobInstanceExists = jobRepository.isJobInstanceExists(job1.getName(), jobExecution.getJobParameters());
        assertThat(jobInstanceExists, is(true));

        JobExecution jobExecution1 = jobExplorer.getJobExecution(jobExecution.getJobId());
        assertThat(jobExecution1, is(notNullValue()));
    }

    @Test
    public void testJob2() throws Exception {
        JobExecution jobExecution = jobLauncher
            .run(job2, new JobParametersBuilder().addLong("from", 10l).addLong("to", 100l).toJobParameters());

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @Test
    public void testJob3() throws Exception {
        JobExecution jobExecution = jobLauncher
            .run(job3, new JobParametersBuilder().addLong("from", 10l).addLong("to", 100l).toJobParameters());

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @Configuration
    static class TestConfig {

    }
}
