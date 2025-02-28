package edu.neu.cs6510.sp25.t1.api;

import edu.neu.cs6510.sp25.t1.service.RunPipelineService;
import edu.neu.cs6510.sp25.t1.model.PipelineStatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pipelines")
public class PipelineController {

    private final RunPipelineService runPipelineService;

    public PipelineController(RunPipelineService runPipelineService) {
        this.runPipelineService = runPipelineService;
    }

    @PostMapping("/{repoId}/{pipelineId}/run")
    public ResponseEntity<String> runPipeline(@PathVariable String repoId, @PathVariable String pipelineId) {
        UUID pipelineRunId = runPipelineService.startPipelineExecution(repoId, pipelineId);
        return ResponseEntity.ok("Pipeline execution started with ID: " + pipelineRunId);
    }

    @GetMapping("/{repoId}/{pipelineId}/status")
    public ResponseEntity<PipelineStatusResponse> getPipelineStatus(@PathVariable String repoId, @PathVariable String pipelineId) {
        PipelineStatusResponse statusResponse = runPipelineService.getPipelineStatus(repoId, pipelineId);
        return ResponseEntity.ok(statusResponse);
    }
}
