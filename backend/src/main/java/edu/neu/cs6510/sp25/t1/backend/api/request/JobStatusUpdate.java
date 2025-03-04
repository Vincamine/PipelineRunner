package edu.neu.cs6510.sp25.t1.backend.api.request;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Represents a job status update request body.
 * This class is used for sending job status updates to the backend.
 */
public class JobStatusUpdate {
  private String jobName;
  private ExecutionStatus status;

  public JobStatusUpdate() {
  }

  public JobStatusUpdate(String jobName, String status) {
    if (jobName == null || jobName.isBlank()) {
      throw new IllegalArgumentException("Job name cannot be null or empty.");
    }
    this.jobName = jobName;
    this.status = ExecutionStatus.fromString(status);
  }

  public String getJobName() {
    return jobName;
  }

  public ExecutionStatus getStatus() {
    return status;
  }


  @Override
  public String toString() {
    return "JobStatusUpdate{" +
            "jobName='" + jobName + '\'' +
            ", status=" + status +
            '}';
  }
}