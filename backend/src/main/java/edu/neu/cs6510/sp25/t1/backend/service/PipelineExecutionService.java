package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.PipelineExecutionMapper;
import edu.neu.cs6510.sp25.t1.backend.service.event.StageCompletedEvent;
import edu.neu.cs6510.sp25.t1.backend.utils.YamlPipelineUtils;
import edu.neu.cs6510.sp25.t1.common.api.request.PipelineExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.response.PipelineExecutionResponse;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for managing pipeline executions.
 */
@Service
@RequiredArgsConstructor
public class PipelineExecutionService {
  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final PipelineExecutionMapper pipelineExecutionMapper;
  private final StageExecutionService stageExecutionService;
  private final StageExecutionRepository stageExecutionRepository;


  /**
   * Retrieves a pipeline execution by ID.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   * @return response containing pipeline execution ID and status
   */
  public PipelineExecutionResponse getPipelineExecution(UUID pipelineExecutionId) {
    PipelineExecutionDTO dto = pipelineExecutionRepository.findById(pipelineExecutionId)
            .map(pipelineExecutionMapper::toDTO)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline Execution not found"));
    return new PipelineExecutionResponse(dto.getId().toString(), dto.getStatus().toString());
  }

  /**
   * Starts a new run of a pipeline after reading and validating the YAML configuration.
   *
   * @param request request containing pipeline ID and commit hash
   * @return response containing pipeline execution ID and status
   */
  @Transactional
  public PipelineExecutionResponse startPipelineExecution(PipelineExecutionRequest request) {
    PipelineLogger.info("Received pipeline execution request for: " + request.getFilePath());

    Path resolvedPath = Paths.get(request.getFilePath());
    if (!resolvedPath.isAbsolute()) {
      resolvedPath = Paths.get(System.getProperty("user.dir")).resolve(request.getFilePath()).normalize();
    }

    PipelineLogger.info("Corrected pipeline file path: " + resolvedPath);

    Map<String, Object> pipelineConfig;
    try {
      PipelineLogger.info("Attempting to read pipeline YAML...");
      pipelineConfig = YamlPipelineUtils.readPipelineYaml(resolvedPath.toString());

      PipelineLogger.info("Successfully read pipeline YAML. Now validating...");
      YamlPipelineUtils.validatePipelineConfig(pipelineConfig);

      PipelineLogger.info("YAML validation completed for: " + resolvedPath);
    } catch (Exception e) {
      PipelineLogger.error("ERROR reading pipeline YAML: " + e.getMessage());
      throw new RuntimeException("YAML parsing failed: " + e.getMessage(), e);
    }

    // Save the pipeline execution
    PipelineLogger.info("Preparing to save pipeline execution to DB...");
    PipelineExecutionEntity pipelineExecution = PipelineExecutionEntity.builder()
            .pipelineId(request.getPipelineId())
            .commitHash(request.getCommitHash())
            .isLocal(request.isLocal())
            .status(ExecutionStatus.PENDING)
            .startTime(Instant.now())
            .build();

    pipelineExecution = pipelineExecutionRepository.save(pipelineExecution);
    pipelineExecutionRepository.flush(); // Force immediate commit
    PipelineLogger.info("Successfully saved pipeline execution: " + pipelineExecution.getId());

    // **Create Stage Executions**
    createStageExecutions(pipelineExecution.getId(), pipelineConfig);

    // Execute Stages
    executeStagesSequentially(pipelineExecution.getId());

    return new PipelineExecutionResponse(pipelineExecution.getId().toString(), "RUNNING");
  }

  /**
   * Creates stage execution entities based on the pipeline YAML configuration.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   * @param pipelineConfig      Parsed pipeline configuration
   */
  @SuppressWarnings("unchecked")
  private void createStageExecutions(UUID pipelineExecutionId, Map<String, Object> pipelineConfig) {
    Object stagesObj = pipelineConfig.get("stages");

    if (!(stagesObj instanceof List<?> rawStages)) {
      PipelineLogger.error("Invalid pipeline structure: 'stages' must be a list.");
      throw new RuntimeException("Invalid pipeline configuration: 'stages' key is missing or incorrect.");
    }

    List<Map<String, Object>> stages = rawStages.stream()
            .filter(item -> item instanceof Map)  // Ensure item is a Map
            .map(item -> (Map<String, Object>) item)
            .toList();

    if (stages.isEmpty()) {
      PipelineLogger.error("No valid stages found in pipeline configuration!");
      throw new RuntimeException("Pipeline must contain at least one valid stage.");
    }

    for (int order = 0; order < stages.size(); order++) {
      Map<String, Object> stage = stages.get(order);
      String stageName = (String) stage.get("name");

      UUID stageId = UUID.randomUUID();
      StageExecutionEntity stageExecution = StageExecutionEntity.builder()
              .pipelineExecutionId(pipelineExecutionId)
              .stageId(stageId)
              .executionOrder(order)
              .status(ExecutionStatus.PENDING)
              .startTime(Instant.now())
              .build();

      stageExecutionRepository.save(stageExecution);
      PipelineLogger.info("Saved stage execution: " + stageExecution.getId() + " for stage: " + stageName);
    }
  }


  /**
   * Executes stages sequentially for a pipeline execution.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   */
  private void executeStagesSequentially(UUID pipelineExecutionId) {
    PipelineLogger.info("Retrieving stages for execution...");
    List<StageExecutionEntity> stages = stageExecutionRepository.findByPipelineExecutionId(pipelineExecutionId);
    for (StageExecutionEntity stage : stages) {
      UUID stageExecutionId = stage.getId();
      PipelineLogger.info("Executing stage: " + stageExecutionId);
      stageExecutionService.executeStage(stageExecutionId);
    }
  }

  /**
   * Event listener for stage completion.
   *
   * @param event stage completion event
   */
  @EventListener
  @Transactional
  public void onStageCompleted(StageCompletedEvent event) {
    UUID pipelineExecutionId = event.getPipelineExecutionId();
    PipelineLogger.info("Stage completed in pipeline: " + pipelineExecutionId + " | Checking if pipeline is done...");

    List<StageExecutionEntity> stages = stageExecutionRepository.findByPipelineExecutionId(pipelineExecutionId);
    boolean allSuccess = stages.stream().allMatch(s -> s.getStatus() == ExecutionStatus.SUCCESS);

    if (allSuccess) {
      PipelineLogger.info("All stages completed! Marking pipeline as done.");
      finalizePipelineExecution(pipelineExecutionId);
    }
  }

  /**
   * Finalizes a pipeline execution.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   */
  @Transactional
  public void finalizePipelineExecution(UUID pipelineExecutionId) {
    PipelineLogger.info("Finalizing pipeline execution: " + pipelineExecutionId);

    PipelineExecutionEntity pipelineExecution = pipelineExecutionRepository.findById(pipelineExecutionId)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline Execution not found"));

    pipelineExecution.updateState(ExecutionStatus.SUCCESS);
    pipelineExecutionRepository.save(pipelineExecution);
    PipelineLogger.info("Pipeline execution completed: " + pipelineExecutionId);
  }
}
