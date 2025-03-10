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

  private UUID id;
  private UUID stageId;
  private UUID pipelineExecutionId;
  private int executionOrder;
  private String commitHash;
  private boolean isLocal;
  private ExecutionStatus status;
  private Instant startTime;
  private Instant completionTime;
}
