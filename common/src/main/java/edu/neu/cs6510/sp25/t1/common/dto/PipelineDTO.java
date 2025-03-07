package edu.neu.cs6510.sp25.t1.common.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for pipeline data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineDTO {

  /**
   * Unique identifier for the pipeline.
   */
  private UUID id;

  /**
   * Name of the pipeline.
   */
  private String name;

  /**
   * URL or local path of the repository.
   */
  private String repoUrl;

  /**
   * Git branch name associated with this pipeline.
   */
  private String branch;

  /**
   * Git commit hash associated with this pipeline execution.
   */
  private String commitHash;

  /**
   * Timestamp of when the pipeline was created.
   */
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the pipeline was last updated.
   */
  private LocalDateTime updatedAt;
}
