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
 * Represents a stage in a CI/CD pipeline configuration.
 * A stage consists of multiple jobs and is executed as part of a pipeline.
 */
@Entity
@Table(name = "stages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageEntity {

  /**
   * Unique identifier for the stage.
   */
  @Id
  @GeneratedValue
  private UUID id;

  /**
   * Name of the stage, unique within a pipeline.
   */
  @Column(name = "name", nullable = false)
  private String name;

  /**
   * Foreign key reference to the pipeline this stage belongs to.
   */
  @Column(name = "pipeline_id", nullable = false)
  private UUID pipelineId;

  /**
   * Execution order of the stage within the pipeline.
   */
  @Column(name = "execution_order", nullable = false, columnDefinition = "INT DEFAULT 0")
  private int executionOrder;

  /**
   * Timestamp indicating when the stage was created.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp indicating when the stage was last updated.
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
