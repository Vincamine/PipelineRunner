package edu.neu.cs6510.sp25.t1.common.dto;


import java.time.Instant;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for stage execution data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageExecutionDTO {

  /**
   * Unique identifier for the stage execution.
   */
  private UUID id;

  /**
   * Foreign key reference to the stage.
   */
  private UUID stageId;

  /**
   * Foreign key reference to the pipeline execution.
   */
  private UUID pipelineExecutionId;

  /**
   * Execution order of the stage within the pipeline.
   */
  private int executionOrder;

  /**
   * Git commit hash associated with this execution.
   */
  private String commitHash;

  /**
   * Indicates whether this execution is local.
   */
  private boolean isLocal;

  /**
   * Execution status of the stage.
   */
  private ExecutionStatus status;

  /**
   * Timestamp of when the execution started.
   */
  private Instant startTime;

  /**
   * Timestamp of when the execution completed.
   */
  private Instant completionTime;
}
