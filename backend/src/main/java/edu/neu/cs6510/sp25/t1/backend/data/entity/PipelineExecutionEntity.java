package edu.neu.cs6510.sp25.t1.backend.data.entity;

import java.time.Instant;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a pipeline execution entity stored in the database.
 */
@Entity
@Table(name = "pipeline_executions")
public class PipelineExecutionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String pipelineName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ExecutionStatus state;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  public PipelineExecutionEntity() {
  }

  public PipelineExecutionEntity(String pipelineName, ExecutionStatus state, Instant createdAt) {
    this.pipelineName = pipelineName;
    this.state = state;
    this.createdAt = createdAt;
  }

  // Getters & Setters
  public Long getId() {
    return id;
  }

  public String getPipelineName() {
    return pipelineName;
  }

  public ExecutionStatus getState() {
    return state;
  }

  public void setState(ExecutionStatus state) {
    this.state = state;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
