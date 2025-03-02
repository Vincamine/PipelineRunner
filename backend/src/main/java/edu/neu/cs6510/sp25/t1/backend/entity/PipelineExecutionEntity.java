package edu.neu.cs6510.sp25.t1.backend.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "pipeline_executions")
public class PipelineExecutionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String pipelineName;
  private String status;

  public PipelineExecutionEntity() {}

  public PipelineExecutionEntity(String pipelineName, String status) {
    this.pipelineName = pipelineName;
    this.status = status;
  }

  // Getters & Setters
}
