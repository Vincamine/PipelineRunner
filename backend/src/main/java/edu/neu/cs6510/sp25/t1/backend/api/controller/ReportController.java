package edu.neu.cs6510.sp25.t1.backend.api.controller;

import edu.neu.cs6510.sp25.t1.backend.service.ReportService;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.StageReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.JobReportDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/report")
@Tag(name = "Report API", description = "Endpoints for retrieving execution reports")
public class ReportController {

  private final ReportService reportService;

  public ReportController(ReportService reportService) {
    this.reportService = reportService;
  }

  @GetMapping("/pipeline/{pipelineName}")
  @Operation(summary = "Retrieve pipeline execution history", description = "Fetches past executions of a specified pipeline.")
  public List<PipelineReportDTO> getPipelineReport(@PathVariable String pipelineName) {
    return reportService.getPipelineReports(pipelineName);
  }

  @GetMapping("/pipeline/{pipelineExecutionId}/stage/{stageName}")
  @Operation(summary = "Retrieve stage execution history", description = "Fetches past executions of a specified stage in a pipeline.")
  public StageReportDTO getStageReport(@PathVariable UUID pipelineExecutionId, @PathVariable String stageName) {
    return reportService.getStageReport(pipelineExecutionId, stageName);
  }

  @GetMapping("/stage/{stageExecutionId}/job/{jobName}")
  @Operation(summary = "Retrieve job execution history", description = "Fetches past executions of a specified job in a stage.")
  public JobReportDTO getJobReport(@PathVariable UUID stageExecutionId, @PathVariable String jobName) {
    return reportService.getJobReport(stageExecutionId, jobName);
  }
}
