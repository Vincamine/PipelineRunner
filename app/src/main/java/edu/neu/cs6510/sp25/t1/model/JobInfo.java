package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a job within a pipeline stage.
 * Tracks job status and whether failures are allowed.
 */
public class JobInfo {
    private final String jobName;
    private final String status;  // "SUCCESS", "FAILED", or "CANCELED"
    private final boolean allowFailure;

    /**
     * Constructs a new JobInfo instance.
     *
     * @param jobName     The name of the job.
     * @param status      The job status (e.g., SUCCESS, FAILED, CANCELED).
     * @param allowFailure Whether this job allows failure.
     */
    @JsonCreator
    public JobInfo(
            @JsonProperty("jobName") String jobName,
            @JsonProperty("status") String status,
            @JsonProperty("allowFailure") boolean allowFailure) {
        this.jobName = jobName;
        this.status = status;
        this.allowFailure = allowFailure;
    }

    public String getJobName() { return jobName; }
    public String getStatus() { return status; }
    public boolean isAllowFailure() { return allowFailure; }
}
