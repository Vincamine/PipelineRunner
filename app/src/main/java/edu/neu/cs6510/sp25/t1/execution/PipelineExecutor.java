package edu.neu.cs6510.sp25.t1.execution;

import java.util.List;

/**
 * Executes a full CI/CD pipeline by running all stages sequentially.
 */
public class PipelineExecutor {
  private final String pipelineName;
  private final List<StageExecutor> stages;
  private ExecutionStatus status;

  public PipelineExecutor(String pipelineName, List<StageExecutor> stages) {
    this.pipelineName = pipelineName;
    this.stages = stages;
    this.status = ExecutionStatus.PENDING;
  }

  /**
   * Executes all stages in the pipeline sequentially.
   * If any stage fails, the pipeline stops and is marked as FAILED.
   */
  public void execute() {
    System.out.println("Starting pipeline: " + pipelineName);
    status = ExecutionStatus.RUNNING;

    for (StageExecutor stage : stages) {
      stage.execute();
      if (stage.getStatus() == ExecutionStatus.FAILED) {
        status = ExecutionStatus.FAILED;
        System.out.println("Pipeline " + pipelineName + " failed due to stage failure.");
        return;
      }
    }

    status = ExecutionStatus.SUCCESSFUL;
    System.out.println("Pipeline " + pipelineName + " completed successfully.");
  }

  public ExecutionStatus getStatus() {
    return status;
  }
}
