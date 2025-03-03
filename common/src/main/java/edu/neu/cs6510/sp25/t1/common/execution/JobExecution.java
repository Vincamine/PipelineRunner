package edu.neu.cs6510.sp25.t1.common.execution;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Represents a job execution instance within a pipeline stage.
 * This class tracks the execution status of a job, including its start time,
 * completion time, dependencies, and whether it should be executed in Docker
 * or locally in a non-containerized environment.
 */
public class JobExecution {
  private String name;
  private ExecutionStatus status;
  private final Instant startTime;
  private Instant lastUpdated;
  private final boolean allowFailure;
  private final String image;
  private final List<String> script;
  private final List<String> dependencies;

  /**
   * Constructs a new JobExecution instance.
   *
   * @param name         The name of the job.
   * @param image        The Docker image used for execution.
   * @param script       The script to be executed.
   * @param dependencies The dependencies required before execution.
   * @param allowFailure Whether the job is allowed to fail.
   */
  public JobExecution(String name, String image, List<String> script,
                      List<String> dependencies, boolean allowFailure) {
    this.name = name;
    this.status = ExecutionStatus.PENDING;
    this.startTime = Instant.now();
    this.lastUpdated = Instant.now();
    this.allowFailure = allowFailure;
    this.image = image;
    this.script = script;
    this.dependencies = dependencies;
  }

  /**
   * Updates the execution status of this entity.
   *
   * @param newState the new execution status to be set
   */
  public void updateState(ExecutionStatus newState) {
    this.status = newState;
    this.lastUpdated = Instant.now();
  }

  /**
   * Gets the name of the job.
   *
   * @return The job name.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the status of the job.
   *
   * @return The job status.
   */
  public ExecutionStatus getStatus() {
    return status;
  }

  public String getImage() {
    return image;
  }

  public boolean isAllowFailure() {
    return allowFailure;
  }
}
