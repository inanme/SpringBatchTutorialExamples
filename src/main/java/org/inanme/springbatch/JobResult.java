package org.inanme.springbatch;

import org.inanme.springdata.domain.CustomPojo;

public class JobResult {

    final public CustomPojo pojo;

    final public JobStatus status;

    public JobResult(CustomPojo pojo, JobStatus status) {
        this.pojo = pojo;
        this.status = status;
    }

    @Override
    public String toString() {
        return "JobResult{pojo=" + pojo + ", status=" + status + '}';
    }
}
