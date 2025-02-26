package edu.neu.cs6510.sp25.t1.execution;

import java.util.List;

/**
 * Handles the execution of a stage in a CI/CD pipeline.
 * Executes jobs sequentially and updates the stage status.
 */
public class StageExecutor {
  private final String stageName;
  private final List<JobExecutor> jobs;
  private ExecutionStatus status;

  public StageExecutor(String stageName, List<JobExecutor> jobs) {
    this.stageName = stageName;
    this.jobs = jobs;
    this.status = ExecutionStatus.PENDING;
  }

  /**
   * Executes all jobs in the stage sequentially.
   * If any job fails, the stage is marked as FAILED.
   */
  public void execute() {
    System.out.println("Starting stage: " + stageName);
    status = ExecutionStatus.RUNNING;

    for (JobExecutor job : jobs) {
      job.execute();
      if (job.getStatus() == ExecutionStatus.FAILED) {
        status = ExecutionStatus.FAILED;
        System.out.println("Stage " + stageName + " failed due to job failure.");
        return;
      }
    }

    status = ExecutionStatus.SUCCESSFUL;
    System.out.println("Stage " + stageName + " completed successfully.");
  }

  public ExecutionStatus getStatus() {
    return status;
  }

  public void setStageStatus(ExecutionStatus status) {
    this.status = status;
  }
}
