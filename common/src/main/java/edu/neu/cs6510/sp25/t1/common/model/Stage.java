package edu.neu.cs6510.sp25.t1.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Represents a stage in a CI/CD pipeline configuration.
 */
public class Stage {
  private final UUID id;
  private final String name;
  private final UUID pipelineId;
  private final int executionOrder;
  private final List<Job> jobs;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  /**
   * Constructor for Stage.
   *
   * @param id             Unique identifier for the stage
   * @param name           Stage name
   * @param pipelineId     Reference to the pipeline this stage belongs to
   * @param executionOrder Execution order of the stage
   * @param jobs           List of jobs in the stage
   * @param createdAt      Timestamp of stage creation
   * @param updatedAt      Timestamp of last update
   */
  @JsonCreator
  public Stage(
          @JsonProperty("id") UUID id,
          @JsonProperty("name") String name,
          @JsonProperty("pipelineId") UUID pipelineId,
          @JsonProperty("executionOrder") int executionOrder,
          @JsonProperty("jobs") List<Job> jobs,
          @JsonProperty("createdAt") LocalDateTime createdAt,
          @JsonProperty("updatedAt") LocalDateTime updatedAt) {
    this.id = id;
    this.name = name;
    this.pipelineId = pipelineId;
    this.executionOrder = executionOrder;
    this.jobs = jobs;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public UUID getPipelineId() {
    return pipelineId;
  }

  public int getExecutionOrder() {
    return executionOrder;
  }

  public List<Job> getJobs() {
    return jobs;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
