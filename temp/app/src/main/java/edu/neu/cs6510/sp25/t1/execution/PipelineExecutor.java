package edu.neu.cs6510.sp25.t1.execution;

import java.util.List;

/**
 * Executes a full CI/CD pipeline by running all stages sequentially inside a dedicated Docker container.
 */
public class PipelineExecutor {
  private final String pipelineName;
  private final List<StageExecutor> stages;
  private ExecutionStatus status;
  private final DockerRunner dockerRunner;
  private String containerId;

  public PipelineExecutor(String pipelineName, List<StageExecutor> stages, DockerRunner dockerRunner) {
    this.pipelineName = pipelineName;
    this.stages = stages;
    this.dockerRunner = dockerRunner;
    this.status = ExecutionStatus.PENDING;
  }

  /**
   * Executes all stages in the pipeline sequentially inside a Docker container.
   * If any stage fails, the pipeline stops and is marked as FAILED.
   */
  public void execute() {
    System.out.println("Starting pipeline: " + pipelineName);
    status = ExecutionStatus.RUNNING;

    // Start pipeline execution inside a dedicated container
    containerId = dockerRunner.startContainer("/bin/sh", "-c", "echo Running pipeline inside container");

    for (StageExecutor stage : stages) {
      stage.execute();
      if (stage.getStatus() == ExecutionStatus.FAILED) {
        status = ExecutionStatus.FAILED;
        System.out.println("Pipeline " + pipelineName + " failed due to stage failure.");
        return;
      }
    }

    status = ExecutionStatus.SUCCESSFUL;
    System.out.println("Pipeline " + pipelineName + " completed successfully inside container: " + containerId);
  }

  public ExecutionStatus getStatus() {
    return status;
  }
}
