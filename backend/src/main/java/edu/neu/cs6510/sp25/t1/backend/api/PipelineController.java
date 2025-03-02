package edu.neu.cs6510.sp25.t1.backend.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import edu.neu.cs6510.sp25.t1.backend.dto.PipelineExecutionSummary;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;

/**
 * Controller for handling pipeline-related requests.
 */
@RestController
@RequestMapping("/api/v1/pipelines")
public class PipelineController {
  private final PipelineExecutionService pipelineService;

  public PipelineController(PipelineExecutionService pipelineService) {
    this.pipelineService = pipelineService;
  }

  /**
   * Starts a pipeline execution and returns a DTO.
   *
   * @param request The pipeline execution request.
   * @return A DTO representation of the pipeline execution.
   */
  @PostMapping("/run")
  public ResponseEntity<PipelineExecutionSummary> runPipeline(@RequestBody RunPipelineRequest request) {
    Optional<PipelineExecutionSummary> summary = pipelineService.startPipeline(request.getPipeline());
    return summary.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  /**
   * Retrieves the execution status of a pipeline.
   *
   * @param pipelineName The pipeline name.
   * @return A DTO with the pipeline execution status.
   */
  @GetMapping("/{pipelineName}/status")
  public ResponseEntity<PipelineExecutionSummary> getStatus(@PathVariable String pipelineName) {
    Optional<PipelineExecutionSummary> summary = pipelineService.getPipelineExecution(pipelineName);
    return summary.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }
}
