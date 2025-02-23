package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a stage within a pipeline run in the CI/CD system.
 */
public class StageInfo {
  private final String stageName;
  private final String stageStatus;
  private final long startTime;
  private final long completionTime;

  /**
   * Constructs a new StageInfo instance.
   *
   * @param stageName      The name of the stage.
   * @param stageStatus    The status of the stage (e.g., SUCCESS, FAILED,
   *                       CANCELED).
   * @param startTime      The timestamp when the stage started (milliseconds).
   * @param completionTime The timestamp when the stage completed (milliseconds).
   */
  @JsonCreator
  public StageInfo(
      @JsonProperty("stageName") String stageName,
      @JsonProperty("stageStatus") String stageStatus,
      @JsonProperty("startTime") long startTime,
      @JsonProperty("completionTime") long completionTime) {
    this.stageName = stageName;
    this.stageStatus = stageStatus;
    this.startTime = startTime;
    this.completionTime = completionTime;
  }

  public String getStageName() {
    return stageName;
  }

  public String getStageStatus() {
    return stageStatus;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getCompletionTime() {
    return completionTime;
  }
}