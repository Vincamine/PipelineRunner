package edu.neu.cs6510.sp25.t1.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Represents the structure of a CI/CD pipeline configuration.
 * Defines a pipeline and its associated metadata.
 */
public class Pipeline {
  private final UUID id;
  private final String name;
  private final String repoUrl;
  private final String branch;
  private final String commitHash;
  private final List<Stage> stages;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  /**
   * Constructs a new Pipeline instance.
   *
   * @param id         Unique pipeline identifier
   * @param name       Pipeline name
   * @param repoUrl    URL or local path of the repository
   * @param branch     Git branch name (default: "main")
   * @param commitHash Git commit hash (optional, latest commit by default)
   * @param stages     List of stages in the pipeline
   * @param createdAt  Timestamp of pipeline creation
   * @param updatedAt  Timestamp of last update
   */
  @JsonCreator
  public Pipeline(
          @JsonProperty("id") UUID id,
          @JsonProperty("name") String name,
          @JsonProperty("repoUrl") String repoUrl,
          @JsonProperty("branch") String branch,
          @JsonProperty("commitHash") String commitHash,
          @JsonProperty("stages") List<Stage> stages,
          @JsonProperty("createdAt") LocalDateTime createdAt,
          @JsonProperty("updatedAt") LocalDateTime updatedAt) {
    this.id = id;
    this.name = name;
    this.repoUrl = repoUrl;
    this.branch = branch != null ? branch : "main"; // Default to "main"
    this.commitHash = commitHash;
    this.stages = stages != null ? stages : List.of();
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // ========================
  // Getters for the fields
  // ========================

  /**
   * Getter for id.
   *
   * @return the id
   */
  public UUID getId() {
    return id;
  }

  /**
   * Getter for name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for repoUrl.
   *
   * @return the repoUrl
   */
  public String getRepoUrl() {
    return repoUrl;
  }

  /**
   * Getter for branch.
   *
   * @return the branch
   */
  public String getBranch() {
    return branch;
  }

  /**
   * Getter for commitHash.
   *
   * @return the commitHash
   */
  public String getCommitHash() {
    return commitHash;
  }

  /**
   * Getter for stages.
   *
   * @return the stages
   */
  public List<Stage> getStages() {
    return stages;
  }

  /**
   * Getter for createdAt.
   *
   * @return the createdAt
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Getter for updatedAt.
   *
   * @return the updatedAt
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
