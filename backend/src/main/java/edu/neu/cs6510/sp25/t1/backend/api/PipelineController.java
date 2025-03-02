package edu.neu.cs6510.sp25.t1.backend.api;

import edu.neu.cs6510.sp25.t1.backend.dto.PipelineExecutionSummary;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.common.api.UpdateExecutionStateRequest;
import edu.neu.cs6510.sp25.t1.common.runtime.PipelineRunState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for handling pipeline execution requests.
 */
@RestController
@RequestMapping("/api/v1/pipelines")
public class PipelineController {
  private final PipelineExecutionService pipelineService;

  public PipelineController(PipelineExecutionService pipelineService) {
    this.pipelineService = pipelineService;
  }

  /**
   * Starts a pipeline execution and returns a summary DTO.
   *
   * @param request The pipeline execution request.
   * @return A DTO representation of the pipeline execution summary.
   */
  @PostMapping("/run")
  public ResponseEntity<PipelineExecutionSummary> runPipeline(@RequestBody UpdateExecutionStateRequest request) {
    Optional<PipelineRunState> runState = pipelineService.startPipeline(request.getName());

    return runState
            .map(state -> ResponseEntity.ok(PipelineExecutionSummary.fromEntity(state))) // ✅ Convert to correct DTO
            .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Retrieves the execution status of a pipeline.
   *
   * @param pipelineName The pipeline name.
   * @return A DTO with the pipeline execution summary.
   */
  @GetMapping("/{pipelineName}/status")
  public ResponseEntity<PipelineExecutionSummary> getStatus(@PathVariable String pipelineName) {
    Optional<PipelineRunState> runState = pipelineService.getPipelineExecution(pipelineName);

    return runState
            .map(state -> ResponseEntity.ok(PipelineExecutionSummary.fromEntity(state))) // ✅ Convert to correct DTO
            .orElse(ResponseEntity.notFound().build());
  }
}
