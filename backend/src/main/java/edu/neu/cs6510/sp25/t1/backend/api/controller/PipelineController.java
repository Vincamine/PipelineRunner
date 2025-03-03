package edu.neu.cs6510.sp25.t1.backend.api.controller;

import edu.neu.cs6510.sp25.t1.backend.api.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionTrackingService;
import edu.neu.cs6510.sp25.t1.common.api.response.UpdateExecutionStateRequest;
import edu.neu.cs6510.sp25.t1.worker.execution.PipelineExecution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for handling pipeline execution requests.
 */
@RestController
@RequestMapping("/api/v1/pipelines")
public class PipelineController {
  private final PipelineExecutionTrackingService pipelineService;

  public PipelineController(PipelineExecutionTrackingService pipelineService) {
    this.pipelineService = pipelineService;
  }

  /**
   * Starts a pipeline execution and returns a summary DTO.
   *
   * @param request The pipeline execution request.
   * @return A DTO representation of the pipeline execution summary.
   */
  @PostMapping("/run")
  public ResponseEntity<PipelineExecutionDTO> runPipeline(@RequestBody UpdateExecutionStateRequest request) {
    Optional<PipelineExecution> runState = pipelineService.startPipeline(request.getName());

    return runState
            .map(state -> ResponseEntity.ok(PipelineExecutionDTO.fromEntity(state))) // ✅ Convert to correct DTO
            .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Retrieves the execution status of a pipeline.
   *
   * @param pipelineName The pipeline name.
   * @return A DTO with the pipeline execution summary.
   */
  @GetMapping("/{pipelineName}/status")
  public ResponseEntity<PipelineExecutionDTO> getStatus(@PathVariable String pipelineName) {
    Optional<PipelineExecution> runState = pipelineService.getPipelineExecution(pipelineName);

    return runState
            .map(state -> ResponseEntity.ok(PipelineExecutionDTO.fromEntity(state))) // ✅ Convert to correct DTO
            .orElse(ResponseEntity.notFound().build());
  }
}
