package edu.neu.cs6510.sp25.t1.worker.api.response;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import java.util.List;

/**
 * Represents the response of a job execution from the worker.
 */
public class JobResponse {
  private final String jobId;          // Unique execution ID
  private final int exitCode;          // Process exit code (0 = success, non-zero = failure)
  private final String output;         // Console/log output from execution
  private final List<String> artifacts; // Paths to collected artifacts
  private final String errorMessage;   // Error message if execution failed
  private final ExecutionStatus status;

  public JobResponse(String jobId, int exitCode, String output, List<String> artifacts,
                     String errorMessage, ExecutionStatus status) {
    this.jobId = jobId;
    this.exitCode = exitCode;
    this.output = output != null ? output : ""; // Avoid null values
    this.artifacts = artifacts != null ? artifacts : List.of();
    this.errorMessage = errorMessage != null ? errorMessage : "";
    this.status = status;
  }

  public String getJobId() { return jobId; }
  public int getExitCode() { return exitCode; }
  public String getOutput() { return output; }
  public List<String> getArtifacts() { return artifacts; }
  public String getErrorMessage() { return errorMessage; }
  public ExecutionStatus getStatus() { return status; }

  @Override
  public String toString() {
    return "JobResponse{" +
            "jobId='" + jobId + '\'' +
            ", exitCode=" + exitCode +
            ", output='" + output + '\'' +
            ", artifacts=" + artifacts +
            ", errorMessage='" + errorMessage + '\'' +
            ", status=" + status +
            '}';
  }
}
