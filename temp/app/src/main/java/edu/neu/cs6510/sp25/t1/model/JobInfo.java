package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a job within a pipeline stage.
 * Tracks job status and whether failures are allowed.
 */
public class JobInfo {
    private String jobName;
    private String status;
    private boolean allowFailure;

    /**
     * Default constructor required for Jackson deserialization.
     */
    public JobInfo() {
        // Default values can be set if necessary
    }

    /**
     * Constructs a new JobInfo instance.
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
