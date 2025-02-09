package edu.neu.cs6510.sp25.t1.cli.model;

import java.time.Instant;

/**
 * Represents the current status of a pipeline execution.
 */
public class PipelineStatus {
  private String pipelineId;
  private PipelineState state;
  private int progress;
  private String currentStage;
  private String message;
  private Instant startTime;
  private Instant lastUpdated;

  public PipelineStatus(String pipelineId) {
    this.pipelineId = pipelineId;
    this.state = PipelineState.UNKNOWN;
    this.progress = 0;
    this.startTime = Instant.now();
    this.lastUpdated = Instant.now();
  }

  public PipelineStatus(String pipelineId, PipelineState state, int progress,
      String currentStage, Instant startTime, Instant lastUpdated) {
    this.pipelineId = pipelineId;
    this.state = state;
    this.progress = progress;
    this.currentStage = currentStage;
    this.startTime = startTime;
    this.lastUpdated = lastUpdated;
  }

  // Getters
  public String getPipelineId() { return pipelineId; }
  public PipelineState getState() { return state; }
  public int getProgress() { return progress; }
  public String getCurrentStage() { return currentStage; }
  public String getMessage() { return message; }
  public Instant getStartTime() { return startTime; }
  public Instant getLastUpdated() { return lastUpdated; }

  // Setters
  public void setState(PipelineState state) { this.state = state; }
  public void setProgress(int progress) { this.progress = progress; }
  public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }
  public void setMessage(String message) { this.message = message; }
  public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
}
