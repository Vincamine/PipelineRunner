package edu.neu.cs6510.sp25.t1.common.api;

/**
 * Represents a job status update request body.
 * This class is used for sending job status updates to the backend.
 */
public class JobStatusUpdate {
    private final String jobName;
    private final String status;

    /**
     * Constructor for JobStatusUpdate.
     * 
     * @param jobName
     * @param status
     */
    public JobStatusUpdate(String jobName, String status) {
        this.jobName = jobName; // job identifier.
        this.status = status; // current status of the job: "running", "completed", "failed".
    }

    /**
     * Get the job name.
     * 
     * @return the job name
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Get the job status.
     * 
     * @return the job status
     */
    public String getStatus() {
        return status;
    }
}
