package edu.neu.cs6510.sp25.t1.backend.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a CI/CD pipeline configuration in the system.
 * A pipeline consists of multiple stages and is associated with a repository.
 */
@Entity
@Table(name = "pipelines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineEntity {

  /**
   * Unique identifier for the pipeline.
   */
  @Id
  @GeneratedValue
  private UUID id;

  /**
   * Unique name of the pipeline within the repository.
   */
  @Column(name = "name", nullable = false, unique = true)
  private String name;

  /**
   * URL or local path of the repository associated with this pipeline.
   */
  @Column(name = "repository_url", nullable = false)
  private String repoUrl;

  /**
   * Git branch name associated with this pipeline. Defaults to "main".
   */
  @Column(name = "branch", columnDefinition = "VARCHAR(255) DEFAULT 'main'")
  private String branch;

  /**
   * The Git commit hash associated with this pipeline execution.
   */
  @Column(name = "commit_hash", length = 40)
  private String commitHash;

  /**
   * Timestamp of when the pipeline was created.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the pipeline was last updated.
   */
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /**
   * Lifecycle hook to set default timestamps before persisting.
   */
  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Lifecycle hook to update the timestamp before updating.
   */
  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
