package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.neu.cs6510.sp25.t1.backend.api.client.WorkerClient;
import edu.neu.cs6510.sp25.t1.backend.data.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.data.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.backend.service.PipelineService;

/**
 * Controller for managing CI/CD pipeline executions.
 */
@RestController
@RequestMapping("/api/v1/pipelines")
public class PipelineController {
  private static final Logger logger = LoggerFactory.getLogger(PipelineController.class);
  private final PipelineService pipelineService;
  private final PipelineExecutionService pipelineExecutionService;
  private final WorkerClient workerClient;

  public PipelineController(PipelineService pipelineService,
                            PipelineExecutionService pipelineExecutionService,
                            WorkerClient workerClient) {
    this.pipelineService = pipelineService;
    this.pipelineExecutionService = pipelineExecutionService;
    this.workerClient = workerClient;
  }

  /**
   * Fetch all registered pipelines.
   *
   * @return List of pipelines.
   */
  @GetMapping
  public ResponseEntity<List<PipelineDTO>> getAllPipelines() {
    List<PipelineDTO> pipelines = pipelineService.getAllPipelines();
    return ResponseEntity.ok(pipelines);
  }

  /**
   * Fetch executions for a specific pipeline.
   *
   * @param pipelineName Name of the pipeline.
   * @return List of pipeline executions.
   */
  @GetMapping("/{pipelineName}/executions")
  public ResponseEntity<List<PipelineExecutionEntity>> getPipelineExecutions(@PathVariable String pipelineName) {
    List<PipelineExecutionEntity> executions = pipelineExecutionService.getPipelineExecutions(pipelineName);
    return ResponseEntity.ok(executions);
  }

  /**
   * Fetch a specific pipeline execution by run ID.
   *
   * @param pipelineName Name of the pipeline.
   * @param runId        Run ID.
   * @return Pipeline execution entity.
   */
  @GetMapping("/{pipelineName}/executions/{runId}")
  public ResponseEntity<PipelineExecutionEntity> getPipelineExecution(
          @PathVariable String pipelineName,
          @PathVariable String runId) {
    PipelineExecutionEntity execution = pipelineExecutionService.getPipelineExecution(pipelineName, runId);
    return ResponseEntity.ok(execution);
  }

  /**
   * Fetch the latest execution of a pipeline.
   *
   * @param pipelineName Name of the pipeline.
   * @return Latest pipeline execution entity.
   */
  @GetMapping("/{pipelineName}/executions/latest")
  public ResponseEntity<PipelineExecutionEntity> getLatestPipelineExecution(@PathVariable String pipelineName) {
    PipelineExecutionEntity latestExecution = pipelineExecutionService.getLatestPipelineExecution(pipelineName);
    return ResponseEntity.ok(latestExecution);
  }

  /**
   * Start a new pipeline execution.
   *
   * @param request Request containing pipeline execution details.
   * @return Response indicating pipeline execution started.
   */
  @PostMapping("/execute")
  public ResponseEntity<String> executePipeline(@RequestBody RunPipelineRequest request) {
    logger.info("Starting pipeline execution: {}", request.getPipeline());

    // Ensure commit hash is passed correctly
    String commitHash = (request.getCommit() != null) ? request.getCommit() : "default-commit";

    PipelineExecutionDTO pipelineExecution = pipelineExecutionService.startPipelineExecution(
            request.getPipeline(), commitHash
    );

    return ResponseEntity.ok("Pipeline execution started successfully.");
  }


  /**
   * Represents a request to run a pipeline.
   * This class is used for sending pipeline execution requests to the backend.
   */
  public static class RunPipelineRequest {
    private String repo;
    private String branch;
    private String commit;
    private String pipeline;
    private boolean local;
    private Map<String, String> overrides;
    private String configPath;

    public RunPipelineRequest() {
      this.repo = "";
      this.branch = "";
      this.commit = "";
      this.pipeline = "";
      this.local = false;
      this.overrides = Map.of();
      this.configPath = "";
    }

    public RunPipelineRequest(String pipeline) {
      if (pipeline == null || pipeline.isBlank()) {
        throw new IllegalArgumentException("Pipeline name cannot be null or empty.");
      }
      this.repo = "";
      this.branch = "";
      this.commit = "";
      this.pipeline = pipeline;
      this.local = false;
      this.overrides = Map.of();
      this.configPath = "";
    }

    public RunPipelineRequest(String repo, String branch, String commit, String pipeline, boolean local, Map<String, String> overrides, String configPath) {
      if (pipeline == null || pipeline.isBlank()) {
        throw new IllegalArgumentException("Pipeline name cannot be null or empty.");
      }
      this.repo = Objects.requireNonNullElse(repo, "");
      this.branch = Objects.requireNonNullElse(branch, "");
      this.commit = Objects.requireNonNullElse(commit, "");
      this.pipeline = pipeline;
      this.local = local;
      this.overrides = Objects.requireNonNullElse(overrides, Map.of());
      this.configPath = Objects.requireNonNullElse(configPath, "");
    }

    public String getRepo() {
      return repo;
    }

    public String getBranch() {
      return branch;
    }

    public String getCommit() {
      return commit;
    }

    public String getPipeline() {
      return pipeline;
    }

    public boolean isLocal() {
      return local;
    }

    public Map<String, String> getOverrides() {
      return overrides;
    }

    public String getConfigPath() {
      return configPath;
    }

    @Override
    public String toString() {
      return "RunPipelineRequest{" +
              "repo='" + repo + '\'' +
              ", branch='" + branch + '\'' +
              ", commit='" + commit + '\'' +
              ", pipeline='" + pipeline + '\'' +
              ", local=" + local +
              ", overrides=" + overrides +
              ", configPath='" + configPath + '\'' +
              '}';
    }
  }
}
