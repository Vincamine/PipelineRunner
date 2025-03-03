package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.data.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.dto.StageExecutionDTO;
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

  // Get all pipelines
  @GetMapping("/pipelines")
  public ResponseEntity<List<PipelineExecutionDTO>> getAllPipelines() {
    return ResponseEntity.ok(reportService.getRecentExecutions(10));  // Adjust the number for the limit
  }

  // Get all executions for a specific pipeline
  @GetMapping("/{pipeline}/executions")
  public ResponseEntity<List<PipelineExecutionDTO>> getPipelineExecutions(@PathVariable String pipeline) {
    return ResponseEntity.ok(reportService.getPipelineExecutionHistory(pipeline, 10)); // Adjust limit if needed
  }

  // Get a specific pipeline execution by run ID
  @GetMapping("/{pipeline}/executions/{runId}")
  public ResponseEntity<PipelineExecutionDTO> getPipelineExecution(
          @PathVariable String pipeline,
          @PathVariable String runId) {
    return ResponseEntity.ok(reportService.getPipelineExecution(pipeline, runId));
  }

  // Get the latest execution of a pipeline
  @GetMapping("/{pipeline}/executions/latest")
  public ResponseEntity<PipelineExecutionDTO> getLatestPipelineRun(@PathVariable String pipeline) {
    return ResponseEntity.ok(reportService.getLatestPipelineRun(pipeline));
  }

  // Get the summary of a stage from a pipeline run
  @GetMapping("/{pipeline}/executions/{runId}/stage/{stage}")
  public ResponseEntity<StageExecutionDTO> getStageSummary(
          @PathVariable String pipeline,
          @PathVariable String runId,
          @PathVariable String stage) {
    return ResponseEntity.ok(reportService.getStageSummary(pipeline, runId, stage));
  }

  // Get the summary of a job from a pipeline run
  @GetMapping("/{pipeline}/executions/{runId}/stage/{stage}/job/{job}")
  public ResponseEntity<JobExecutionDTO> getJobSummary(
          @PathVariable String pipeline,
          @PathVariable String runId,
          @PathVariable String stage,
          @PathVariable String job) {
    return ResponseEntity.ok(reportService.getJobSummary(pipeline, runId, stage, job));
  }
}
