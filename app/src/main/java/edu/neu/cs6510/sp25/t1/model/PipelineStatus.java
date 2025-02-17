package edu.neu.cs6510.sp25.t1.model;

import java.time.Instant;

/**
 * Represents the current execution status of a CI/CD pipeline.
 * Stores key details such as execution progress, stage, and timestamps.
 */
public class PipelineStatus {
    private final String pipelineId;
    private PipelineState state;
    private int progress;
    private String currentStage;
    private String message;
    private final Instant startTime;
    private Instant lastUpdated;

    /**
     * Constructs a PipelineStatus with an unknown initial state.
     *
     * @param pipelineId The ID of the pipeline.
     */
    public PipelineStatus(String pipelineId) {
        this.pipelineId = pipelineId;
        this.state = PipelineState.UNKNOWN;
        this.progress = 0;
        this.startTime = Instant.now();
        this.lastUpdated = Instant.now();
    }

    /**
     * Constructs a PipelineStatus with detailed attributes.
     *
     * @param pipelineId   The pipeline's unique ID.
     * @param state        The current execution state.
     * @param progress     The execution progress percentage (0-100).
     * @param currentStage The current execution stage.
     * @param startTime    The timestamp when the pipeline started.
     * @param lastUpdated  The timestamp when the status was last updated.
     */
    public PipelineStatus(String pipelineId, PipelineState state, int progress,
                          String currentStage, Instant startTime, Instant lastUpdated) {
        this.pipelineId = pipelineId;
        this.state = state;
        this.progress = Math.max(0, Math.min(progress, 100)); // Ensure valid range
        this.currentStage = currentStage;
        this.startTime = startTime;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public String getPipelineId() { return pipelineId; }
    public PipelineState getState() { return state; }
    public int getProgress() { return progress; }
    public String getCurrentStage() { return currentStage; }
    public String getMessage() { return message; }
    public Instant getStartTime() { return startTime; }
    public Instant getLastUpdated() { return lastUpdated; }

    public void setState(PipelineState state) { this.state = state; }
    pub
