package edu.neu.cs6510.sp25.t1.common.model.execution;

import java.time.Instant;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.model.PipelineState;

/**
 * Represents an active execution of a CI/CD pipeline.
 * This class is responsible for managing the state of the pipeline execution,
 * including the progress of each stage and job, and updating the overall pipeline state.
 * It also provides methods to retrieve the current state and progress of the pipeline execution.
 */
public class PipelineExecution {
    private final String pipelineName; // unique identifier for the pipeline; not pipeline ID
    private PipelineState state; // current state of the pipeline execution
    @SuppressWarnings("unused")
    private int progress; // progress of the pipeline execution
    private final Instant startTime; // start time of the pipeline execution
    private Instant lastUpdated; // last updated time of the pipeline execution
    @SuppressWarnings("unused")
    private final List<StageExecution> stages; // list of stages in the pipeline execution
    private final List<JobExecution> jobs; // list of jobs in the pipeline execution

    /**
     * Constructor for PipelineExecution.
     * Initializes the pipeline execution
     * with the given pipeline name, stages, and jobs.
     * @param pipelineName
     * @param stages
     * @param jobs
     */
    public PipelineExecution(String pipelineName, List<StageExecution> stages, List<JobExecution> jobs) {
        this.pipelineName = pipelineName;
        this.state = PipelineState.PENDING;
        this.progress = 0;
        this.startTime = Instant.now();
        this.lastUpdated = Instant.now();
        this.stages = stages;
        this.jobs = jobs;
    }

    /**
     * Updates the pipeline execution state based on the job execution statuses.
     * This method checks the status of each job in the pipeline execution
     * and updates the overall pipeline state accordingly.
     * The pipeline state can be one of the following:
     * - PENDING: if no jobs have started yet
     * - RUNNING: if any job is currently running
     * - SUCCESS: if all jobs have succeeded
     * - FAILED: if any job has failed and is not allowed to fail
     * - CANCELED: if any job has been canceled
     * The last updated time is also updated to the current time.
     * @return void
     */
    public void updateState() {
        boolean hasFailed = jobs.stream().anyMatch(j -> "FAILED".equals(j.getStatus()) && !j.isAllowFailure());
        boolean hasCanceled = jobs.stream().anyMatch(j -> "CANCELED".equals(j.getStatus()));
        boolean allSucceeded = jobs.stream().allMatch(j -> "SUCCESS".equals(j.getStatus()));
        boolean anyRunning = jobs.stream().anyMatch(j -> "RUNNING".equals(j.getStatus()));

        if (hasFailed) {
            this.state = PipelineState.FAILED;
        } else if (hasCanceled) {
            this.state = PipelineState.CANCELED;
        } else if (allSucceeded) {
            this.state = PipelineState.SUCCESS;
        } else if (anyRunning) {
            this.state = PipelineState.RUNNING;
        } else {
            this.state = PipelineState.PENDING;
        }

        this.lastUpdated = Instant.now();
    }

    /**
     * Get the pipeline name.
     * @return the pipeline name
     */
    public String getPipelineName() {
        return pipelineName;
    }

    /**
     * Get the pipeline execution state.
     * @return the pipeline execution state
     */
    public PipelineState getState() {
        return state;
    }

    /**
     * Get the progress of the pipeline execution
     * @return the progress of the pipeline execution
     */
    public Instant getStartTime() {
        return startTime;
    }

    /**
     * Get the last updated time of the pipeline execution
     * @return the last updated time of the pipeline execution
     */
    public Instant getLastUpdated() {
        return lastUpdated;
    }
}
