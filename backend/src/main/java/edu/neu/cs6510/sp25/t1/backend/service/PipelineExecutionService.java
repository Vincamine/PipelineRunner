package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.PipelineExecutionMapper;
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
  private final PipelineTransactionService pipelineTransactionService;
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


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

    try {
      PipelineLogger.info("Attempting to read pipeline YAML...");
      Map<String, Object> pipelineConfig = YamlPipelineUtils.readPipelineYaml(resolvedPath.toString());

      PipelineLogger.info("Successfully read pipeline YAML. Now validating...");
      YamlPipelineUtils.validatePipelineConfig(pipelineConfig);

      PipelineLogger.info("YAML validation completed for: " + resolvedPath);
    } catch (Exception e) {
      PipelineLogger.error("ERROR reading pipeline YAML: " + e.getMessage());
      throw new RuntimeException("YAML parsing failed: " + e.getMessage(), e);
    }

    // Add debug logs before saving to database
    PipelineLogger.info("Preparing to save pipeline execution to DB...");

    PipelineExecutionEntity pipelineExecution = PipelineExecutionEntity.builder()
            .pipelineId(request.getPipelineId())
            .commitHash(request.getCommitHash())
            .isLocal(request.isLocal())
            .status(ExecutionStatus.PENDING)
            .startTime(Instant.now())
            .build();

    pipelineExecution = pipelineExecutionRepository.save(pipelineExecution);
    UUID executionId = pipelineExecution.getId();

    executeStagesSequentially(executionId);

    return new PipelineExecutionResponse(executionId.toString(), "RUNNING");
  }

  private void executeStagesSequentially(UUID pipelineExecutionId) {
    PipelineLogger.info("Retrieving stages for execution...");

    List<StageExecutionEntity> stages = stageExecutionRepository.findByPipelineExecutionId(pipelineExecutionId);
    for (StageExecutionEntity stage : stages) {
      UUID stageExecutionId = stage.getId();
      PipelineLogger.info("Executing stage: " + stageExecutionId);

      stageExecutionService.executeStage(stageExecutionId);

      waitForStageCompletion(stageExecutionId);
    }

    pipelineTransactionService.finalizePipelineExecution(pipelineExecutionId);
  }

  /**
   * Waits for a stage to complete.
   *
   * @param stageExecutionId ID of the stage execution
   */
  public void waitForStageCompletion(UUID stageExecutionId) {
    scheduler.scheduleAtFixedRate(() -> {
      ExecutionStatus status = stageExecutionRepository.findById(stageExecutionId)
              .map(StageExecutionEntity::getStatus)
              .orElse(ExecutionStatus.FAILED);

      if (status == ExecutionStatus.SUCCESS) {
        scheduler.shutdown();
      }
    }, 0, 5, TimeUnit.SECONDS);
  }
}
