package edu.neu.cs6510.sp25.t1.common.runtime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.config.JobConfig;
import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;

/**
 * Represents a job execution instance within a pipeline stage.
 * This class is used to track the status of a job execution.
 * It contains the job definition, status, start time, completion time, and dependencies.
 */
public class JobRunState {
  private final JobConfig jobConfig;
  private String status;
  private final boolean allowFailure;
  private Instant startTime;
  private Instant completionTime;
  private final List<String> dependencies;

  /**
   * Constructs a new JobExecution instance based on its definition.
   *
   * @param jobConfig The definition of the job.
   * @param status        The status of the job.
   * @param allowFailure  Whether the job is allowed to fail.
   * @param dependencies  The list of dependencies for the job.
   */
  @JsonCreator
  public JobRunState(
          @JsonProperty("jobDefinition") JobConfig jobConfig,
          @JsonProperty("status") String status,
          @JsonProperty("allowFailure") boolean allowFailure,
          @JsonProperty("dependencies") List<String> dependencies) {
    this.jobConfig = jobConfig;
    this.status = status;
    this.allowFailure = allowFailure;
    this.dependencies = dependencies;
    this.startTime = Instant.now();
  }

  /**
   * Constructs a new JobExecution instance based on its name and status for integration testing.
   *
   * @param jobName      The name of the job.
   * @param status       The status of the job.
   * @param allowFailure Whether the job is allowed to fail.
   */
  public JobRunState(String jobName, String status, boolean allowFailure) {
    this.jobConfig = new JobConfig(jobName, "default-stage", "default-image", List.of(), List.of(), false);
    this.status = status;
    this.allowFailure = allowFailure;
    this.dependencies = List.of();
    this.startTime = Instant.now();
  }

  /**
   * Constructs a new JobExecution instance based on its definition for integration testing.
   *
   * @param jobConfig The definition of the job.
   */
  public JobRunState(JobConfig jobConfig) {
    this.jobConfig = jobConfig;
    this.status = ExecutionState.PENDING.name();
    this.allowFailure = jobConfig.isAllowFailure();
    this.dependencies = jobConfig.getNeeds();
    this.startTime = Instant.now();
  }


  /**
   * Constructs a new JobExecution instance based on its name and status for integration testing.
   *
   * @param jobName The name of the job.
   * @param status  The status of the job.
   */
  public JobRunState(String jobName, String status) {
    this.jobConfig = new JobConfig(jobName, "default-stage", "default-image", List.of(), List.of(), false);
    this.status = status;
    this.allowFailure = false;
    this.dependencies = List.of();
    this.startTime = Instant.now();
  }

  /**
   * Constructs a new JobExecution instance based on its name for integration testing.
   *
   * @param jobName The name of the job.
   */
  public JobRunState(String jobName) {
    this(new JobConfig(jobName, "default-stage", "default-image",
                    List.of(), List.of(), false),
            ExecutionState.PENDING.name(),
            false,
            List.of()
    );
  }

  /**
   * Marks the job as started.
   */
  public void start() {
    this.status = "RUNNING";
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
   * get the job name
   *
   * @return job name
   */
  public String getJobName() {
    return jobConfig.getName();
  }

  /**
   * get the status
   *
   * @return status
   */
  public String getStatus() {
    return status;
  }

  /**
   * check if the job is allowed to fail
   *
   * @return true if allowed to fail, false otherwise
   */
  public boolean isAllowFailure() {
    return allowFailure;
  }

  /**
   * get the start time
   *
   * @return start time
   */
  public Instant getStartTime() {
    return startTime;
  }

  /**
   * get the completion time
   *
   * @return completion time
   */
  public Instant getCompletionTime() {
    return completionTime;
  }

  /**
   * get the job definition
   *
   * @return job definition
   */
  public JobConfig getJobDefinition() {
    return jobConfig;
  }
}
