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
public class PipelineReportDTO {
  private UUID id;
  private String pipelineName;
  private int runNumber;
  private String commitHash;
  private ExecutionStatus status;
  private Instant startTime;
  private Instant completionTime;
  private List<StageReportDTO> stageReports;
}
