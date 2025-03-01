package edu.neu.cs6510.sp25.t1.backend.api;

import edu.neu.cs6510.sp25.t1.backend.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public ResponseEntity<PipelineDTO> runPipeline(@RequestBody RunPipelineRequest request) {
        Optional<PipelineDTO> dto = pipelineService.startPipeline(request.getPipeline());
        return dto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves the execution status of a pipeline.
     *
     * @param pipelineName The pipeline name.
     * @return A DTO with the pipeline execution status.
     */
    @GetMapping("/{pipelineName}/status")
    public ResponseEntity<PipelineDTO> getStatus(@PathVariable String pipelineName) {
        Optional<PipelineDTO> dto = pipelineService.getPipelineExecution(pipelineName);
        return dto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
