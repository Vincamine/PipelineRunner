package edu.neu.cs6510.sp25.t1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a stage within a pipeline run in the CI/CD system.
 */
public class StageInfo {
  private final String stageName;
  private final String stageStatus;
  private final long startTime;
  private final long completionTime;
  private final List<String> jobs; 

  /**
   * Constructs a new StageInfo instance.
   *
   * @param stageName      The name of the stage.
   * @param stageStatus    The status of the stage (e.g., SUCCESS, FAILED, CANCELED).
   * @param startTime      The timestamp when the stage started (milliseconds).
   * @param completionTime The timestamp when the stage completed (milliseconds).
   * @param jobs           The list of jobs in this stage.
   */
  @JsonCreator
  public StageInfo(
      @JsonProperty("stageName") String stageName,
      @JsonProperty("stageStatus") String stageStatus,
      @JsonProperty("startTime") long startTime,
      @JsonProperty("completionTime") long completionTime,
      @JsonProperty("jobs") List<String> jobs) {
    this.stageName = stageName;
    this.stageStatus = stageStatus;
    this.startTime = startTime;
    this.completionTime = completionTime;
    this.jobs = jobs != null ? jobs : List.of(); 
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

  public List<String> getJobs() {
    return jobs;
  }
}
