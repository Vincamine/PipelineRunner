package edu.neu.cs6510.sp25.t1.backend.entity;

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
public class Pipeline {
  @Id
  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Stage> stages;

  /**
   * Default constructor for JPA.
   */
  public Pipeline() {
  }

  /**
   * Constructs a new Pipeline entity.
   *
   * @param name The unique name of the pipeline.
   */
  public Pipeline(String name) {
    this.name = name;
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
  public List<Stage> getStages() {
    return stages;
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
   * @param stages The stages of the pipeline.
   */
  public void setStages(List<Stage> stages) {
    this.stages = stages;
  }
}
