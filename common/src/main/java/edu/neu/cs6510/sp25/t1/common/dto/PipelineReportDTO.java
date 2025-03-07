package edu.neu.cs6510.sp25.t1.common.dto;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineReportDTO {
  private UUID id;
  private int runNumber;
  private String commitHash;
  private ExecutionStatus status;
  private Instant startTime;
  private Instant completionTime;
}
