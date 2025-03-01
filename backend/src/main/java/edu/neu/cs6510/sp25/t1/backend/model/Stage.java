package edu.neu.cs6510.sp25.t1.backend.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Represents a stage in a CI/CD pipeline.
 * A stage contains multiple jobs that can be executed sequentially or in parallel.
 */
@Entity
@Table(name = "stages")
public class Stage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pipeline_id", nullable = false)
  private Pipeline pipeline;

  @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Job> jobs;

  /**
   * Default constructor for JPA.
   */
  public Stage() {
  }

  /**
   * Constructs a new Stage entity.
   *
   * @param name     The name of the stage.
   * @param pipeline The associated pipeline.
   */
  public Stage(String name, Pipeline pipeline) {
    this.name = name;
    this.pipeline = pipeline;
  }

  /**
   * Retrieves the ID of the stage.
   * This ID is unique within the database.
   * @return The ID of the stage.
   */
  public Long getId() {
    return id;
  }

  /**
   * Retrieves the name of the stage.
   * This name is unique within the pipeline.
   * @return The name of the stage.
   */
  public String getName() {
    return name;
  }

  /**
   * Retrieves the pipeline associated with the stage.
   * @return The pipeline associated with the stage.
   */
  public Pipeline getPipeline() {
    return pipeline;
  }

  /**
   * Retrieves the list of jobs in the stage.
   * @return The list of jobs in the stage.
   */
  public List<Job> getJobs() {
    return jobs;
  }

  /**
   * Sets the ID of the stage.
   * @param id The ID of the stage.
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Sets the name of the stage.
   * @param name The name of the stage.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the pipeline associated with the stage.
   * @param pipeline The pipeline associated with the stage.
   */
  public void setPipeline(Pipeline pipeline) {
    this.pipeline = pipeline;
  }

  /**
   * Sets the list of jobs in the stage.
   * @param jobs The list of jobs in the stage.
   */
  public void setJobs(List<Job> jobs) {
    this.jobs = jobs;
  }
}
