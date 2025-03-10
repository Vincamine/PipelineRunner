package edu.neu.cs6510.sp25.t1.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.common.dto.JobReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.StageReportDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ReportServiceTest {

  @Mock
  private PipelineExecutionRepository pipelineExecutionRepository;

  @Mock
  private StageExecutionRepository stageExecutionRepository;

  @Mock
  private JobExecutionRepository jobExecutionRepository;

  @InjectMocks
  private ReportService reportService;

  private UUID testPipelineId;
  private UUID testPipelineExecutionId;
  private UUID testStageId;
  private UUID testStageExecutionId;
  private UUID testJobId;
  private UUID testJobExecutionId;
  private String testPipelineName;
  private String testStageName;
  private String testJobName;
  private int testRunNumber;
  private Instant testStartTime;
  private Instant testCompletionTime;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Initialize test data
    testPipelineId = UUID.randomUUID();
    testPipelineExecutionId = UUID.randomUUID();
    testStageId = UUID.randomUUID();
    testStageExecutionId = UUID.randomUUID();
    testJobId = UUID.randomUUID();
    testJobExecutionId = UUID.randomUUID();
    testPipelineName = "TestPipeline";
    testStageName = "TestStage";
    testJobName = "TestJob";
    testRunNumber = 42;
    testStartTime = Instant.now().minusSeconds(3600);
    testCompletionTime = Instant.now();
  }

  @Test
  void testGetAvailablePipelines() {
    // Arrange
    PipelineExecutionEntity execution1 = new PipelineExecutionEntity();
    execution1.setPipelineId(testPipelineId);

    PipelineExecutionEntity execution2 = new PipelineExecutionEntity();
    UUID anotherPipelineId = UUID.randomUUID();
    execution2.setPipelineId(anotherPipelineId);

    when(pipelineExecutionRepository.findAll()).thenReturn(Arrays.asList(execution1, execution2));
    when(pipelineExecutionRepository.findPipelineNameByPipelineId(testPipelineId))
            .thenReturn(Optional.of(testPipelineName));
    when(pipelineExecutionRepository.findPipelineNameByPipelineId(anotherPipelineId))
            .thenReturn(Optional.of("AnotherPipeline"));

    // Act
    List<String> result = reportService.getAvailablePipelines();

    // Assert
    assertEquals(2, result.size());
    assertTrue(result.contains(testPipelineName));
    assertTrue(result.contains("AnotherPipeline"));
  }

  @Test
  void testGetAvailablePipelinesWithUnknownPipeline() {
    // Arrange
    PipelineExecutionEntity execution = new PipelineExecutionEntity();
    execution.setPipelineId(testPipelineId);

    when(pipelineExecutionRepository.findAll()).thenReturn(Collections.singletonList(execution));
    when(pipelineExecutionRepository.findPipelineNameByPipelineId(testPipelineId))
            .thenReturn(Optional.empty());

    // Act
    List<String> result = reportService.getAvailablePipelines();

    // Assert
    assertEquals(1, result.size());
    assertEquals("Unknown Pipeline", result.get(0));
  }

  @Test
  void testGetPipelineReports() {
    // Arrange
    PipelineExecutionEntity execution = createTestPipelineExecution();
    StageExecutionEntity stage = createTestStageExecution();

    when(pipelineExecutionRepository.findByPipelineNameOrderByStartTimeDesc(testPipelineName))
            .thenReturn(Collections.singletonList(execution));
    when(pipelineExecutionRepository.findPipelineNameByPipelineId(testPipelineId))
            .thenReturn(Optional.of(testPipelineName));
    when(stageExecutionRepository.findByPipelineExecutionId(testPipelineExecutionId))
            .thenReturn(Collections.singletonList(stage));

    // Act
    List<PipelineReportDTO> reports = reportService.getPipelineReports(testPipelineName);

    // Assert
    assertEquals(1, reports.size());
    PipelineReportDTO report = reports.get(0);
    // Access fields using getters
    assertEquals(testPipelineExecutionId, report.getId());
    assertEquals(testPipelineName, report.getPipelineName());
    assertEquals(testRunNumber, report.getRunNumber());
    assertEquals(ExecutionStatus.SUCCESS, report.getStatus());
  }

  @Test
  void testGetPipelineRunSummary() {
    // Arrange
    PipelineExecutionEntity execution = createTestPipelineExecution();
    StageExecutionEntity stage = createTestStageExecution();
    JobExecutionEntity job = createTestJobExecution();

    when(pipelineExecutionRepository.findPipelineIdByName(testPipelineName))
            .thenReturn(Optional.of(testPipelineId));
    when(pipelineExecutionRepository.findByPipelineIdAndRunNumber(testPipelineId, testRunNumber))
            .thenReturn(Optional.of(execution));
    when(stageExecutionRepository.findByPipelineExecutionId(testPipelineExecutionId))
            .thenReturn(Collections.singletonList(stage));
    when(jobExecutionRepository.findByStageExecutionId(testStageExecutionId))
            .thenReturn(Collections.singletonList(job));
    when(stageExecutionRepository.findStageNameByStageId(testStageId))
            .thenReturn(Optional.of(testStageName));
    when(jobExecutionRepository.findJobNameByJobId(testJobId))
            .thenReturn(Optional.of(testJobName));

    // Act
    PipelineReportDTO report = reportService.getPipelineRunSummary(testPipelineName, testRunNumber);

    // Assert
    assertNotNull(report);
    assertEquals(testPipelineExecutionId, report.getId());
    assertEquals(testPipelineName, report.getPipelineName());
    assertEquals(testRunNumber, report.getRunNumber());
    assertEquals(ExecutionStatus.SUCCESS, report.getStatus());
    assertNotNull(report.getStageReports());
    assertEquals(1, report.getStageReports().size());
  }

  @Test
  void testGetPipelineRunSummary_PipelineNotFound() {
    // Arrange
    when(pipelineExecutionRepository.findPipelineIdByName(testPipelineName))
            .thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      reportService.getPipelineRunSummary(testPipelineName, testRunNumber);
    });

    assertTrue(exception.getMessage().contains("Pipeline not found"));
  }

  @Test
  void testGetPipelineRunSummary_ExecutionNotFound() {
    // Arrange
    when(pipelineExecutionRepository.findPipelineIdByName(testPipelineName))
            .thenReturn(Optional.of(testPipelineId));
    when(pipelineExecutionRepository.findByPipelineIdAndRunNumber(testPipelineId, testRunNumber))
            .thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      reportService.getPipelineRunSummary(testPipelineName, testRunNumber);
    });

    assertTrue(exception.getMessage().contains("Pipeline execution not found"));
  }

  @Test
  void testGetStageReport() {
    // Arrange
    PipelineExecutionEntity execution = createTestPipelineExecution();
    StageExecutionEntity stage = createTestStageExecution();
    JobExecutionEntity job = createTestJobExecution();

    when(pipelineExecutionRepository.findPipelineIdByName(testPipelineName))
            .thenReturn(Optional.of(testPipelineId));
    when(pipelineExecutionRepository.findByPipelineIdAndRunNumber(testPipelineId, testRunNumber))
            .thenReturn(Optional.of(execution));
    when(stageExecutionRepository.findByPipelineExecutionIdAndStageNameOrderByStartTimeDesc(testPipelineExecutionId, testStageName))
            .thenReturn(Collections.singletonList(stage));
    when(jobExecutionRepository.findByStageExecutionId(testStageExecutionId))
            .thenReturn(Collections.singletonList(job));
    when(stageExecutionRepository.findStageNameByStageId(testStageId))
            .thenReturn(Optional.of(testStageName));
    when(jobExecutionRepository.findJobNameByJobId(testJobId))
            .thenReturn(Optional.of(testJobName));

    // Act
    StageReportDTO report = reportService.getStageReport(testPipelineName, testRunNumber, testStageName);

    // Assert
    assertNotNull(report);
    assertEquals(testStageExecutionId, report.getStageId());
    assertEquals(testStageName, report.getStageName());
    assertEquals(ExecutionStatus.SUCCESS, report.getStatus());
    assertNotNull(report.getJobReports());
    assertEquals(1, report.getJobReports().size());
  }

  @Test
  void testGetStageReport_StageNotFound() {
    // Arrange
    PipelineExecutionEntity execution = createTestPipelineExecution();

    when(pipelineExecutionRepository.findPipelineIdByName(testPipelineName))
            .thenReturn(Optional.of(testPipelineId));
    when(pipelineExecutionRepository.findByPipelineIdAndRunNumber(testPipelineId, testRunNumber))
            .thenReturn(Optional.of(execution));
    when(stageExecutionRepository.findByPipelineExecutionIdAndStageNameOrderByStartTimeDesc(testPipelineExecutionId, testStageName))
            .thenReturn(Collections.emptyList());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      reportService.getStageReport(testPipelineName, testRunNumber, testStageName);
    });

    assertTrue(exception.getMessage().contains("No executions found for stage"));
  }

  @Test
  void testGetJobReport() {
    // Arrange
    PipelineExecutionEntity execution = createTestPipelineExecution();
    StageExecutionEntity stage = createTestStageExecution();
    JobExecutionEntity job = createTestJobExecution();

    when(pipelineExecutionRepository.findPipelineIdByName(testPipelineName))
            .thenReturn(Optional.of(testPipelineId));
    when(pipelineExecutionRepository.findByPipelineIdAndRunNumber(testPipelineId, testRunNumber))
            .thenReturn(Optional.of(execution));
    when(stageExecutionRepository.findByPipelineExecutionIdAndStageNameOrderByStartTimeDesc(testPipelineExecutionId, testStageName))
            .thenReturn(Collections.singletonList(stage));
    when(jobExecutionRepository.findByStageExecutionIdAndJobNameOrderByStartTimeDesc(testStageExecutionId, testJobName))
            .thenReturn(Collections.singletonList(job));
    when(jobExecutionRepository.findJobNameByJobId(testJobId))
            .thenReturn(Optional.of(testJobName));

    // Act
    JobReportDTO report = reportService.getJobReport(testPipelineName, testRunNumber, testStageName, testJobName);

    // Assert
    assertNotNull(report);
    assertEquals(testJobName, report.getJobName());
    assertNotNull(report.getExecutionRecords());
    assertEquals(1, report.getExecutionRecords().size());
    assertEquals(testJobExecutionId, report.getExecutionRecords().get(0).getId());
  }

  @Test
  void testGetJobReport_JobNotFound() {
    // Arrange
    PipelineExecutionEntity execution = createTestPipelineExecution();
    StageExecutionEntity stage = createTestStageExecution();

    when(pipelineExecutionRepository.findPipelineIdByName(testPipelineName))
            .thenReturn(Optional.of(testPipelineId));
    when(pipelineExecutionRepository.findByPipelineIdAndRunNumber(testPipelineId, testRunNumber))
            .thenReturn(Optional.of(execution));
    when(stageExecutionRepository.findByPipelineExecutionIdAndStageNameOrderByStartTimeDesc(testPipelineExecutionId, testStageName))
            .thenReturn(Collections.singletonList(stage));
    when(jobExecutionRepository.findByStageExecutionIdAndJobNameOrderByStartTimeDesc(testStageExecutionId, testJobName))
            .thenReturn(Collections.emptyList());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      reportService.getJobReport(testPipelineName, testRunNumber, testStageName, testJobName);
    });

    assertTrue(exception.getMessage().contains("Job execution not found"));
  }

  @Test
  void testCalculatePipelineStatus_Success() {
    // Arrange
    StageExecutionEntity stage = createTestStageExecution();
    stage.setStatus(ExecutionStatus.SUCCESS);

    when(stageExecutionRepository.findByPipelineExecutionId(testPipelineExecutionId))
            .thenReturn(Collections.singletonList(stage));

    // Act
    PipelineExecutionEntity execution = createTestPipelineExecution();
    when(pipelineExecutionRepository.findByPipelineNameOrderByStartTimeDesc(testPipelineName))
            .thenReturn(Collections.singletonList(execution));
    when(pipelineExecutionRepository.findPipelineNameByPipelineId(testPipelineId))
            .thenReturn(Optional.of(testPipelineName));

    List<PipelineReportDTO> reports = reportService.getPipelineReports(testPipelineName);

    // Assert
    assertEquals(ExecutionStatus.SUCCESS, reports.get(0).getStatus());
  }

  @Test
  void testCalculatePipelineStatus_Failed() {
    // Arrange
    StageExecutionEntity successStage = createTestStageExecution();
    successStage.setStatus(ExecutionStatus.SUCCESS);

    StageExecutionEntity failedStage = createTestStageExecution();
    failedStage.setId(UUID.randomUUID());
    failedStage.setStatus(ExecutionStatus.FAILED);

    when(stageExecutionRepository.findByPipelineExecutionId(testPipelineExecutionId))
            .thenReturn(Arrays.asList(successStage, failedStage));

    // Act
    PipelineExecutionEntity execution = createTestPipelineExecution();
    when(pipelineExecutionRepository.findByPipelineNameOrderByStartTimeDesc(testPipelineName))
            .thenReturn(Collections.singletonList(execution));
    when(pipelineExecutionRepository.findPipelineNameByPipelineId(testPipelineId))
            .thenReturn(Optional.of(testPipelineName));

    List<PipelineReportDTO> reports = reportService.getPipelineReports(testPipelineName);

    // Assert
    assertEquals(ExecutionStatus.FAILED, reports.get(0).getStatus());
  }

  @Test
  void testCalculatePipelineStatus_Canceled() {
    // Arrange
    StageExecutionEntity successStage = createTestStageExecution();
    successStage.setStatus(ExecutionStatus.SUCCESS);

    StageExecutionEntity canceledStage = createTestStageExecution();
    canceledStage.setId(UUID.randomUUID());
    canceledStage.setStatus(ExecutionStatus.CANCELED);

    when(stageExecutionRepository.findByPipelineExecutionId(testPipelineExecutionId))
            .thenReturn(Arrays.asList(successStage, canceledStage));

    // Act
    PipelineExecutionEntity execution = createTestPipelineExecution();
    when(pipelineExecutionRepository.findByPipelineNameOrderByStartTimeDesc(testPipelineName))
            .thenReturn(Collections.singletonList(execution));
    when(pipelineExecutionRepository.findPipelineNameByPipelineId(testPipelineId))
            .thenReturn(Optional.of(testPipelineName));

    List<PipelineReportDTO> reports = reportService.getPipelineReports(testPipelineName);

    // Assert
    assertEquals(ExecutionStatus.CANCELED, reports.get(0).getStatus());
  }

  private PipelineExecutionEntity createTestPipelineExecution() {
    PipelineExecutionEntity execution = new PipelineExecutionEntity();
    execution.setId(testPipelineExecutionId);
    execution.setPipelineId(testPipelineId);
    execution.setRunNumber(testRunNumber);
    execution.setCommitHash("abc123");
    execution.setLocal(true);
    execution.setStatus(ExecutionStatus.SUCCESS);
    execution.setStartTime(testStartTime);
    execution.setCompletionTime(testCompletionTime);
    return execution;
  }

  private StageExecutionEntity createTestStageExecution() {
    StageExecutionEntity stage = new StageExecutionEntity();
    stage.setId(testStageExecutionId);
    stage.setStageId(testStageId);
    stage.setPipelineExecutionId(testPipelineExecutionId);
    stage.setStatus(ExecutionStatus.SUCCESS);
    stage.setStartTime(testStartTime);
    stage.setCompletionTime(testCompletionTime);
    return stage;
  }

  private JobExecutionEntity createTestJobExecution() {
    JobExecutionEntity job = new JobExecutionEntity();
    job.setId(testJobExecutionId);
    job.setJobId(testJobId);
    job.setStageExecutionId(testStageExecutionId);
    job.setStatus(ExecutionStatus.SUCCESS);
    job.setStartTime(testStartTime);
    job.setCompletionTime(testCompletionTime);
    job.setAllowFailure(false);
    return job;
  }
}