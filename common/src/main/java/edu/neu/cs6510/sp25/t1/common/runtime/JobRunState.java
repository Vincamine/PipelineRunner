package edu.neu.cs6510.sp25.t1.common.runtime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.config.JobConfig;

/**
 * Represents a job execution instance within a pipeline stage.
 * This class tracks the execution status of a job, including its start time,
 * completion time, dependencies, and whether it should be executed in Docker
 * or locally in a non-containerized environment.
 */
public class JobRunState {
  private final JobConfig jobConfig;
  private String status;
  private final boolean allowFailure;
  private Instant startTime;
  private Instant completionTime;
  private final List<String> dependencies;
  private final boolean runInDocker;  // Flag to determine execution environment

  /**
   * Constructs a new JobExecution instance based on its definition.
   *
   * @param jobConfig    The definition of the job.
   * @param status       The status of the job.
   * @param allowFailure Whether the job is allowed to fail.
   * @param dependencies The list of dependencies for the job.
   * @param runInDocker  Whether the job should be executed in a Docker container.
   */
  @JsonCreator
  public JobRunState(
          @JsonProperty("jobDefinition") JobConfig jobConfig,
          @JsonProperty("status") String status,
          @JsonProperty("allowFailure") boolean allowFailure,
          @JsonProperty("dependencies") List<String> dependencies,
          @JsonProperty("runInDocker") boolean runInDocker) {
    this.jobConfig = jobConfig;
    this.status = status;
    this.allowFailure = allowFailure;
    this.dependencies = dependencies != null ? dependencies : List.of();
    this.runInDocker = runInDocker;
    this.startTime = Instant.now();
  }

  /**
   * Constructs a new JobExecution instance based on its definition for testing.
   *
   * @param jobConfig The definition of the job.
   */
  public JobRunState(JobConfig jobConfig) {
    this(jobConfig, ExecutionState.PENDING.name(), jobConfig.isAllowFailure(), jobConfig.getNeeds(), true);
  }

  /**
   * Constructs a new JobExecution instance based on its name and status for testing.
   *
   * @param jobName      The name of the job.
   * @param status       The status of the job.
   * @param allowFailure Whether the job is allowed to fail.
   */
  public JobRunState(String jobName, String status, boolean allowFailure) {
    this(new JobConfig(jobName, "default-stage", "default-image", List.of(), List.of(), allowFailure, null),
            status, allowFailure, List.of(), true);
  }

  /**
   * Constructs a new JobExecution instance based on its name.
   *
   * @param jobName The name of the job.
   */
  public JobRunState(String jobName) {
    this(new JobConfig(jobName, "default-stage", "default-image",
                    List.of(), List.of(), false, null),
            ExecutionState.PENDING.name(),
            false,
            List.of(),
            true
    );
  }

  /**
   * Marks the job as started.
   */
  public void start() {
    this.status = ExecutionState.RUNNING.name();
    this.startTime = Instant.now();
  }

  /**
   * Marks the job as completed.
   *
   * @param finalStatus The final status of the job.
   */
  public void complete(String finalStatus) {
    this.status = finalStatus;
    this.completionTime = Instant.now();
  }

  /**
   * Checks if the job should run inside a Docker container.
   *
   * @return true if the job should be executed in Docker, false otherwise.
   */
  public boolean shouldRunInDocker() {
    return runInDocker;
  }

  /**
   * Gets the job name.
   *
   * @return the job name
   */
  public String getJobName() {
    return jobConfig.getName();
  }

  /**
   * Gets the job status.
   *
   * @return the job status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Checks if the job is allowed to fail.
   *
   * @return true if allowed to fail, false otherwise
   */
  public boolean isAllowFailure() {
    return allowFailure;
  }

  /**
   * Gets the start time of the job.
   *
   * @return the start time
   */
  public Instant getStartTime() {
    return startTime;
  }

  /**
   * Gets the completion time of the job.
   *
   * @return the completion time
   */
  public Instant getCompletionTime() {
    return completionTime;
  }

  /**
   * Gets the job definition.
   *
   * @return the job definition
   */
  public JobConfig getJobDefinition() {
    return jobConfig;
  }
}
