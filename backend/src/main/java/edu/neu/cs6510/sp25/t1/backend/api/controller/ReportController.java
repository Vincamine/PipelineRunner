package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import edu.neu.cs6510.sp25.t1.common.api.response.JobReportResponse;
import edu.neu.cs6510.sp25.t1.common.api.response.PipelineReportResponse;
import edu.neu.cs6510.sp25.t1.common.api.response.StageReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/report")
@Tag(name = "Report API", description = "Endpoints for retrieving execution reports")
public class ReportController {

  @GetMapping("/pipeline/{pipelineName}")
  @Operation(summary = "Retrieve pipeline execution history", description = "Fetches past executions of a specified pipeline.")
  public List<PipelineReportResponse> getPipelineReport(@PathVariable String pipelineName) {
    // TODO: Implement report retrieval
    return List.of(new PipelineReportResponse("12345", "SUCCESS", "abcdef"),
            new PipelineReportResponse("12346", "FAILED", "ghijkl"));
  }

  @GetMapping("/pipeline/{pipelineName}/stage/{stageName}")
  @Operation(summary = "Retrieve stage execution history", description = "Fetches past executions of a specified stage in a pipeline.")
  public StageReportResponse getStageReport(@PathVariable String pipelineName, @PathVariable String stageName) {
    // TODO: Implement stage report retrieval
    return new StageReportResponse(stageName, List.of(
            new StageReportResponse.ExecutionRecord("12345", "SUCCESS"),
            new StageReportResponse.ExecutionRecord("12346", "FAILED")
    ));
  }

  @GetMapping("/pipeline/{pipelineName}/stage/{stageName}/job/{jobName}")
  @Operation(summary = "Retrieve job execution history", description = "Fetches past executions of a specified job in a stage.")
  public JobReportResponse getJobReport(@PathVariable String pipelineName, @PathVariable String stageName, @PathVariable String jobName) {
    // TODO: Implement job report retrieval
    return new JobReportResponse(jobName, List.of(
            new JobReportResponse.ExecutionRecord("12345", "SUCCESS", "Build successful.")
    ));
  }
}
