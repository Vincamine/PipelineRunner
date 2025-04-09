package edu.neu.cs6510.sp25.t1.backend.service.execution;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;


import edu.neu.cs6510.sp25.t1.backend.info.ClonedPipelineInfo;
import edu.neu.cs6510.sp25.t1.backend.service.pipeline.*;


import edu.neu.cs6510.sp25.t1.backend.utils.PathUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.PipelineExecutionMapper;
import edu.neu.cs6510.sp25.t1.common.api.request.PipelineExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.response.PipelineExecutionResponse;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import lombok.RequiredArgsConstructor;
import edu.neu.cs6510.sp25.t1.backend.utils.PathUtil;


/**
 * Main service for pipeline execution operations.
 * This service coordinates between different specialized services to handle
 * the complete pipeline execution lifecycle.
 */
@Service
@RequiredArgsConstructor
public class PipelineExecutionService {
  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final PipelineExecutionMapper pipelineExecutionMapper;
  
  private final PipelineDefinitionService pipelineDefinitionService;
  private final YamlConfigurationService yamlConfigurationService;
  private final PipelineExecutionCreationService pipelineExecutionCreationService;
  private final PipelineStatusService pipelineStatusService;
  private final ExecutionQueueService executionQueueService;
  private final GitPipelineService gitPipelineService;
  private final edu.neu.cs6510.sp25.t1.backend.service.queue.PipelineExecutionQueueService pipelineExecutionQueueService;

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
   * Starts a new pipeline execution by parsing YAML, saving entities, and queuing execution.
   * This method coordinates between different services to handle the complete execution process.
   *
   * @param request request containing pipeline details and YAML file path
   * @param stageQueue queue of stages to be executed
   * @return response containing pipeline execution ID and status
   */
  @Transactional(rollbackFor = Exception.class)
  public PipelineExecutionResponse startPipelineExecution(PipelineExecutionRequest request, Queue<Queue<UUID>> stageQueue) {
    PipelineLogger.info("Received pipeline execution request for: " + request.getFilePath());

    try {
      // step 0: use volume to clone the repo then pass the volume file path to step 1
      ClonedPipelineInfo info = gitPipelineService.cloneRepoAndLocatePipelineFile(request);

      // Step 1: Resolve and validate the pipeline file path
      var resolvedPath = yamlConfigurationService.resolveAndValidatePipelinePath(info.getYamlPath());

      // changing rootPath to repo url, use this working Dir block in db to save url for worker extraction
      String rootPath = request.getFilePath();
//      String rootPath = PathUtil.extractPipelineRootDirectoryAsString(resolvedPath);

      // Step 2: Parse and validate the pipeline YAML configuration
      Map<String, Object> pipelineConfig = yamlConfigurationService.parseAndValidatePipelineYaml(resolvedPath.toString());

      // Step 3: Create or get pipeline entity
      PipelineLogger.info("Step 1: Creating or getting pipeline entity");
      UUID pipelineId = pipelineDefinitionService.createOrGetPipelineEntity(request, pipelineConfig);
      PipelineLogger.info("Pipeline entity created/retrieved with ID: " + pipelineId);

      // Step 4: Create pipeline stages and jobs
      PipelineLogger.info("Step 2: Creating pipeline stages and jobs");
      pipelineDefinitionService.createPipelineDefinition(pipelineId, pipelineConfig, rootPath);

      // Step 5: Create and save the pipeline execution entity
      PipelineLogger.info("Step 3: Creating pipeline execution entity");
      PipelineExecutionEntity pipelineExecution = pipelineExecutionCreationService.createPipelineExecution(request, pipelineId);
      pipelineExecution = pipelineExecutionCreationService.savePipelineExecution(pipelineExecution);
      PipelineLogger.info("Pipeline execution saved with ID: " + pipelineExecution.getId());

      // Step 6: Create and save stage executions with their jobs
      PipelineLogger.info("Step 4: Creating stage executions and job executions");
      pipelineExecutionCreationService.createAndSaveStageExecutions(pipelineExecution.getId(), pipelineConfig, stageQueue);

      // Step 7: Verify entities were properly saved
      PipelineLogger.info("Step 5: Verifying entities were properly saved");
      pipelineExecutionCreationService.verifyEntitiesSaved(pipelineId, pipelineExecution.getId());

      // Step 8: delete the folder
      File clonedFolder = new File("/mnt/pipeline", info.getUuid().toString());
      try {
        FileUtils.deleteDirectory(clonedFolder); // org.apache.commons.io.FileUtils
        PipelineLogger.info("Cleaned up cloned repo: " + clonedFolder.getAbsolutePath());
      } catch (IOException e) {
        PipelineLogger.warn("Failed to clean up cloned repo: " + e.getMessage());
      }

      return new PipelineExecutionResponse(pipelineExecution.getId().toString(), "PENDING");
    } catch (Exception e) {
      PipelineLogger.error("Failed to start pipeline execution: " + e.getMessage() + " | " + e);
      throw new RuntimeException("Pipeline execution failed: " + e.getMessage());
    }
  }

  /**
   * Process a pipeline execution from the queue.
   * Delegates to ExecutionQueueService.
   *
   * @param pipelineExecutionId ID of the pipeline execution to process
   */
  @Transactional
  public void processPipelineExecution(UUID pipelineExecutionId) {
    executionQueueService.processPipelineExecution(pipelineExecutionId);
  }
  
  /**
   * Updates the status of a pipeline execution.
   * Delegates to PipelineStatusService.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   * @param status the new status
   */
  @Transactional
  public void updatePipelineStatus(UUID pipelineExecutionId, edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus status) {
    pipelineStatusService.updatePipelineStatus(pipelineExecutionId, status);
  }
  
  /**
   * Finalizes a pipeline execution.
   * Delegates to PipelineStatusService.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   */
  @Transactional
  public void finalizePipelineExecution(UUID pipelineExecutionId) {
    pipelineStatusService.finalizePipelineExecution(pipelineExecutionId);
  }
}