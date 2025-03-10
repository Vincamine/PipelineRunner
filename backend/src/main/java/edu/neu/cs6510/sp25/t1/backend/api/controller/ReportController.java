package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.service.ReportService;
import edu.neu.cs6510.sp25.t1.common.dto.JobReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.StageReportDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller class for handling report related endpoints.
 */
@RestController
@RequestMapping("/api/report")
@Tag(name = "Report API", description = "Endpoints for retrieving execution reports")
public class ReportController {

  private final ReportService reportService;

  /**
   * Constructor for ReportController.
   *
   * @param reportService ReportService instance
   */
  public ReportController(ReportService reportService) {
    this.reportService = reportService;
  }

  /**
   * Fetch available pipelines.
   *
   * @return List of pipeline names
   */
  @GetMapping("/pipelines")
  @Operation(summary = "Retrieve available pipelines", description = "Returns a list of pipeline names for which execution reports are available.")
  public List<String> getAvailablePipelines() {
    return reportService.getAvailablePipelines();
  }

  /**
   * Fetch pipeline execution history.
   *
   * @param pipelineName Name of the pipeline
   * @return List of pipeline execution reports
   */
  @GetMapping("/pipeline/history/{pipelineName}")
  @Operation(summary = "Retrieve pipeline execution history", description = "Fetches past executions of a specified pipeline.")
  public List<PipelineReportDTO> getPipelineExecutionHistory(@PathVariable String pipelineName) {
    return reportService.getPipelineReports(pipelineName);
  }

  /**
   * Fetch detailed execution summary for a pipeline run.
   *
   * @param pipelineName Name of the pipeline
   * @param runNumber    Run number of the pipeline
   * @return summary of pipeline execution
   */
  @GetMapping("/pipeline/{pipelineName}/run/{runNumber}")
  @Operation(summary = "Retrieve detailed pipeline execution report", description = "Fetches detailed execution report of a pipeline run including all stages and jobs.")
  public PipelineReportDTO getPipelineExecutionSummary(@PathVariable String pipelineName, @PathVariable int runNumber) {
    return reportService.getPipelineRunSummary(pipelineName, runNumber);
  }


  /**
   * Fetch stage execution history.
   *
   * @param pipelineName Name of the pipeline
   * @param runNumber    Run number of the pipeline
   * @param stageName    Name of the stage
   * @return summary of stage execution
   */
  @GetMapping("/pipeline/{pipelineName}/run/{runNumber}/stage/{stageName}")
  @Operation(summary = "Retrieve stage execution history", description = "Fetches execution summary of a specified stage in a pipeline execution.")
  public StageReportDTO getStageReport(@PathVariable String pipelineName, @PathVariable int runNumber, @PathVariable String stageName) {
    return reportService.getStageReport(pipelineName, runNumber, stageName);
  }


  /**
   * Fetch the report for a job execution.
   *
   * @param pipelineName Name of the pipeline
   * @param runNumber    Run number of the pipeline
   * @param stageName    Name of the stage
   * @param jobName      Name of the job
   * @return summary of job execution
   */
  @GetMapping("/pipeline/{pipelineName}/run/{runNumber}/stage/{stageName}/job/{jobName}")
  @Operation(summary = "Retrieve job execution history", description = "Fetches execution summary of a specified job in a stage execution.")
  public JobReportDTO getJobReport(@PathVariable String pipelineName, @PathVariable int runNumber, @PathVariable String stageName, @PathVariable String jobName) {
    return reportService.getJobReport(pipelineName, runNumber, stageName, jobName);
  }
}
