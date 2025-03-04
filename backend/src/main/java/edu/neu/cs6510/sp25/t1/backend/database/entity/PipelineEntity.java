package edu.neu.cs6510.sp25.t1.backend.database.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a CI/CD pipeline, which consists of multiple stages.
 */
@Entity
@Table(name = "pipelines")
public class PipelineEntity {
  @Id
  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String repositoryUrl;

  @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<StageEntity> stageEntities;

  /**
   * Default constructor for JPA.
   */
  public PipelineEntity() {
  }

  /**
   * Constructs a new Pipeline entity.
   *
   * @param name The unique name of the pipeline.
   */
  public PipelineEntity(String name) {
    this.name = name;
    this.repositoryUrl = repositoryUrl;
  }

  /**
   * Retrieves the name of the pipeline.
   *
   * @return The name of the pipeline.
   */
  public String getName() {
    return name;
  }

  /**
   * Retrieves the stage of the pipeline.
   *
   * @return The stage of the pipeline.
   */
  public List<StageEntity> getStages() {
    return stageEntities;
  }

  /**
   * sets the name of the pipeline.
   *
   * @param name The name of the pipeline.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * sets the stages of the pipeline.
   *
   * @param stageEntities The stages of the pipeline.
   */
  public void setStages(List<StageEntity> stageEntities) {
    this.stageEntities = stageEntities;
  }

  public String getRepositoryUrl() { return repositoryUrl; }

  public void setRepositoryUrl(String repositoryUrl) { this.repositoryUrl = repositoryUrl; }
}
