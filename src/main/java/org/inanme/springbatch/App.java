package org.inanme.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Import;

@EnableBatchProcessing
@Import({Job1.class, Job2.class, Job3.class, ProcessingResources.class})
public class App {


}
