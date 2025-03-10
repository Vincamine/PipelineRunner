package edu.neu.cs6510.sp25.t1.common.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
