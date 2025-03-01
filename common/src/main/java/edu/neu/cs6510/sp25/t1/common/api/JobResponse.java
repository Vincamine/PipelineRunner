package edu.neu.cs6510.sp25.t1.common.api;

import java.util.List;

/**
 * Represents the response of a job execution.
 * Contains information about the job ID, exit code, output, success status,
 * collected artifacts, and error message if any.
 * Used for sending the response back to the client.
 * sent from the worker back to the backend.
 */
public class JobResponse {
  private final String jobId; // Unique identifier for the job execution.
  private final int exitCode; // Process exit code (0 = success, non-zero = failure).
  private final String output; // Console/log output from execution.
  private final List<String> collectedArtifacts; // Paths to collected artifacts.
  private final String errorMessage; // Error message if execution failed.

  /**
   * Constructor to initialize the JobResponse object.
   *
   * @param jobId              Unique job execution ID.
   * @param exitCode           Exit code of the job execution (0 = success).
   * @param output             Console output from the job execution.
   * @param collectedArtifacts List of artifacts collected during execution.
   * @param errorMessage       Error message if the job failed.
   */
  public JobResponse(String jobId, int exitCode, String output,
                     List<String> collectedArtifacts, String errorMessage) {
    this.jobId = jobId;
    this.exitCode = exitCode;
    this.output = output != null ? output : ""; // Avoid null values
    this.collectedArtifacts = collectedArtifacts != null ? collectedArtifacts : List.of();
    this.errorMessage = errorMessage != null ? errorMessage : "";
  }

  /**
   * Get the job ID.
   *
   * @return Job ID.
   */
  public String getJobId() {
    return jobId;
  }

  /**
   * Get the exit code of the job execution.
   *
   * @return Exit code.
   */
  public int getExitCode() {
    return exitCode;
  }

  /**
   * Get the output from the job execution.
   *
   * @return Output.
   */
  public String getOutput() {
    return output;
  }

  /**
   * Get the list of collected artifacts.
   *
   * @return List of artifacts.
   */
  public List<String> getCollectedArtifacts() {
    return collectedArtifacts;
  }

  /**
   * Get the error message if the job execution failed.
   *
   * @return Error message.
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Determines if the job execution was successful based on exit code.
   *
   * @return `true` if `exitCode == 0`, otherwise `false`.
   */
  public boolean isSuccess() {
    return exitCode == 0;
  }

  /**
   * String representation of the JobResponse object.
   *
   * @return String representation.
   */
  @Override
  public String toString() {
    return "JobResponse{" +
            "jobId='" + jobId + '\'' +
            ", exitCode=" + exitCode +
            ", success=" + isSuccess() + // Uses the method dynamically
            ", collectedArtifacts=" + collectedArtifacts +
            ", errorMessage='" + errorMessage + '\'' +
            '}';
  }
}
