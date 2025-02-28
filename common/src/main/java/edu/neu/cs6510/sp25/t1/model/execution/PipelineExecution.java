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
    private int progress;
    private final Instant startTime;
    private Instant lastUpdated;
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
     * Updates the pipeline execution state based on the stages and jobs.
     */
    public void updateState() {
        boolean hasFailed = jobs.stream().anyMatch(j -> "FAILED".equals(j.getStatus()) && !j.isAllowFailure());
        boolean hasCanceled = jobs.stream().anyMatch(j -> "CANCELED".equals(j.getStatus()));

        if (hasFailed) this.state = PipelineState.FAILED;
        else if (hasCanceled) this.state = PipelineState.CANCELED;
        else this.state = PipelineState.SUCCESS;
    }

    public String getPipelineId() { return pipelineId; }
    public PipelineState getState() { return state; }
}
