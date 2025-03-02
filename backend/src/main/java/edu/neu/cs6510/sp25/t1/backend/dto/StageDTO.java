package edu.neu.cs6510.sp25.t1.backend.dto;

import java.util.List;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.backend.entity.Stage;

/**
 * Data Transfer Object (DTO) for Stage.
 * Used for API communication to prevent direct exposure of JPA entities.
 */
public class StageDTO {
  private String name;
  private String pipelineName;
  private List<JobDTO> jobs;

  /**
   * Constructs a StageDTO.
   *
   * @param name         The name of the stage.
   * @param pipelineName The name of the associated pipeline.
   * @param jobs         The list of job DTOs in this stage.
   */
  public StageDTO(String name, String pipelineName, List<JobDTO> jobs) {
    this.name = name;
    this.pipelineName = pipelineName;
    this.jobs = jobs;
  }

  /**
   * Converts a Stage entity to a DTO.
   *
   * @param stage The Stage entity.
   * @return A StageDTO representation of the entity.
   */
  public static StageDTO fromEntity(Stage stage) {
    return new StageDTO(
            stage.getName(),
            stage.getPipeline().getName(),
            stage.getJobs() != null
                    ? stage.getJobs().stream().map(JobDTO::fromEntity).collect(Collectors.toList())
                    : List.of()
    );
  }

  /**
   * get the name of the stage
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * get the name of the pipeline
   *
   * @return pipelineName
   */
  public String getPipelineName() {
    return pipelineName;
  }

  /**
   * get the jobs of the stage
   *
   * @return jobs
   */
  public List<JobDTO> getJobs() {
    return jobs;
  }

  /**
   * set the name of the stage
   *
   * @param name The name of the stage
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * set the name of the pipeline
   *
   * @param pipelineName The name of the pipeline
   */
  public void setPipelineName(String pipelineName) {
    this.pipelineName = pipelineName;
  }

  /**
   * set the jobs of the stage
   *
   * @param jobs The jobs of the stage
   */
  public void setJobs(List<JobDTO> jobs) {
    this.jobs = jobs;
  }
}
