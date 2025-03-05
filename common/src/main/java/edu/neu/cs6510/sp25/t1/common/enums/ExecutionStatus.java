package edu.neu.cs6510.sp25.t1.common.enums;

/**
 * Enum representing the possible execution states of a pipeline, stage, or job.
 */
public enum ExecutionStatus {
  PENDING("Execution is scheduled but has not started."),
  RUNNING("Execution is in progress."),
  SUCCESS("Execution completed successfully."),
  FAILED("Execution encountered a failure."),
  CANCELED("The job/stage was dropped due to an earlier failure.");

  private final String description;

  ExecutionStatus(String description) {
    this.description = description;
  }

  /**
   * Retrieves the description of the execution status.
   *
   * @return The description of the status.
   */
  public String getDescription() {
    return description;
  }
}
