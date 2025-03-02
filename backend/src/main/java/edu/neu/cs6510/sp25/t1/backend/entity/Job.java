package edu.neu.cs6510.sp25.t1.backend.entity;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents a job in a CI/CD pipeline.
 * This entity is stored in the database and should not be exposed directly in API responses.
 */
@Entity
@Table(name = "jobs")
public class Job {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String image;

  @ElementCollection
  @CollectionTable(name = "job_scripts", joinColumns = @JoinColumn(name = "job_id"))
  @Column(name = "script")
  private List<String> script;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stage_id", nullable = false)
  private Stage stage;

  @Column(nullable = false)
  private boolean allowFailure;

  @Column
  private Instant startTime;

  @Column
  private Instant completionTime;

  /**
   * Default constructor for JPA.
   */
  public Job() {
  }

  /**
   * Constructs a new Job entity.
   *
   * @param name         The name of the job.
   * @param image        The Docker image to use for this job.
   * @param script       The list of commands to execute.
   * @param stage        The associated stage in the pipeline.
   * @param allowFailure Whether the job is allowed to fail without failing the pipeline.
   */
  public Job(String name, String image, List<String> script, Stage stage, boolean allowFailure) {
    this.name = name;
    this.image = image;
    this.script = script;
    this.stage = stage;
    this.allowFailure = allowFailure;
  }

  /**
   * get the id of the job
   *
   * @return id
   */
  public Long getId() {
    return id;
  }

  /**
   * get the name of the job
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * get the image of the job
   *
   * @return image
   */
  public String getImage() {
    return image;
  }

  /**
   * get the script of the job
   *
   * @return script
   */
  public List<String> getScript() {
    return script;
  }

  /**
   * get the stage of the job
   *
   * @return stage
   */
  public Stage getStage() {
    return stage;
  }

  /**
   * get the allowFailure of the job
   *
   * @return allowFailure
   */
  public boolean isAllowFailure() {
    return allowFailure;
  }

  /**
   * get the start time of the job
   *
   * @return startTime
   */
  public Instant getStartTime() {
    return startTime;
  }

  /**
   * get the completion time of the job
   *
   * @return completionTime
   */
  public Instant getCompletionTime() {
    return completionTime;
  }

  /**
   * set the id of the job
   *
   * @param id the id of the job
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * set the name of the job
   *
   * @param name the name of the job
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * set the image of the job
   *
   * @param image the Docker image to use for this job
   */
  public void setImage(String image) {
    this.image = image;
  }

  /**
   * set the script of the job
   *
   * @param script the list of commands to execute
   */
  public void setScript(List<String> script) {
    this.script = script;
  }

  /**
   * set the stage of the job
   *
   * @param stage the associated stage in the pipeline
   */
  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * set the allowFailure of the job
   *
   * @param allowFailure whether the job is allowed to fail without failing the pipeline
   */
  public void setAllowFailure(boolean allowFailure) {
    this.allowFailure = allowFailure;
  }

  /**
   * set the start time of the job
   *
   * @param startTime the start time of the job
   */
  public void setStartTime(Instant startTime) {
    this.startTime = startTime;
  }

  /**
   * set the completion time of the job
   *
   * @param completionTime the completion time of the job
   */
  public void setCompletionTime(Instant completionTime) {
    this.completionTime = completionTime;
  }
}

