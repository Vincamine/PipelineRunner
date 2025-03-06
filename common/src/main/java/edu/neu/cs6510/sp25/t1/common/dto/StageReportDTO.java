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
  private String stageName;
  private List<ExecutionRecord> executionRecords;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ExecutionRecord {
    private UUID id;
    private ExecutionStatus status;
    private Instant startTime;
    private Instant completionTime;
  }
}
