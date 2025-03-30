package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.service.report.ReportService;
import edu.neu.cs6510.sp25.t1.common.dto.JobReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.StageReportDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

  @Mock
  private ReportService reportService;

  private ReportController reportController;

  @BeforeEach
  void setUp() {
    reportController = new ReportController(reportService);
  }

  @Test
  void testGetAvailablePipelines() {
    // Arrange
    List<String> expectedPipelines = Arrays.asList("pipeline1", "pipeline2", "pipeline3");
    when(reportService.getAvailablePipelines()).thenReturn(expectedPipelines);

    // Act
    List<String> actualPipelines = reportController.getAvailablePipelines();

    // Assert
    assertEquals(expectedPipelines, actualPipelines);
    assertEquals(3, actualPipelines.size());
    verify(reportService, times(1)).getAvailablePipelines();
  }

  @Test
  void testGetAvailablePipelines_EmptyList() {
    // Arrange
    when(reportService.getAvailablePipelines()).thenReturn(Collections.emptyList());

    // Act
    List<String> actualPipelines = reportController.getAvailablePipelines();

    // Assert
    assertTrue(actualPipelines.isEmpty());
    verify(reportService, times(1)).getAvailablePipelines();
  }

//  @Test
//  void testGetPipelineExecutionHistory() {
//    // Arrange
//    String pipelineName = "testPipeline";
//    List<PipelineReportDTO> expectedReports = Arrays.asList(
//            mock(PipelineReportDTO.class),
//            mock(PipelineReportDTO.class)
//    );
//    when(reportService.getPipelineReports(pipelineName)).thenReturn(expectedReports);
//
//    // Act
//    List<PipelineReportDTO> actualReports = reportController.getPipelineExecutionHistory(pipelineName);
//
//    // Assert
//    assertEquals(expectedReports, actualReports);
//    assertEquals(2, actualReports.size());
//    verify(reportService, times(1)).getPipelineReports(pipelineName);
//  }
//
//  @Test
//  void testGetPipelineExecutionHistory_EmptyList() {
//    // Arrange
//    String pipelineName = "nonExistentPipeline";
//    when(reportService.getPipelineReports(pipelineName)).thenReturn(Collections.emptyList());
//
//    // Act
//    List<PipelineReportDTO> actualReports = reportController.getPipelineExecutionHistory(pipelineName);
//
//    // Assert
//    assertTrue(actualReports.isEmpty());
//    verify(reportService, times(1)).getPipelineReports(pipelineName);
//  }

  @Test
  void testGetPipelineExecutionSummary() {
    // Arrange
    String pipelineName = "testPipeline";
    int runNumber = 5;
    PipelineReportDTO expectedReport = mock(PipelineReportDTO.class);
    when(reportService.getPipelineRunSummary(pipelineName, runNumber)).thenReturn(expectedReport);

    // Act
    PipelineReportDTO actualReport = reportController.getPipelineExecutionSummary(pipelineName, runNumber);

    // Assert
    assertEquals(expectedReport, actualReport);
    verify(reportService, times(1)).getPipelineRunSummary(pipelineName, runNumber);
  }

  @Test
  void testGetStageReport() {
    // Arrange
    String pipelineName = "testPipeline";
    int runNumber = 5;
    String stageName = "testStage";
    StageReportDTO expectedReport = mock(StageReportDTO.class);
    when(reportService.getStageReport(pipelineName, runNumber, stageName)).thenReturn(expectedReport);

    // Act
    StageReportDTO actualReport = reportController.getStageReport(pipelineName, runNumber, stageName);

    // Assert
    assertEquals(expectedReport, actualReport);
    verify(reportService, times(1)).getStageReport(pipelineName, runNumber, stageName);
  }

  @Test
  void testGetJobReport() {
    // Arrange
    String pipelineName = "testPipeline";
    int runNumber = 5;
    String stageName = "testStage";
    String jobName = "testJob";
    JobReportDTO expectedReport = mock(JobReportDTO.class);
    when(reportService.getJobReport(pipelineName, runNumber, stageName, jobName)).thenReturn(expectedReport);

    // Act
    JobReportDTO actualReport = reportController.getJobReport(pipelineName, runNumber, stageName, jobName);

    // Assert
    assertEquals(expectedReport, actualReport);
    verify(reportService, times(1)).getJobReport(pipelineName, runNumber, stageName, jobName);
  }

  @Test
  void testConstructor() {
    // Act
    ReportController controller = new ReportController(reportService);

    // Assert
    assertNotNull(controller);
  }

  @Test
  void testWithNegativeRunNumber() {
    // Arrange
    String pipelineName = "testPipeline";
    int negativeRunNumber = -1;
    PipelineReportDTO expectedReport = mock(PipelineReportDTO.class);
    when(reportService.getPipelineRunSummary(pipelineName, negativeRunNumber)).thenReturn(expectedReport);

    // Act
    PipelineReportDTO actualReport = reportController.getPipelineExecutionSummary(pipelineName, negativeRunNumber);

    // Assert
    assertEquals(expectedReport, actualReport);
    verify(reportService, times(1)).getPipelineRunSummary(pipelineName, negativeRunNumber);
  }
}