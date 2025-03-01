package edu.neu.cs6510.sp25.t1.model.execution;

import java.time.Instant;
import java.util.List;

import edu.neu.cs6510.sp25.t1.model.PipelineState;

/**
 * Represents an active execution of a CI/CD pipeline.
 */
public class PipelineExecution {
    private final String pipelineId;
    private PipelineState state;
    @SuppressWarnings("unused")
    private int progress;
    private final Instant startTime;
    private Instant lastUpdated;
    @SuppressWarnings("unused")
    private final List<StageExecution> stages;
    private final List<JobExecution> jobs;

    public PipelineExecution(String pipelineId, List<StageExecution> stages, List<JobExecution> jobs) {
        this.pipelineId = pipelineId;
        this.state = PipelineState.PENDING;
        this.progress = 0;
        this.startTime = Instant.now();
        this.lastUpdated = Instant.now();
        this.stages = stages;
        this.jobs = jobs;
    }

    /**
     * Updates the pipeline execution state based on the job execution statuses.
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

    public String getPipelineId() { return pipelineId; }
    public PipelineState getState() { return state; }
    public Instant getStartTime() { return startTime; }
    public Instant getLastUpdated() { return lastUpdated; }
}
