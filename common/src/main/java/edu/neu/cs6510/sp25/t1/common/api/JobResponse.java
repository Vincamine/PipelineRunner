package edu.neu.cs6510.sp25.t1.common.api;

import java.util.List;

/**
 * Represents the response of a job execution.
 * Contains information about the job ID, exit code, output, success status,
 * collected artifacts, and error message if any.
 */
public class JobResponse {
    private String jobId;
    private int exitCode;
    private String output;
    private boolean success;
    private List<String> collectedArtifacts;
    private String errorMessage;

    /**
     * Constructor to initialize the JobResponse object.
     * @param jobId
     * @param exitCode
     * @param output
     * @param success
     * @param collectedArtifacts
     * @param errorMessage
     */
    public JobResponse(String jobId, int exitCode, String output, boolean success, 
                       List<String> collectedArtifacts, String errorMessage) {
        this.jobId = jobId;
        this.exitCode = exitCode;
        this.output = output;
        this.success = success;
        this.collectedArtifacts = collectedArtifacts;
        this.errorMessage = errorMessage;
    }

    /**
     * Getters for jobId
     * @return
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Getters for exitCode
     * @return
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * Getters for output
     * @return String output
     */
    public String getOutput() {
        return output;
    }

    /**
     * Getters for success
     * @return boolean success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Getters for collectedArtifacts
     * @return List<String> collectedArtifacts
     */
    public List<String> getCollectedArtifacts() {
        return collectedArtifacts;
    }

    /**
     * Getters for errorMessage
     * @return String errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Override toString method for better readability
     * @return String representation of JobResponse
     */
    @Override
    public String toString() {
        return "JobResponse{" +
                "jobId='" + jobId + '\'' +
                ", exitCode=" + exitCode +
                ", success=" + success +
                ", collectedArtifacts=" + collectedArtifacts +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
