package edu.neu.cs6510.sp25.t1.common.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for stage data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageDTO {

  /**
   * Unique identifier for the stage.
   */
  private UUID id;

  /**
   * Name of the stage.
   */
  private String name;

  /**
   * Foreign key reference to the pipeline.
   */
  private UUID pipelineId;

  /**
   * Execution order of the stage within the pipeline.
   */
  private int executionOrder;

  /**
   * Timestamp of when the stage was created.
   */
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the stage was last updated.
   */
  private LocalDateTime updatedAt;
}
