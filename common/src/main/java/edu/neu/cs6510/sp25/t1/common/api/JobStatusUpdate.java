package edu.neu.cs6510.sp25.t1.common.api;

import edu.neu.cs6510.sp25.t1.common.runtime.ExecutionState;

/**
 * Represents a job status update request body.
 * This class is used for sending job status updates to the backend.
 */
public class JobStatusUpdate {
  private final String jobName;
  private final ExecutionState status; // ✅ Use the unified ExecutionState enum

  /**
   * Constructor for JobStatusUpdate.
   *
   * @param jobName The name of the job
   * @param status  The status of the job (must be a valid enum value)
   * @throws IllegalArgumentException if jobName is null or empty
   */
  public JobStatusUpdate(String jobName, String status) {
    if (jobName == null || jobName.isBlank()) {
      throw new IllegalArgumentException("Job name cannot be null or empty.");
    }

    this.jobName = jobName;
    this.status = ExecutionState.fromString(status); // ✅ Convert string to ExecutionState safely
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
   * @return the job status as an ExecutionState enum
   */
  public ExecutionState getStatus() {
    return status;
  }

  /**
   * Get the job status as a string.
   *
   * @return the job status as a string
   */
  @Override
  public String toString() {
    return "JobStatusUpdate{" +
            "jobName='" + jobName + '\'' +
            ", status=" + status +
            '}';
  }
}
