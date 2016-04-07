package org.inanme.springbatch;

import org.inanme.ProcessingResources;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@Import(ProcessingResources.class)
@ImportResource("/horizontal-scaling-job1.xml")
public class HorizontalScalingModule {
}
