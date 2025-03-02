package edu.neu.cs6510.sp25.t1.common.runtime;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;

/**
 * Represents an active execution of a CI/CD pipeline. Defines execution state & metadata.
 * This class is responsible for managing the state of the pipeline execution,
 * including the progress of each stage and job, and updating the overall pipeline state.
 * It also provides methods to retrieve the current state and progress of the pipeline execution.
 */
public class PipelineRunState {
  private final String pipelineName; // unique identifier for the pipeline; not pipeline ID
  private ExecutionState state; // current state of the pipeline execution
  private final Instant startTime; // start time of the pipeline execution
  private Instant lastUpdated; // last updated time of the pipeline execution
  private final List<StageRunState> stages; // list of stages in the pipeline execution
  private final List<JobRunState> jobs; // list of jobs in the pipeline execution

  /**
   * Constructor for PipelineExecution.
   * Initializes the pipeline execution with the given pipeline name, stages, and jobs.
   *
   * @param pipelineName the name of the pipeline
   * @param stages       the list of stages in the pipeline
   * @param jobs         the list of jobs in the pipeline
   */
  public PipelineRunState(String pipelineName, List<StageRunState> stages, List<JobRunState> jobs) {
    this.pipelineName = pipelineName;
    this.state = ExecutionState.PENDING;
    this.startTime = Instant.now();
    this.lastUpdated = Instant.now();
    this.stages = stages != null ? stages : new ArrayList<>();
    this.jobs = jobs != null ? jobs : new ArrayList<>();
  }

  /**
   * Constructor for initializing a pipeline execution with only a name.
   *
   * @param pipelineName the name of the pipeline
   */
  public PipelineRunState(String pipelineName) {
    this(pipelineName, new ArrayList<>(), new ArrayList<>());
  }

  /**
   * Updates the pipeline execution state based on the job execution statuses.
   * This method checks the status of each job in the pipeline execution
   * and updates the overall pipeline state accordingly.
   *
   * <p>The pipeline state transitions:</p>
   * <ul>
   *   <li>PENDING: No jobs have started yet.</li>
   *   <li>RUNNING: Any job is currently running.</li>
   *   <li>SUCCESS: All jobs have succeeded.</li>
   *   <li>FAILED: A job has failed and is not allowed to fail.</li>
   *   <li>CANCELED: A job has been canceled.</li>
   * </ul>
   *
   * The last updated timestamp is also updated.
   */
  public void updateState() {
    if (jobs.isEmpty()) {
      this.state = ExecutionState.PENDING;
      return;
    }

    boolean hasFailed = jobs.stream()
            .anyMatch(j -> ExecutionState.FAILED.name().equals(j.getStatus()) && !j.isAllowFailure());

    boolean hasCanceled = jobs.stream()
            .anyMatch(j -> ExecutionState.CANCELED.name().equals(j.getStatus()));

    boolean allSucceeded = jobs.stream()
            .allMatch(j -> ExecutionState.SUCCESS.name().equals(j.getStatus()));

    boolean anyRunning = jobs.stream()
            .anyMatch(j -> ExecutionState.RUNNING.name().equals(j.getStatus()));

    if (hasFailed) {
      this.state = ExecutionState.FAILED;
    } else if (hasCanceled) {
      this.state = ExecutionState.CANCELED;
    } else if (allSucceeded) {
      this.state = ExecutionState.SUCCESS;
    } else if (anyRunning) {
      this.state = ExecutionState.RUNNING;
    } else {
      this.state = ExecutionState.PENDING;
    }

    this.lastUpdated = Instant.now();
  }

  /**
   * Get the pipeline name.
   *
   * @return the pipeline name
   */
  public String getPipelineName() {
    return pipelineName;
  }

  /**
   * Get the current execution state of the pipeline.
   *
   * @return the pipeline execution state
   */
  public ExecutionState getState() {
    return state;
  }

  /**
   * Get the start time of the pipeline execution.
   *
   * @return the start time of the pipeline execution
   */
  public Instant getStartTime() {
    return startTime;
  }

  /**
   * Get the last updated time of the pipeline execution.
   *
   * @return the last updated time of the pipeline execution
   */
  public Instant getLastUpdated() {
    return lastUpdated;
  }

  /**
   * Set the pipeline execution state.
   *
   * @param state the new state of the pipeline execution
   */
  public void setState(ExecutionState state) {
    this.state = state;
    this.lastUpdated = Instant.now();
  }

  /**
   * Get the list of stages in the pipeline execution.
   *
   * @return list of stage execution states
   */
  public List<StageRunState> getStages() {
    return stages;
  }

  /**
   * Get the list of jobs in the pipeline execution.
   *
   * @return list of job execution states
   */
  public List<JobRunState> getJobs() {
    return jobs;
  }
}
