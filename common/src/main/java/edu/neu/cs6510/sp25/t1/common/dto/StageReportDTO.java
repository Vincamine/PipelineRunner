package edu.neu.cs6510.sp25.t1.common.dto;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageReportDTO {
  private UUID stageId;  // Stage identifier
  private String stageName; // Name of the stage
  private ExecutionStatus status; // Overall stage execution status
  private Instant startTime; // Start timestamp
  private Instant completionTime; // Completion timestamp
  private List<JobReportDTO> jobReports; // Jobs inside this stage
}
