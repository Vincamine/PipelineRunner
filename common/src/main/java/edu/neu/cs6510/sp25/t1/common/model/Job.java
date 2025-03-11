package edu.neu.cs6510.sp25.t1.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Getter;

/**
 * Represents a job in a CI/CD pipeline configuration.
 * Static configuration data.
 */
@Getter
public class Job {

  // Getters with lombok
  private final UUID id;  // This will be generated when saving to the database

  private final UUID stageId;

  private final String name;

  private final String dockerImage;

  private final List<String> script;

  private final List<String> dependencies;  // 🔧 Store as job names instead of UUIDs

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
   * @param dependencies Job dependencies (other job names, will be resolved to UUIDs later)
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
          @JsonProperty("dependencies") List<String> dependencies,  // 🔧 Accept job names instead of UUIDs
          @JsonProperty("allowFailure") boolean allowFailure,
          @JsonProperty("artifacts") List<String> artifacts,
          @JsonProperty("createdAt") LocalDateTime createdAt,
          @JsonProperty("updatedAt") LocalDateTime updatedAt) {
    this.id = id;
    this.stageId = stageId;
    this.name = name;
    this.dockerImage = dockerImage;
    this.script = script != null ? script : List.of();
    this.dependencies = dependencies != null ? dependencies : List.of();  // 🔧 Default empty list
    this.allowFailure = allowFailure;
    this.artifacts = artifacts != null ? artifacts : List.of();
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
