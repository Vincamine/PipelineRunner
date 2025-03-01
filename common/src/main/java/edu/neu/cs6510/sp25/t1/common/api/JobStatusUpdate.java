package edu.neu.cs6510.sp25.t1.common.api;

/**
 * Represents a job status update request body.
 * This class is used for sending job status updates to the backend.
 */
public class JobStatusUpdate {
    private final String jobName;
    private final String status;

    public JobStatusUpdate(String jobName, String status) {
        this.jobName = jobName;
        this.status = status;
    }

    public String getJobName() {
        return jobName;
    }

    public String getStatus() {
        return status;
    }
}
