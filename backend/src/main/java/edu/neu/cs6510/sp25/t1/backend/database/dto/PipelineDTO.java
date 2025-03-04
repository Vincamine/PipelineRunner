package edu.neu.cs6510.sp25.t1.backend.database.dto;

import java.util.List;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineEntity;

/**
 * Data Transfer Object (DTO) for Pipeline.
 * Used for API communication to prevent direct exposure of JPA entities.
 */
public class PipelineDTO {
  private String name;
  private List<StageDTO> stages;
  private String repositoryUrl;

  /**
   * Constructs a PipelineDTO.
   *
   * @param name   The name of the pipeline.
   * @param stages The list of stage DTOs in the pipeline.
   */
  public PipelineDTO(String name, List<StageDTO> stages) {
    this.name = name;
    this.stages = stages;
  }

  /**
   * Converts a Pipeline entity to a DTO.
   *
   * @param pipelineEntity The Pipeline entity.
   * @return A PipelineDTO representation of the entity.
   */
  public static PipelineDTO fromEntity(PipelineEntity pipelineEntity) {
    return new PipelineDTO(
            pipelineEntity.getName(),
            pipelineEntity.getStages() != null
                    ? pipelineEntity.getStages().stream().map(StageDTO::fromEntity).collect(Collectors.toList())
                    : List.of()
    );
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
   * Retrieves the stages of the pipeline.
   *
   * @return The stages of the pipeline.
   */
  public List<StageDTO> getStages() {
    return stages;
  }

  /**
   * Sets the name of the pipeline.
   *
   * @param name The name of the pipeline.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the stages of the pipeline.
   *
   * @param stages The stages of the pipeline.
   */
  public void setStages(List<StageDTO> stages) {
    this.stages = stages;
  }

  public String getRepositoryUrl() {
    return repositoryUrl;
  }
}
