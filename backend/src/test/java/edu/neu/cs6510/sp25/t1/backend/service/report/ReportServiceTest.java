
package edu.neu.cs6510.sp25.t1.backend.service.report;

import edu.neu.cs6510.sp25.t1.backend.database.entity.*;
import edu.neu.cs6510.sp25.t1.backend.database.repository.*;
import edu.neu.cs6510.sp25.t1.common.dto.JobReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.StageReportDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

  private PipelineExecutionRepository pipelineExecutionRepository;
  private StageExecutionRepository stageExecutionRepository;
  private JobExecutionRepository jobExecutionRepository;
  private ReportService reportService;

  @BeforeEach
  void setUp() {
    pipelineExecutionRepository = mock(PipelineExecutionRepository.class);
    stageExecutionRepository = mock(StageExecutionRepository.class);
    jobExecutionRepository = mock(JobExecutionRepository.class);

    reportService = new ReportService(
        pipelineExecutionRepository,
        stageExecutionRepository,
        jobExecutionRepository
    );
  }

  @Test
  void testGetAvailablePipelines() {
    PipelineExecutionEntity execution = new PipelineExecutionEntity();
    execution.setPipelineId(UUID.randomUUID());

    when(pipelineExecutionRepository.findAll()).thenReturn(List.of(execution));
    when(pipelineExecutionRepository.findPipelineNameByPipelineId(any())).thenReturn(Optional.of("DemoPipeline"));

    List<String> result = reportService.getAvailablePipelines();
    assertEquals(1, result.size());
    assertEquals("DemoPipeline", result.get(0));
  }

  @Test
  void testGetPipelineReports() {
    // Setup UUIDs
    UUID pipelineId = UUID.randomUUID();
    UUID pipelineExecId = UUID.randomUUID();
    UUID stageExecId = UUID.randomUUID();
    UUID stageId = UUID.randomUUID();

    // Create mock pipeline execution
    PipelineExecutionEntity exec = new PipelineExecutionEntity();
    exec.setId(pipelineExecId);
    exec.setPipelineId(pipelineId);
    exec.setRunNumber(1);
    exec.setCommitHash("abc123");
    Instant start = Instant.parse("2024-04-01T10:00:00Z");
    Instant end = Instant.parse("2024-04-01T10:05:00Z");
    exec.setStartTime(start);
    exec.setCompletionTime(end);

    // Create mock stage execution
    StageExecutionEntity stage = new StageExecutionEntity();
    stage.setId(stageExecId);
    stage.setStageId(stageId);
    stage.setPipelineExecutionId(pipelineExecId);
    stage.setStatus(ExecutionStatus.SUCCESS);
    stage.setStartTime(start);
    stage.setCompletionTime(end);

    // Mock repository behavior
    when(pipelineExecutionRepository.findByPipelineNameOrderByStartTimeDesc("DemoPipeline")).thenReturn(List.of(exec));
    when(pipelineExecutionRepository.findPipelineNameByPipelineId(pipelineId)).thenReturn(Optional.of("DemoPipeline"));
    when(stageExecutionRepository.findByPipelineExecutionId(pipelineExecId)).thenReturn(List.of(stage));
    when(stageExecutionRepository.findById(stageExecId)).thenReturn(Optional.of(stage));
    when(pipelineExecutionRepository.findById(pipelineExecId)).thenReturn(Optional.of(exec));
    when(stageExecutionRepository.findStageNameByStageId(stageId)).thenReturn(Optional.of("Build"));
    when(jobExecutionRepository.findByStageExecution(stage)).thenReturn(List.of());

    // Act
    List<PipelineReportDTO> reports = reportService.getPipelineReports("DemoPipeline");

    // Assert
    assertEquals(1, reports.size());
    PipelineReportDTO report = reports.get(0);
    assertEquals("DemoPipeline", report.getPipelineName());
    assertEquals(1, report.getRunNumber());
    assertEquals("abc123", report.getCommitHash());
    assertEquals(ExecutionStatus.SUCCESS, report.getStatus());
    assertEquals(start, report.getStartTime());
    assertEquals(end, report.getCompletionTime());

    List<StageReportDTO> stages = report.getStages();
    assertEquals(1, stages.size());
    assertEquals("Build", stages.get(0).getName());
    assertEquals(ExecutionStatus.SUCCESS, stages.get(0).getStatus());
    assertTrue(stages.get(0).getJobs().isEmpty()); // no jobs
  }


  @Test
  void testGetPipelineRunSummary_pipelineNotFound() {
    when(pipelineExecutionRepository.findPipelineIdByName("Unknown")).thenReturn(Optional.empty());
    assertThrows(IllegalArgumentException.class,
        () -> reportService.getPipelineRunSummary("Unknown", 1));
  }

  @Test
  void testGetStageReport_stageNotFound() {
    UUID pipelineId = UUID.randomUUID();
    UUID execId = UUID.randomUUID();

    PipelineExecutionEntity exec = new PipelineExecutionEntity();
    exec.setId(execId);
    exec.setPipelineId(pipelineId);
    exec.setRunNumber(1);

    when(pipelineExecutionRepository.findPipelineIdByName("Demo")).thenReturn(Optional.of(pipelineId));
    when(pipelineExecutionRepository.findByPipelineIdAndRunNumber(pipelineId, 1)).thenReturn(Optional.of(exec));
    when(stageExecutionRepository.findByPipelineExecutionIdAndStageNameOrderByStartTimeDesc(execId, "Build")).thenReturn(List.of());

    assertThrows(IllegalArgumentException.class,
        () -> reportService.getStageReport("Demo", 1, "Build"));
  }

  @Test
  void testGetJobReport_jobNotFound() {
    UUID pipelineId = UUID.randomUUID();
    UUID execId = UUID.randomUUID();
    UUID stageExecId = UUID.randomUUID();

    PipelineExecutionEntity exec = new PipelineExecutionEntity();
    exec.setId(execId);
    exec.setPipelineId(pipelineId);
    exec.setRunNumber(1);
    exec.setCommitHash("abc");

    StageExecutionEntity stage = new StageExecutionEntity();
    stage.setId(stageExecId);
    stage.setPipelineExecutionId(execId);

    when(pipelineExecutionRepository.findPipelineIdByName("Demo")).thenReturn(Optional.of(pipelineId));
    when(pipelineExecutionRepository.findByPipelineIdAndRunNumber(pipelineId, 1)).thenReturn(Optional.of(exec));
    when(stageExecutionRepository.findByPipelineExecutionIdAndStageNameOrderByStartTimeDesc(execId, "Build")).thenReturn(List.of(stage));
    when(stageExecutionRepository.findById(stageExecId)).thenReturn(Optional.of(stage));
    when(jobExecutionRepository.findByStageExecutionAndJobNameOrderByStartTimeDesc(stageExecId, "Compile")).thenReturn(List.of());

    assertThrows(IllegalArgumentException.class,
        () -> reportService.getJobReport("Demo", 1, "Build", "Compile"));
  }
}
