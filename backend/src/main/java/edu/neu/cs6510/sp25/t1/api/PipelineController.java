package edu.neu.cs6510.sp25.t1.api;

import edu.neu.cs6510.sp25.t1.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.model.execution.PipelineExecution;
import edu.neu.cs6510.sp25.t1.model.PipelineState;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pipelines")
public class PipelineController {
    private final PipelineExecutionService pipelineService;

    public PipelineController(PipelineExecutionService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @PostMapping("/run")
    public ResponseEntity<PipelineExecution> runPipeline(@RequestParam String pipelineId) {
        PipelineExecution execution = pipelineService.startPipeline(pipelineId, "build");
        return ResponseEntity.ok(execution);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> getStatus(@PathVariable String id) {
        PipelineExecution execution = pipelineService.getPipelineExecution(id);
        if (execution == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("id", execution.getPipelineId(), "status", execution.getState().name()));
    }
}
