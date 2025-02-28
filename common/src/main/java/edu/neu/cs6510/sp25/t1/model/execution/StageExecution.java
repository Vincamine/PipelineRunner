package edu.neu.cs6510.sp25.t1.model.execution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.neu.cs6510.sp25.t1.model.definition.StageDefinition;
import edu.neu.cs6510.sp25.t1.model.PipelineState;
import java.time.Instant;
import java.util.List;

/**
 * Represents a stage execution instance in a CI/CD pipeline.
 */
public class StageExecution {
    private final StageDefinition stageDefinition;
    private PipelineState stageStatus;
    private Instant startTime;
    private Instant completionTime;
    private List<JobExecution> jobExecutions;

    /**
     * Constructs a new StageExecution based on its definition.
     * @param stageDefinition the stage definition
     * @param jobExecutions the list of job executions
     */
    @JsonCreator
    public StageExecution(
            @JsonProperty("stageDefinition") StageDefinition stageDefinition,
            @JsonProperty("jobExecutions") List<JobExecution> jobExecutions) {
        this.stageDefinition = stageDefinition;
        this.jobExecutions = jobExecutions;
        this.stageStatus = PipelineState.PENDING;
        this.startTime = Instant.now();
    }

    /**
     * Computes the stage execution status dynamically.
     * This method should be called whenever the status of any job changes.
     */
    public void updateStatus() {
        boolean hasFailed = jobExecutions.stream()
                .anyMatch(job -> job.getStatus().equals("FAILED") && !job.isAllowFailure());

        boolean hasCanceled = jobExecutions.stream()
                .anyMatch(job -> job.getStatus().equals("CANCELED"));

        if (hasFailed) {
            this.stageStatus = PipelineState.FAILED;
        } else if (hasCanceled) {
            this.stageStatus = PipelineState.CANCELED;
        } else if (jobExecutions.stream().allMatch(job -> job.getStatus().equals("SUCCESS"))) {
            this.stageStatus = PipelineState.SUCCESS;
        } else {
            this.stageStatus = PipelineState.RUNNING;
        }
    }

    /**
     * Marks the stage as completed.
     */
    public void complete() {
        this.completionTime = Instant.now();
    }

    /**
     * Getter for stage name.
     * @return stage name
     */
    public String getStageName() { return stageDefinition.getName(); }

    /**
     * Getter for stage status.
     * @return stage status
     */
    public PipelineState getStageStatus() { return stageStatus; }

    /**
     * Getter for start time.
     * @return start time
     */
    public Instant getStartTime() { return startTime; }

    /**
     * Getter for completion time.
     * @return completion time
     */
    public Instant getCompletionTime() { return completionTime; }

    /**
     * Getter for job executions.
     * @return the list of job executions
     */
    public List<JobExecution> getJobExecutions() { return jobExecutions; }
}
