package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.api.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineReportService;

/**
 * Controller for handling report-related requests.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {
  private final PipelineReportService reportService;

  /**
   * Constructor for the ReportController.
   *
   * @param reportService The report service.
   */
  public ReportController(PipelineReportService reportService) {
    this.reportService = reportService;
  }

  /**
   * Retrieves all pipelines.
   *
   * @return A list of pipeline DTOs.
   */
  @GetMapping("/pipelines")
  public List<PipelineDTO> getAllPipelines() {
    return reportService.getAllPipelines();
  }

  /**
   * Retrieves executions of a specific pipeline.
   *
   * @param pipeline The pipeline name.
   * @return A list of pipeline executions converted to DTOs.
   */
  @GetMapping("/{pipeline}")
  public List<PipelineDTO> getPipelineExecutions(@PathVariable String pipeline) {
    return reportService.getPipelineExecutions(pipeline);
  }

  /**
   * Retrieves the latest execution of a pipeline.
   *
   * @param pipeline The pipeline name.
   * @return The latest pipeline execution converted to a DTO.
   */
  @GetMapping("/{pipeline}/latest")
  public PipelineDTO getLatestPipelineRun(@PathVariable String pipeline) {
    return reportService.getLatestPipelineRun(pipeline);
  }
}
