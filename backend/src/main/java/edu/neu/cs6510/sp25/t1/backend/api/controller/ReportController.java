package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  /**
   * Retrieve recent pipeline executions with an optional limit.
   *
   * @param limit Optional limit (default: 10).
   * @return List of recent pipeline executions.
   */
  @GetMapping("/pipelines")
  public ResponseEntity<List<PipelineExecutionDTO>> getAllPipelines(@RequestParam(defaultValue = "10") int limit) {
    return ResponseEntity.ok(reportService.getRecentExecutions(limit));
  }

  /**
   * Retrieve all executions for a specific pipeline.
   *
   * @param pipeline The pipeline name.
   * @param limit    Optional limit (default: 10).
   * @return List of pipeline executions.
   */
  @GetMapping("/{pipeline}/executions")
  public ResponseEntity<List<PipelineExecutionDTO>> getPipelineExecutions(
          @PathVariable String pipeline,
          @RequestParam(defaultValue = "10") int limit) {
    return ResponseEntity.ok(reportService.getPipelineExecutionHistory(pipeline, limit));
  }

  /**
   * Get details of a specific pipeline execution.
   *
   * @param pipeline The pipeline name.
   * @param runId    The run ID.
   * @return Pipeline execution details.
   */
  @GetMapping("/{pipeline}/executions/{runId}")
  public ResponseEntity<PipelineExecutionDTO> getPipelineExecution(
          @PathVariable String pipeline,
          @PathVariable String runId) {
    PipelineExecutionDTO execution = reportService.getPipelineExecution(pipeline, runId);
    return execution != null ? ResponseEntity.ok(execution) : ResponseEntity.notFound().build();
  }

  /**
   * Get the latest execution of a pipeline.
   *
   * @param pipeline The pipeline name.
   * @return Latest pipeline execution details.
   */
  @GetMapping("/{pipeline}/executions/latest")
  public ResponseEntity<PipelineExecutionDTO> getLatestPipelineRun(@PathVariable String pipeline) {
    PipelineExecutionDTO latestExecution = reportService.getLatestPipelineRun(pipeline);
    return latestExecution != null ? ResponseEntity.ok(latestExecution) : ResponseEntity.notFound().build();
  }

  /**
   * Retrieve a summary of a specific stage in a pipeline execution.
   *
   * @param pipeline The pipeline name.
   * @param runId    The run ID.
   * @param stage    The stage name.
   * @return Stage execution details.
   */
  @GetMapping("/{pipeline}/executions/{runId}/stage/{stage}")
  public ResponseEntity<StageExecutionDTO> getStageSummary(
          @PathVariable String pipeline,
          @PathVariable String runId,
          @PathVariable String stage) {
    StageExecutionDTO stageSummary = reportService.getStageSummary(pipeline, runId, stage);
    return stageSummary != null ? ResponseEntity.ok(stageSummary) : ResponseEntity.notFound().build();
  }

  /**
   * Retrieve a summary of a specific job execution.
   *
   * @param pipeline The pipeline name.
   * @param runId    The run ID.
   * @param stage    The stage name.
   * @param job      The job name.
   * @return Job execution details.
   */
  @GetMapping("/{pipeline}/executions/{runId}/stage/{stage}/job/{job}")
  public ResponseEntity<JobExecutionDTO> getJobSummary(
          @PathVariable String pipeline,
          @PathVariable String runId,
          @PathVariable String stage,
          @PathVariable String job) {
    JobExecutionDTO jobSummary = reportService.getJobSummary(pipeline, runId, stage, job);
    return jobSummary != null ? ResponseEntity.ok(jobSummary) : ResponseEntity.notFound().build();
  }

  @GetMapping("/{pipeline}/executions/{runId}/logs")
  public ResponseEntity<String> getExecutionLogs(
          @PathVariable String pipeline, @PathVariable String runId) {
    List<String> logs = reportService.getPipelineLogs(pipeline, runId);
    return logs.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(String.join("\n", logs));
  }
}
