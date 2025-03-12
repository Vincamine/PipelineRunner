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

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
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
  private final JobExecutionRepository jobExecutionRepository;

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

    // Resolve and validate the pipeline file path
    Path resolvedPath = resolveAndValidatePipelinePath(request.getFilePath());

    // Parse and validate the pipeline YAML configuration
    Map<String, Object> pipelineConfig = parseAndValidatePipelineYaml(resolvedPath.toString());

    try {
      // Create and save the pipeline execution entity
      PipelineLogger.info("Preparing to save pipeline execution to DB...");
      PipelineExecutionEntity pipelineExecution = createPipelineExecution(request);

      // Save the pipeline execution to the database
      pipelineExecution = savePipelineExecution(pipelineExecution);

      // Create and save stage executions with their jobs
      createAndSaveStageExecutions(pipelineExecution.getId(), pipelineConfig);

      // Now that all entities are saved, execute the stages
      executeStagesSequentially(pipelineExecution.getId());

      return new PipelineExecutionResponse(pipelineExecution.getId().toString(), "RUNNING");
    } catch (Exception e) {
      PipelineLogger.error("Failed to start pipeline execution: " + e.getMessage());
      throw new RuntimeException("Pipeline execution failed: " + e.getMessage());
    }
  }

  /**
   * Resolves and validates the pipeline file path.
   *
   * @param filePath the file path from the request
   * @return the resolved path
   */
  private Path resolveAndValidatePipelinePath(String filePath) {
    Path resolvedPath = Paths.get(filePath);
    if (!resolvedPath.isAbsolute()) {
      resolvedPath = Paths.get(System.getProperty("user.dir")).resolve(filePath).normalize();
    }
    PipelineLogger.info("Corrected pipeline file path: " + resolvedPath);
    return resolvedPath;
  }

  /**
   * Parses and validates the pipeline YAML configuration.
   *
   * @param pipelinePath the path to the pipeline YAML file
   * @return the parsed pipeline configuration
   */
  private Map<String, Object> parseAndValidatePipelineYaml(String pipelinePath) {
    try {
      PipelineLogger.info("Attempting to read pipeline YAML...");
      Map<String, Object> pipelineConfig = YamlPipelineUtils.readPipelineYaml(pipelinePath);

      PipelineLogger.info("Successfully read pipeline YAML. Now validating...");
      YamlPipelineUtils.validatePipelineConfig(pipelineConfig);

      PipelineLogger.info("YAML validation completed for: " + pipelinePath);
      return pipelineConfig;
    } catch (Exception e) {
      PipelineLogger.error("ERROR reading pipeline YAML: " + e.getMessage());
      throw new RuntimeException("YAML parsing failed: " + e.getMessage(), e);
    }
  }

  /**
   * Creates a new pipeline execution entity.
   *
   * @param request the pipeline execution request
   * @return the created pipeline execution entity
   */
  private PipelineExecutionEntity createPipelineExecution(PipelineExecutionRequest request) {
    return PipelineExecutionEntity.builder()
            .pipelineId(request.getPipelineId())
            .commitHash(request.getCommitHash())
            .isLocal(request.isLocal())
            .status(ExecutionStatus.PENDING)
            .startTime(Instant.now())
            .build();
  }

  /**
   * Saves a pipeline execution to the database.
   *
   * @param pipelineExecution the pipeline execution entity to save
   * @return the saved pipeline execution entity
   */
  private PipelineExecutionEntity savePipelineExecution(PipelineExecutionEntity pipelineExecution) {
    try {
      pipelineExecution = pipelineExecutionRepository.save(pipelineExecution);
      pipelineExecutionRepository.flush(); // Force immediate commit
      PipelineLogger.info("Successfully saved pipeline execution: " + pipelineExecution.getId());

      // Verify the save was successful
      PipelineExecutionEntity savedEntity = pipelineExecutionRepository.findById(pipelineExecution.getId())
              .orElseThrow(() -> new RuntimeException("Failed to verify pipeline execution was saved"));
      PipelineLogger.info("Verified pipeline execution exists in database: " + savedEntity.getId());

      return pipelineExecution;
    } catch (Exception e) {
      PipelineLogger.error("Error saving pipeline execution: " + e.getMessage());
      throw new RuntimeException("Failed to save pipeline execution: " + e.getMessage(), e);
    }
  }

  /**
   * Creates and saves stage execution entities based on the pipeline YAML configuration.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   * @param pipelineConfig      Parsed pipeline configuration
   */
  @Transactional
  protected void createAndSaveStageExecutions(UUID pipelineExecutionId, Map<String, Object> pipelineConfig) {
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

    PipelineLogger.info("Creating " + stages.size() + " stage executions for pipeline: " + pipelineExecutionId);

    for (int order = 0; order < stages.size(); order++) {
      Map<String, Object> stageConfig = stages.get(order);
      String stageName = (String) stageConfig.get("name");

      // Create the stage execution entity without explicit ID (let JPA generate it)
      StageExecutionEntity stageExecution = StageExecutionEntity.builder()
              .pipelineExecutionId(pipelineExecutionId)
              .stageId(UUID.randomUUID())  // Generate a new stageId
              .executionOrder(order)
              .status(ExecutionStatus.PENDING)
              .startTime(Instant.now())
              .build();

      // Save the stage execution and let JPA generate the ID
      stageExecution = stageExecutionRepository.save(stageExecution);
      stageExecutionRepository.flush();  // Ensure it's committed
      PipelineLogger.info("Saved stage execution with generated ID: " + stageExecution.getId());

      // Extract and create job executions
      List<JobExecutionEntity> jobs = createJobExecutions(stageConfig, stageExecution);

      // Save job executions
      if (!jobs.isEmpty()) {
        PipelineLogger.info("Saving " + jobs.size() + " jobs for stage: " + stageExecution.getId());
        jobs = jobExecutionRepository.saveAll(jobs);
        jobExecutionRepository.flush();  // Ensure they're committed

        PipelineLogger.info("✅ Saved stage execution: " + stageExecution.getId() + " with " + jobs.size() + " jobs.");
      } else {
        PipelineLogger.warn("⚠ No jobs defined for stage: " + stageExecution.getId());
      }
    }
  }

  /**
   * Creates job execution entities for a stage.
   *
   * @param stageConfig    stage configuration from YAML
   * @param stageExecution stage execution entity
   * @return list of job execution entities
   */
  @SuppressWarnings("unchecked")
  private List<JobExecutionEntity> createJobExecutions(Map<String, Object> stageConfig, StageExecutionEntity stageExecution) {
    List<Map<String, Object>> jobsConfig = (List<Map<String, Object>>) stageConfig.get("jobs");

    if (jobsConfig == null || jobsConfig.isEmpty()) {
      PipelineLogger.warn("⚠ No jobs defined in YAML for stage: " + stageExecution.getId());
      return List.of();
    }

    return jobsConfig.stream().map(jobConfig -> {
      UUID jobId = jobConfig.containsKey("id") ?
              UUID.fromString(jobConfig.get("id").toString()) :
              UUID.randomUUID();

      return JobExecutionEntity.builder()
              // Let JPA generate the ID instead of setting it explicitly
              .stageExecution(stageExecution)
              .jobId(jobId)
              .status(ExecutionStatus.PENDING)
              .startTime(Instant.now())
              .build();
    }).toList();
  }

  /**
   * Executes stages sequentially for a pipeline execution.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   */
  private void executeStagesSequentially(UUID pipelineExecutionId) {
    PipelineLogger.info("Retrieving stages for execution...");
    List<StageExecutionEntity> stages = stageExecutionRepository.findByPipelineExecutionId(pipelineExecutionId);

    if (stages.isEmpty()) {
      PipelineLogger.error("No stages found for pipeline execution: " + pipelineExecutionId);
      throw new RuntimeException("No stages found for execution");
    }

    PipelineLogger.info("Found " + stages.size() + " stages to execute");

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