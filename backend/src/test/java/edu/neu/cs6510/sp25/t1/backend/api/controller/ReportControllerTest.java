package edu.neu.cs6510.sp25.t1.backend.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.neu.cs6510.sp25.t1.backend.service.ReportService;
import edu.neu.cs6510.sp25.t1.common.dto.JobReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.StageReportDTO;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

  @Mock
  private ReportService reportService;

  @InjectMocks
  private ReportController reportController;

  private static final String PIPELINE_NAME = "test-pipeline";
  private static final String STAGE_NAME = "test-stage";
  private static final String JOB_NAME = "test-job";
  private UUID pipelineExecutionId;
  private UUID stageExecutionId;

  @BeforeEach
  public void setup() {
    pipelineExecutionId = UUID.randomUUID();
    stageExecutionId = UUID.randomUUID();
  }

  @Test
  public void testGetPipelineReport_WithResults() {
    // Arrange
    List<PipelineReportDTO> expectedReports = new ArrayList<>();
    expectedReports.add(mock(PipelineReportDTO.class));
    expectedReports.add(mock(PipelineReportDTO.class));

    when(reportService.getPipelineReports(PIPELINE_NAME)).thenReturn(expectedReports);

    // Act
    List<PipelineReportDTO> actualReports = reportController.getPipelineReport(PIPELINE_NAME);

    // Assert
    assertEquals(expectedReports.size(), actualReports.size());
    assertEquals(expectedReports, actualReports);
    verify(reportService, times(1)).getPipelineReports(PIPELINE_NAME);
  }

  @Test
  public void testGetPipelineReport_EmptyList() {
    // Arrange
    List<PipelineReportDTO> emptyList = Collections.emptyList();
    when(reportService.getPipelineReports(PIPELINE_NAME)).thenReturn(emptyList);

    // Act
    List<PipelineReportDTO> actualReports = reportController.getPipelineReport(PIPELINE_NAME);

    // Assert
    assertNotNull(actualReports);
    assertEquals(0, actualReports.size());
    verify(reportService, times(1)).getPipelineReports(PIPELINE_NAME);
  }

  @Test
  public void testGetPipelineReport_NullPipelineName() {
    // Arrange
    when(reportService.getPipelineReports(null)).thenThrow(new IllegalArgumentException("Pipeline name cannot be null"));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      reportController.getPipelineReport(null);
    });
    verify(reportService, times(1)).getPipelineReports(null);
  }

  @Test
  public void testGetStageReport_Success() {
    // Arrange
    StageReportDTO expectedReport = mock(StageReportDTO.class);
    when(reportService.getStageReport(pipelineExecutionId, STAGE_NAME)).thenReturn(expectedReport);

    // Act
    StageReportDTO actualReport = reportController.getStageReport(pipelineExecutionId, STAGE_NAME);

    // Assert
    assertEquals(expectedReport, actualReport);
    verify(reportService, times(1)).getStageReport(pipelineExecutionId, STAGE_NAME);
  }

  @Test
  public void testGetStageReport_NullResult() {
    // Arrange
    when(reportService.getStageReport(pipelineExecutionId, STAGE_NAME)).thenReturn(null);

    // Act
    StageReportDTO actualReport = reportController.getStageReport(pipelineExecutionId, STAGE_NAME);

    // Assert
    assertEquals(null, actualReport);
    verify(reportService, times(1)).getStageReport(pipelineExecutionId, STAGE_NAME);
  }

  @Test
  public void testGetStageReport_InvalidId() {
    // Arrange
    UUID invalidId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    when(reportService.getStageReport(invalidId, STAGE_NAME))
            .thenThrow(new IllegalArgumentException("Invalid pipeline execution ID"));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      reportController.getStageReport(invalidId, STAGE_NAME);
    });
    verify(reportService, times(1)).getStageReport(invalidId, STAGE_NAME);
  }

  @Test
  public void testGetJobReport_Success() {
    // Arrange
    JobReportDTO expectedReport = mock(JobReportDTO.class);
    when(reportService.getJobReport(stageExecutionId, JOB_NAME)).thenReturn(expectedReport);

    // Act
    JobReportDTO actualReport = reportController.getJobReport(stageExecutionId, JOB_NAME);

    // Assert
    assertEquals(expectedReport, actualReport);
    verify(reportService, times(1)).getJobReport(stageExecutionId, JOB_NAME);
  }

  @Test
  public void testGetJobReport_NullResult() {
    // Arrange
    when(reportService.getJobReport(stageExecutionId, JOB_NAME)).thenReturn(null);

    // Act
    JobReportDTO actualReport = reportController.getJobReport(stageExecutionId, JOB_NAME);

    // Assert
    assertEquals(null, actualReport);
    verify(reportService, times(1)).getJobReport(stageExecutionId, JOB_NAME);
  }

  @Test
  public void testGetJobReport_InvalidInput() {
    // Arrange
    when(reportService.getJobReport(stageExecutionId, null))
            .thenThrow(new IllegalArgumentException("Job name cannot be null"));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      reportController.getJobReport(stageExecutionId, null);
    });
    verify(reportService, times(1)).getJobReport(stageExecutionId, null);
  }
}