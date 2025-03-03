package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.api.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineReportService;

/**
 * Controller for handling report-related requests.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {
  private final PipelineReportService reportService;

  public ReportController(PipelineReportService reportService) {
    this.reportService = reportService;
  }

  /**
   * Retrieves all pipelines with execution history.
   *
   * @return List of pipeline names.
   */
  @GetMapping("/pipelines")
  public ResponseEntity<List<String>> getAllPipelines() {
    return ResponseEntity.ok(reportService.getAllPipelines());
  }

  /**
   * Retrieves all executions for a specific pipeline.
   *
   * @param pipeline The pipeline name.
   * @return List of pipeline execution entities.
   */
  @GetMapping("/{pipeline}")
  public ResponseEntity<List<PipelineExecutionEntity>> getPipelineExecutions(@PathVariable String pipeline) {
    return ResponseEntity.ok(reportService.getPipelineExecutions(pipeline));
  }

  /**
   * Retrieves a specific pipeline execution.
   *
   * @param pipeline The pipeline name.
   * @param runId The run ID of the execution.
   * @return The pipeline execution entity.
   */
  @GetMapping("/{pipeline}/{runId}")
  public ResponseEntity<PipelineExecutionEntity> getPipelineExecution(
          @PathVariable String pipeline,
          @PathVariable String runId) {
    return ResponseEntity.ok(reportService.getPipelineExecution(pipeline, runId));
  }

  /**
   * Retrieves the latest execution of a pipeline.
   *
   * @param pipeline The pipeline name.
   * @return The latest pipeline execution DTO.
   */
  @GetMapping("/{pipeline}/latest")
  public ResponseEntity<PipelineDTO> getLatestPipelineRun(@PathVariable String pipeline) {
    return ResponseEntity.ok(reportService.getLatestPipelineRun(pipeline));
  }
}
