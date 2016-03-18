package org.inanme.springbatch;

import com.google.common.base.Joiner;
import org.inanme.ProcessingResources;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
abstract class JobBase {

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    TaskExecutor mte;

    @Autowired
    ApplicationContext applicationContext;

    final Joiner JOINER = Joiner.on(", ").skipNulls();

    public abstract String getJobName();
}
