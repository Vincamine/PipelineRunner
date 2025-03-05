package edu.neu.cs6510.sp25.t1.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Represents a job in a CI/CD pipeline configuration.
 * Static configuration data.
 */
public class Job {
  private final UUID id;
  private final UUID stageId;
  private final String name;
  private final String dockerImage;
  private final List<String> script;
  private final List<UUID> dependencies;
  private final boolean allowFailure;
  private final List<String> artifacts;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  /**
   * Constructor for Job.
   *
   * @param id           Job ID (UUID)
   * @param stageId      ID of the stage this job belongs to
   * @param name         Job name
   * @param dockerImage  Docker image used for execution
   * @param script       List of commands to run (should be stored separately in DB)
   * @param dependencies Job dependencies (other job IDs)
   * @param allowFailure Whether the job can fail without failing the pipeline
   * @param artifacts    List of artifacts produced by the job
   * @param createdAt    Timestamp when the job was created
   * @param updatedAt    Timestamp when the job was last updated
   */
  @JsonCreator
  public Job(
          @JsonProperty("id") UUID id,
          @JsonProperty("stageId") UUID stageId,
          @JsonProperty("name") String name,
          @JsonProperty("dockerImage") String dockerImage,
          @JsonProperty("script") List<String> script,
          @JsonProperty("dependencies") List<UUID> dependencies,
          @JsonProperty("allowFailure") boolean allowFailure,
          @JsonProperty("artifacts") List<String> artifacts,
          @JsonProperty("createdAt") LocalDateTime createdAt,
          @JsonProperty("updatedAt") LocalDateTime updatedAt) {
    this.id = id;
    this.stageId = stageId;
    this.name = name;
    this.dockerImage = dockerImage;
    this.script = script != null ? script : List.of();
    this.dependencies = dependencies != null ? dependencies : List.of();
    this.allowFailure = allowFailure;
    this.artifacts = artifacts != null ? artifacts : List.of();
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // ========================
  // Getters for the fields
  // ========================


  /**
   * Getter for Job ID.
   *
   * @return ID
   */
  public UUID getId() {
    return id;
  }

  /**
   * Getter for Stage ID.
   *
   * @return Stage ID
   */
  public UUID getStageId() {
    return stageId;
  }

  /**
   * Getter for Job name.
   *
   * @return Job name
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for Docker image.
   *
   * @return Docker image
   */
  public String getDockerImage() {
    return dockerImage;
  }

  /**
   * Getter for script.
   *
   * @return script
   */
  public List<String> getScript() {
    return script;
  }

  /**
   * Getter for dependencies.
   *
   * @return dependencies
   */
  public List<UUID> getDependencies() {
    return dependencies;
  }

  /**
   * Getter for allowFailure.
   *
   * @return allowFailure
   */
  public boolean isAllowFailure() {
    return allowFailure;
  }

  /**
   * Getter for artifacts.
   *
   * @return artifacts
   */
  public List<String> getArtifacts() {
    return artifacts;
  }

  /**
   * Getter for creation timestamp.
   *
   * @return creation timestamp
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Getter for last updated timestamp.
   *
   * @return last updated timestamp
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
