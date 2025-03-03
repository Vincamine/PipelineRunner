package edu.neu.cs6510.sp25.t1.backend.data.entity;

import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "stages")
public class StageEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pipeline_name", referencedColumnName = "name", nullable = false)
  private PipelineEntity pipeline;

  @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<JobEntity> jobs;

  @OneToMany(mappedBy = "stageExecution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<StageExecutionEntity> stageExecutions; // ✅ Link to execution history

  public StageEntity() {}

  public StageEntity(String name, PipelineEntity pipeline) {
    this.name = name;
    this.pipeline = pipeline;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public PipelineEntity getPipeline() {
    return pipeline;
  }

  public List<JobEntity> getJobs() {
    return jobs;
  }

  public List<StageExecutionEntity> getStageExecutions() {
    return stageExecutions;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPipeline(PipelineEntity pipeline) {
    this.pipeline = pipeline;
  }

  public void setJobs(List<JobEntity> jobs) {
    this.jobs = jobs;
  }

  public void setStageExecutions(List<StageExecutionEntity> stageExecutions) {
    this.stageExecutions = stageExecutions;
  }
}
