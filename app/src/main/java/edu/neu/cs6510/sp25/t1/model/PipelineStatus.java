package edu.neu.cs6510.sp25.t1.model;

import java.time.Instant;
import java.util.List;

/**
 * Represents the current execution status of a CI/CD pipeline.
 */
public class PipelineStatus {
    private final String pipelineId;
    private PipelineState state;
    private int progress;
    private String currentStage;
    private String message;
    private final Instant startTime;
    private Instant lastUpdated;
    private final List<StageInfo> stages;
    private final List<JobInfo> jobs;

    /**
     * Constructs a PipelineStatus with a computed state from stages and jobs.
     */
    public PipelineStatus(String pipelineId, List<StageInfo> stages, List<JobInfo> jobs) {
        this.pipelineId = pipelineId;
        this.state = calculatePipelineStatus(stages, jobs);
        this.progress = calculateProgress(stages);
        this.startTime = Instant.now();
        this.lastUpdated = Instant.now();
        this.stages = stages;
        this.jobs = jobs;
    }

    /**
     * Constructs a PipelineStatus with a manually specified state, stages, and
     * jobs.
     */
    public PipelineStatus(String pipelineId, PipelineState state, int progress, String message, List<StageInfo> stages,
            List<JobInfo> jobs) {
        this.pipelineId = pipelineId;
        this.state = state;
        this.progress = progress;
        this.message = message;
        this.startTime = Instant.now();
        this.lastUpdated = Instant.now();
        this.stages = stages;
        this.jobs = jobs;
    }

    /**
     * Constructs a PipelineStatus with a manually specified state.
     */
    public PipelineStatus(String pipelineId, PipelineState state, int progress, String message) {
        this.pipelineId = pipelineId;
        this.state = state;
        this.progress = progress;
        this.message = message;
        this.startTime = Instant.now();
        this.lastUpdated = Instant.now();
        this.stages = List.of(); // Empty stages since it's a manual update
        this.jobs = List.of(); // Empty jobs list
    }

    /**
     * Computes the final status of a pipeline based on its stages and jobs.
     */
    private PipelineState calculatePipelineStatus(List<StageInfo> stages, List<JobInfo> jobs) {
        boolean hasFailed = false;
        boolean hasCanceled = false;

        for (JobInfo job : jobs) {
            if ("FAILED".equals(job.getStatus()) && !job.isAllowFailure()) {
                hasFailed = true;
            }
            if ("CANCELED".equals(job.getStatus())) {
                hasCanceled = true;
            }
        }

        for (StageInfo stage : stages) {
            if ("FAILED".equals(stage.getStageStatus())) {
                hasFailed = true;
            }
            if ("CANCELED".equals(stage.getStageStatus())) {
                hasCanceled = true;
            }
        }

        if (hasFailed)
            return PipelineState.FAILED;
        if (hasCanceled)
            return PipelineState.CANCELED;
        return PipelineState.SUCCESS;
    }

    /**
     * Computes the execution progress percentage.
     */
    private int calculateProgress(List<StageInfo> stages) {
        if (stages.isEmpty()) {
            return 0;
        }
        long completedStages = stages.stream().filter(s -> "SUCCESS".equals(s.getStageStatus())).count();
        return (int) ((completedStages * 100) / stages.size());
    }

    // Getters
    public String getPipelineId() {
        return pipelineId;
    }

    public PipelineState getState() {
        return state;
    }

    public int getProgress() {
        return progress;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public String getMessage() {
        return message;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public List<StageInfo> getStages() {
        return stages;
    }

    public List<JobInfo> getJobs() {
        return jobs;
    }
}
