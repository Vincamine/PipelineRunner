package edu.neu.cs6510.sp25.t1.backend.database.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class for jobs.
 */
@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(name = "stage_id", nullable = false)
  private UUID stageId;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "docker_image", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'docker.io/library/alpine:latest'")
  private String dockerImage;

  @Column(name = "allow_failure", columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean allowFailure;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @ElementCollection
  @CollectionTable(name = "job_scripts", joinColumns = @JoinColumn(name = "job_id"))
  @Column(name = "script")
  private List<String> script;

  @ElementCollection
  @CollectionTable(name = "job_dependencies", joinColumns = @JoinColumn(name = "job_id"))
  @Column(name = "depends_on_job_id")
  private List<UUID> dependencies;

  @ElementCollection
  @CollectionTable(name = "job_artifacts", joinColumns = @JoinColumn(name = "job_id"))
  @Column(name = "artifact_path")
  private List<String> artifacts;

  /**
   * Create the created at and updated at timestamps.
   */
  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Update the updated at timestamp.
   */
  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
