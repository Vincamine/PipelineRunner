package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobScriptRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.PipelineExecutionMapper;
import edu.neu.cs6510.sp25.t1.backend.service.event.StageCompletedEvent;
import edu.neu.cs6510.sp25.t1.backend.service.queue.PipelineExecutionQueueService;
import edu.neu.cs6510.sp25.t1.backend.service.queue.StageExecutionQueueService;
import edu.neu.cs6510.sp25.t1.backend.utils.YamlPipelineUtils;
import edu.neu.cs6510.sp25.t1.common.api.request.PipelineExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.response.PipelineExecutionResponse;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for managing pipeline executions.
 * This service has been refactored to:
 * 1. Work with a queue system for pipeline, stage, and job executions
 * 2. Remove worker dependencies
 * 3. Follow the "one function does one thing" principle
 */
@Service
@RequiredArgsConstructor
public class PipelineExecutionService {
  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final PipelineRepository pipelineRepository;
  private final PipelineExecutionMapper pipelineExecutionMapper;
  private final StageExecutionService stageExecutionService;
  private final StageExecutionRepository stageExecutionRepository;
  private final StageRepository stageRepository;
  private final JobExecutionRepository jobExecutionRepository;
  private final JobRepository jobRepository;
  private final JobScriptRepository jobScriptRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final PipelineExecutionQueueService pipelineExecutionQueueService;
  private final StageExecutionQueueService stageExecutionQueueService;

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
   * This method does validation, entity creation, and queuing but doesn't do actual execution logic.
   *
   * @param request request containing pipeline details and YAML file path
   * @return response containing pipeline execution ID and status
   */
  @Transactional(rollbackFor = Exception.class)
  public PipelineExecutionResponse startPipelineExecution(PipelineExecutionRequest request) {
    PipelineLogger.info("Received pipeline execution request for: " + request.getFilePath());

    // Resolve and validate the pipeline file path
    Path resolvedPath = resolveAndValidatePipelinePath(request.getFilePath());

    // Parse and validate the pipeline YAML configuration
    Map<String, Object> pipelineConfig = parseAndValidatePipelineYaml(resolvedPath.toString());

    try {
      // Create or get pipeline entity first (ensuring it exists in pipelines table)
      UUID pipelineId = createOrGetPipelineEntity(request, pipelineConfig);

      // Create pipeline stages and jobs from the configuration
      createPipelineDefinition(pipelineId, pipelineConfig);

      // Create and save the pipeline execution entity
      PipelineLogger.info("Preparing to save pipeline execution to DB...");
      PipelineExecutionEntity pipelineExecution = createPipelineExecution(request, pipelineId);

      // Save the pipeline execution to the database
      pipelineExecution = savePipelineExecution(pipelineExecution);
      
      // Create and save stage executions with their jobs
      createAndSaveStageExecutions(pipelineExecution.getId(), pipelineConfig);

      // Verify entities were properly saved (optional but helpful for debugging)
      verifyEntitiesSaved(pipelineId);

      // Add pipeline execution to queue instead of executing directly
      pipelineExecutionQueueService.enqueuePipelineExecution(pipelineExecution.getId());

      return new PipelineExecutionResponse(pipelineExecution.getId().toString(), "PENDING");
    } catch (Exception e) {
      PipelineLogger.error("Failed to start pipeline execution: " + e.getMessage() + " | " + e);
      throw new RuntimeException("Pipeline execution failed: " + e.getMessage());
    }
  }

  /**
   * Process a pipeline execution from the queue.
   * This method is called by the queue service to process a pipeline execution.
   *
   * @param pipelineExecutionId ID of the pipeline execution to process
   */
  @Transactional
  public void processPipelineExecution(UUID pipelineExecutionId) {
    PipelineLogger.info("Processing pipeline execution: " + pipelineExecutionId);
    
    try {
      // Update pipeline status to RUNNING
      updatePipelineStatus(pipelineExecutionId, ExecutionStatus.RUNNING);
      
      // Queue stages for execution
      queueStagesForExecution(pipelineExecutionId);
    } catch (Exception e) {
      PipelineLogger.error("Failed to process pipeline execution: " + e.getMessage());
      updatePipelineStatus(pipelineExecutionId, ExecutionStatus.FAILED);
    }
  }
  
  /**
   * Updates the status of a pipeline execution.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   * @param status the new status
   */
  @Transactional
  public void updatePipelineStatus(UUID pipelineExecutionId, ExecutionStatus status) {
    PipelineExecutionEntity pipelineExecution = pipelineExecutionRepository.findById(pipelineExecutionId)
        .orElseThrow(() -> new IllegalArgumentException("Pipeline Execution not found"));
    
    // Set start time when pipeline starts running
    if (status == ExecutionStatus.RUNNING && pipelineExecution.getStartTime() == null) {
      pipelineExecution.setStartTime(Instant.now());
    }
    
    // Update state which will set completionTime for terminal states
    pipelineExecution.updateState(status);
    
    pipelineExecutionRepository.saveAndFlush(pipelineExecution);
    PipelineLogger.info("Updated pipeline execution status to " + status + ": " + pipelineExecutionId);
  }

  /**
   * Queue stages for execution based on their order.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   */
  private void queueStagesForExecution(UUID pipelineExecutionId) {
    List<StageExecutionEntity> stages = stageExecutionRepository.findByPipelineExecutionId(pipelineExecutionId);
    
    if (stages.isEmpty()) {
      PipelineLogger.error("No stages found for pipeline execution: " + pipelineExecutionId);
      throw new RuntimeException("No stages found for execution");
    }
    
    PipelineLogger.info("Queueing " + stages.size() + " stages for execution");
    
    // Sort stages by execution order
    stages.stream()
        .sorted((s1, s2) -> Integer.compare(s1.getExecutionOrder(), s2.getExecutionOrder()))
        .forEach(stage -> {
          // Add each stage to the stage execution queue
          stageExecutionQueueService.enqueueStageExecution(stage.getId());
          PipelineLogger.info("Stage queued for execution: " + stage.getId() + " (order: " + stage.getExecutionOrder() + ")");
        });
  }

  /**
   * Resolves and validates the pipeline file path.
   *
   * @param filePath the file path from the request
   * @return the resolved path
   */
  private Path resolveAndValidatePipelinePath(String filePath) {
    if (filePath == null || filePath.trim().isEmpty()) {
      throw new IllegalArgumentException("Pipeline file path cannot be null or empty");
    }
    
    Path resolvedPath = Paths.get(filePath);
    if (!resolvedPath.isAbsolute()) {
      resolvedPath = Paths.get(System.getProperty("user.dir")).resolve(filePath).normalize();
    }
    
    PipelineLogger.info("Corrected pipeline file path: " + resolvedPath.toAbsolutePath());
    
    // Validate file exists and is readable
    if (!Files.exists(resolvedPath)) {
      PipelineLogger.error("Pipeline configuration file not found: " + resolvedPath.toAbsolutePath());
      throw new IllegalArgumentException("Pipeline configuration file not found: " + resolvedPath.toAbsolutePath());
    }
    
    if (!Files.isReadable(resolvedPath)) {
      PipelineLogger.error("Pipeline configuration file is not readable: " + resolvedPath.toAbsolutePath());
      throw new IllegalArgumentException("Pipeline configuration file is not readable: " + resolvedPath.toAbsolutePath());
    }
    
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
   * Creates or retrieves an existing pipeline entity based on the request.
   *
   * @param request the pipeline execution request 
   * @param pipelineConfig the pipeline configuration from YAML
   * @return the ID of the pipeline entity
   */
  @Transactional
  protected UUID createOrGetPipelineEntity(PipelineExecutionRequest request, Map<String, Object> pipelineConfig) {
    // Debug log the request
    PipelineLogger.info("createOrGetPipelineEntity called with pipelineId: " + request.getPipelineId());
    PipelineLogger.info("Repo: " + request.getRepo() + ", Branch: " + request.getBranch());
    PipelineLogger.info("CommitHash: " + request.getCommitHash() + ", FilePath: " + request.getFilePath());
    
    // Check if a pipeline ID was provided in the request
    if (request.getPipelineId() != null) {
      // Check if the pipeline exists in the database
      if (pipelineRepository.existsById(request.getPipelineId())) {
        PipelineLogger.info("Using existing pipeline with ID: " + request.getPipelineId());
        return request.getPipelineId();
      } else {
        PipelineLogger.info("Pipeline ID provided but not found in database: " + request.getPipelineId());
      }
    } else {
      PipelineLogger.info("No pipeline ID provided in request, will create new entity");
    }
    
    // Extract pipeline properties from config
    String name = extractPipelineName(pipelineConfig);
    String repoUrl = extractRepositoryUrl(request, pipelineConfig);
    String branch = extractBranch(request, pipelineConfig);
    
    PipelineLogger.info("Building pipeline entity with name: " + name);
    PipelineLogger.info("Repository URL: " + repoUrl);
    PipelineLogger.info("Branch: " + branch);
    PipelineLogger.info("Commit Hash: " + request.getCommitHash());
    
    // Create and save pipeline entity
    PipelineEntity pipeline = PipelineEntity.builder()
        .name(name)
        .repositoryUrl(repoUrl)
        .branch(branch)
        .commitHash(request.getCommitHash())
        .build();
    
    try {
      pipeline = pipelineRepository.saveAndFlush(pipeline);
      
      // Verify the save by retrieving it
      if (pipelineRepository.existsById(pipeline.getId())) {
        PipelineLogger.info("Successfully created and verified pipeline entity with ID: " + pipeline.getId());
      } else {
        PipelineLogger.error("Failed to verify pipeline entity was saved: " + pipeline.getId());
      }
    } catch (Exception e) {
      PipelineLogger.error("Error saving pipeline entity: " + e.getMessage());
      throw e;
    }
    
    return pipeline.getId();
  }
  
  /**
   * Extract name from pipeline configuration.
   *
   * @param pipelineConfig the pipeline configuration
   * @return the pipeline name
   */
  private String extractPipelineName(Map<String, Object> pipelineConfig) {
    return pipelineConfig.containsKey("name") 
        ? (String) pipelineConfig.get("name") 
        : "pipeline-" + UUID.randomUUID().toString().substring(0, 8);
  }
  
  /**
   * Extract repository URL from request or pipeline configuration.
   *
   * @param request the pipeline execution request
   * @param pipelineConfig the pipeline configuration
   * @return the repository URL
   */
  private String extractRepositoryUrl(PipelineExecutionRequest request, Map<String, Object> pipelineConfig) {
    return request.getRepo() != null 
        ? request.getRepo() 
        : (pipelineConfig.containsKey("repository") 
            ? (String) pipelineConfig.get("repository") 
            : "local-repository");
  }
  
  /**
   * Extract branch from request or pipeline configuration.
   *
   * @param request the pipeline execution request
   * @param pipelineConfig the pipeline configuration
   * @return the branch name
   */
  private String extractBranch(PipelineExecutionRequest request, Map<String, Object> pipelineConfig) {
    return request.getBranch() != null 
        ? request.getBranch() 
        : (pipelineConfig.containsKey("branch") 
            ? (String) pipelineConfig.get("branch") 
            : "main");
  }
  
  /**
   * Creates stage and job entities based on pipeline configuration.
   *
   * @param pipelineId the pipeline ID
   * @param pipelineConfig the parsed pipeline configuration
   */
  @Transactional
  protected void createPipelineDefinition(UUID pipelineId, Map<String, Object> pipelineConfig) {
    PipelineLogger.info("Creating stage and job definitions for pipeline: " + pipelineId);
    
    // Verify the pipeline exists first
    PipelineEntity pipeline = pipelineRepository.findById(pipelineId)
        .orElseThrow(() -> {
            PipelineLogger.error("Pipeline not found with ID: " + pipelineId);
            return new RuntimeException("Pipeline not found: " + pipelineId);
        });
    
    PipelineLogger.info("Found pipeline entity with ID: " + pipelineId + ", name: " + pipeline.getName());
    
    List<Map<String, Object>> stages = extractStagesFromConfig(pipelineConfig);
    
    if (stages.isEmpty()) {
      PipelineLogger.error("No valid stages found in pipeline configuration!");
      throw new RuntimeException("Pipeline must contain at least one valid stage.");
    }
    
    PipelineLogger.info("Found " + stages.size() + " stages in pipeline configuration");
    
    for (int order = 0; order < stages.size(); order++) {
      Map<String, Object> stageConfig = stages.get(order);
      createStageWithJobs(pipelineId, stageConfig, order);
    }
  }
  
  /**
   * Extract stages from pipeline configuration.
   *
   * @param pipelineConfig the pipeline configuration
   * @return list of stage configurations
   */
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> extractStagesFromConfig(Map<String, Object> pipelineConfig) {
    Object stagesObj = pipelineConfig.get("stages");
    if (!(stagesObj instanceof List<?> rawStages)) {
      PipelineLogger.error("Invalid pipeline structure: 'stages' must be a list.");
      throw new RuntimeException("Invalid pipeline configuration: 'stages' key is missing or incorrect.");
    }
    
    return rawStages.stream()
        .filter(item -> item instanceof Map)
        .map(item -> (Map<String, Object>) item)
        .toList();
  }
  
  /**
   * Create a stage entity with its jobs.
   *
   * @param pipelineId the pipeline ID
   * @param stageConfig the stage configuration
   * @param order the execution order
   */
  @Transactional
  private void createStageWithJobs(UUID pipelineId, Map<String, Object> stageConfig, int order) {
    String stageName = (String) stageConfig.get("name");
    
    PipelineLogger.info("Creating stage with name: " + stageName + ", order: " + order);
    
    // Create and save the stage entity
    StageEntity stage = StageEntity.builder()
        .pipelineId(pipelineId)
        .name(stageName)
        .executionOrder(order)
        .build();
    
    try {
      stage = stageRepository.save(stage);
      stageRepository.flush();
      
      // Verify the stage was saved
      if (stageRepository.existsById(stage.getId())) {
        PipelineLogger.info("Successfully created stage with ID: " + stage.getId());
      } else {
        PipelineLogger.error("Failed to verify stage was saved: " + stage.getId());
      }
      
      // Create and save job entities for this stage
      createJobDefinitions(stage.getId(), stageConfig);
    } catch (Exception e) {
      PipelineLogger.error("Error saving stage entity: " + e.getMessage());
      throw e;
    }
  }
  
  /**
   * Creates job entities based on stage configuration.
   *
   * @param stageId the stage ID
   * @param stageConfig the stage configuration from YAML
   */
  @SuppressWarnings("unchecked")
  private void createJobDefinitions(UUID stageId, Map<String, Object> stageConfig) {
    // Verify the stage exists first
    StageEntity stage = stageRepository.findById(stageId)
        .orElseThrow(() -> {
            PipelineLogger.error("Stage not found with ID: " + stageId);
            return new RuntimeException("Stage not found: " + stageId);
        });
    
    PipelineLogger.info("Found stage entity with ID: " + stageId + ", name: " + stage.getName());
    
    List<Map<String, Object>> jobsConfig = (List<Map<String, Object>>) stageConfig.get("jobs");
    
    if (jobsConfig == null || jobsConfig.isEmpty()) {
      PipelineLogger.warn("No jobs defined in YAML for stage: " + stageId);
      return;
    }
    
    PipelineLogger.info("Found " + jobsConfig.size() + " jobs in stage configuration");
    
    for (Map<String, Object> jobConfig : jobsConfig) {
      createJob(stageId, jobConfig);
    }
  }
  
  /**
   * Create a job entity.
   *
   * @param stageId the stage ID
   * @param jobConfig the job configuration
   */
  @Transactional
  private void createJob(UUID stageId, Map<String, Object> jobConfig) {
    String jobName = (String) jobConfig.get("name");
    PipelineLogger.info("Creating job with name: " + jobName + " for stage: " + stageId);
    
    String dockerImage = extractDockerImage(jobConfig);
    boolean allowFailure = extractAllowFailure(jobConfig);
    
    PipelineLogger.info("Job details - Name: " + jobName + ", Docker image: " + dockerImage + ", Allow failure: " + allowFailure);
    
    // Create and save the job entity
    JobEntity job = JobEntity.builder()
        .stageId(stageId)
        .name(jobName)
        .dockerImage(dockerImage)
        .allowFailure(allowFailure)
        .build();
    
    try {
      job = jobRepository.save(job);
      jobRepository.flush();
      
      // Verify the job was saved
      if (jobRepository.existsById(job.getId())) {
        PipelineLogger.info("Successfully created job with ID: " + job.getId());
      } else {
        PipelineLogger.error("Failed to verify job was saved: " + job.getId());
      }
      
      // Handle job scripts if present
      saveJobScripts(job.getId(), jobConfig);
    } catch (Exception e) {
      PipelineLogger.error("Error saving job entity: " + e.getMessage() + " | " + e);
      throw e;
    }
  }
  
  /**
   * Extract docker image from job configuration.
   *
   * @param jobConfig the job configuration
   * @return the docker image
   */
  private String extractDockerImage(Map<String, Object> jobConfig) {
    return jobConfig.containsKey("image") 
        ? (String) jobConfig.get("image") 
        : "docker.io/library/alpine:latest";
  }
  
  /**
   * Extract allow failure flag from job configuration.
   *
   * @param jobConfig the job configuration
   * @return true if failure is allowed, false otherwise
   */
  private boolean extractAllowFailure(Map<String, Object> jobConfig) {
    if (!jobConfig.containsKey("allow_failure")) {
      return false;
    }
    
    Object allowFailureObj = jobConfig.get("allow_failure");
    if (allowFailureObj instanceof Boolean) {
      return (Boolean) allowFailureObj;
    } else if (allowFailureObj instanceof String) {
      return Boolean.parseBoolean((String) allowFailureObj);
    }
    
    return false;
  }
  
  /**
   * Save job scripts.
   *
   * @param jobId the job ID
   * @param jobConfig the job configuration
   */
  @SuppressWarnings("unchecked")
  private void saveJobScripts(UUID jobId, Map<String, Object> jobConfig) {
    if (!jobConfig.containsKey("script")) {
      PipelineLogger.warn("No scripts defined for job: " + jobId);
      return;
    }
    
    Object scriptObj = jobConfig.get("script");
    if (scriptObj instanceof String) {
      // Single script line
      String script = (String) scriptObj;
      PipelineLogger.info("Adding script to job " + jobId + ": " + 
          (script.length() > 30 ? script.substring(0, 30) + "..." : script));
      jobScriptRepository.saveScript(jobId, script);
    } else if (scriptObj instanceof List) {
      // Multiple script lines
      List<String> scripts = ((List<?>) scriptObj).stream()
          .filter(s -> s instanceof String)
          .map(s -> (String) s)
          .toList();
      
      PipelineLogger.info("Adding " + scripts.size() + " script lines to job " + jobId);
      for (String script : scripts) {
        jobScriptRepository.saveScript(jobId, script);
      }
    }
  }

  /**
   * Creates a new pipeline execution entity.
   *
   * @param request the pipeline execution request
   * @param pipelineId the ID of the pipeline entity
   * @return the created pipeline execution entity
   */
  private PipelineExecutionEntity createPipelineExecution(PipelineExecutionRequest request, UUID pipelineId) {
    return PipelineExecutionEntity.builder()
            .pipelineId(pipelineId)
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
      pipelineExecution = pipelineExecutionRepository.saveAndFlush(pipelineExecution); // Save and flush in one operation
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
    List<Map<String, Object>> stages = extractStagesFromConfig(pipelineConfig);

    if (stages.isEmpty()) {
      PipelineLogger.error("No valid stages found in pipeline configuration!");
      throw new RuntimeException("Pipeline must contain at least one valid stage.");
    }

    // Get pipeline execution to retrieve pipelineId
    PipelineExecutionEntity pipelineExecution = pipelineExecutionRepository.findById(pipelineExecutionId)
            .orElseThrow(() -> new RuntimeException("Pipeline execution not found: " + pipelineExecutionId));
    
    // Get all stages for this pipeline
    List<StageEntity> pipelineStages = stageRepository.findByPipelineId(pipelineExecution.getPipelineId());
    if (pipelineStages.isEmpty()) {
      PipelineLogger.error("No stage definitions found for pipeline: " + pipelineExecution.getPipelineId());
      throw new RuntimeException("Pipeline stage definitions not found");
    }

    PipelineLogger.info("Creating " + stages.size() + " stage executions for pipeline: " + pipelineExecutionId);
    
    String commitHash = pipelineExecution.getCommitHash();
    boolean isLocal = pipelineExecution.isLocal();
    PipelineLogger.info("Using commit hash: " + commitHash + " and isLocal: " + isLocal);

    for (int order = 0; order < stages.size(); order++) {
      createStageExecution(pipelineExecutionId, pipelineStages, order, commitHash, isLocal);
    }
  }
  
  /**
   * Create a stage execution entity.
   *
   * @param pipelineExecutionId the pipeline execution ID
   * @param pipelineStages the list of stage entities
   * @param order the execution order
   * @param commitHash the commit hash
   * @param isLocal whether the execution is local
   */
  @Transactional
  private void createStageExecution(UUID pipelineExecutionId, List<StageEntity> pipelineStages, int order, 
      String commitHash, boolean isLocal) {
    // Find the matching stage entity for this order
    int finalOrder = order;
    StageEntity matchingStage = pipelineStages.stream()
            .filter(stage -> stage.getExecutionOrder() == finalOrder)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Stage definition not found for order: " + finalOrder));
    
    // Create the stage execution entity
    StageExecutionEntity stageExecution = StageExecutionEntity.builder()
            .pipelineExecutionId(pipelineExecutionId)
            .stageId(matchingStage.getId())  // Use actual stage ID 
            .executionOrder(order)
            .commitHash(commitHash)  // Use commit hash from pipeline execution
            .isLocal(isLocal)        // Use isLocal from pipeline execution
            .status(ExecutionStatus.PENDING)
            .startTime(Instant.now())
            .build();

    try {
      // Save the stage execution
      stageExecution = stageExecutionRepository.saveAndFlush(stageExecution);
      PipelineLogger.info("Saved stage execution with ID: " + stageExecution.getId() + " for stage: " + matchingStage.getId());

      // Extract and create job executions
      List<JobExecutionEntity> jobs = createJobExecutions(matchingStage.getId(), stageExecution);

      // Save job executions
      if (!jobs.isEmpty()) {
        PipelineLogger.info("Saving " + jobs.size() + " jobs for stage: " + stageExecution.getId());
        jobs = jobExecutionRepository.saveAll(jobs).stream().toList();
        // No need to flush here, let Spring manage the transaction

        PipelineLogger.info("✅ Saved stage execution: " + stageExecution.getId() + " with " + jobs.size() + " jobs.");
      } else {
        PipelineLogger.warn("⚠ No jobs defined for stage: " + stageExecution.getId());
      }
    } catch (Exception e) {
      PipelineLogger.error("Error saving stage execution: " + e.getMessage() + " | " + e);
      throw e;
    }
  }

  /**
   * Creates job execution entities for a stage.
   *
   * @param stageId       ID of the stage entity
   * @param stageExecution stage execution entity
   * @return list of job execution entities
   */
  private List<JobExecutionEntity> createJobExecutions(UUID stageId, StageExecutionEntity stageExecution) {
    // Get all jobs for this stage
    List<JobEntity> stageJobs = jobRepository.findByStageId(stageId);
    
    if (stageJobs.isEmpty()) {
      PipelineLogger.warn("No job definitions found for stage: " + stageId);
      return List.of();
    }

    return stageJobs.stream().map(job -> {
      return JobExecutionEntity.builder()
              .stageExecution(stageExecution)
              .jobId(job.getId())  // Use actual job ID
              .commitHash(stageExecution.getCommitHash())  // Use commit hash from stage execution
              .isLocal(stageExecution.isLocal())        // Use isLocal from stage execution
              .allowFailure(job.isAllowFailure())        // Use allowFailure from job entity
              .status(ExecutionStatus.PENDING)
              .startTime(Instant.now())
              .build();
    }).collect(java.util.stream.Collectors.toList());
  }

  /**
   * Verify all entities were saved correctly.
   *
   * @param pipelineId the pipeline ID
   */
  private void verifyEntitiesSaved(UUID pipelineId) {
    try {
      // Check pipeline exists
      if (!pipelineRepository.existsById(pipelineId)) {
        PipelineLogger.error("Pipeline entity was not saved correctly: " + pipelineId);
      } else {
        PipelineLogger.info("Successfully verified pipeline entity: " + pipelineId);
      }

      // Check stages exist
      List<StageEntity> stages = stageRepository.findByPipelineId(pipelineId);
      PipelineLogger.info("Found " + stages.size() + " stages for pipeline: " + pipelineId);

      // Check jobs exist for each stage
      for (StageEntity stage : stages) {
        List<JobEntity> jobs = jobRepository.findByStageId(stage.getId());
        PipelineLogger.info("Found " + jobs.size() + " jobs for stage: " + stage.getId());
      }
    } catch (Exception e) {
      PipelineLogger.error("Error verifying saved entities: " + e.getMessage());
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

    checkAndFinalizePipeline(pipelineExecutionId);
  }
  
  /**
   * Check if all stages are complete and finalize the pipeline if necessary.
   *
   * @param pipelineExecutionId the pipeline execution ID
   */
  @Transactional
  public void checkAndFinalizePipeline(UUID pipelineExecutionId) {
    List<StageExecutionEntity> stages = stageExecutionRepository.findByPipelineExecutionId(pipelineExecutionId);
    
    // Check if all stages are complete
    boolean allComplete = stages.stream().allMatch(s -> 
        s.getStatus() == ExecutionStatus.SUCCESS || 
        s.getStatus() == ExecutionStatus.FAILED || 
        s.getStatus() == ExecutionStatus.CANCELED);
    
    if (!allComplete) {
      PipelineLogger.info("Not all stages are complete yet. Waiting for remaining stages...");
      return;
    }
    
    // Check if any stage failed and was not allowed to fail
    boolean anyFailedNotAllowed = stages.stream().anyMatch(s -> 
        s.getStatus() == ExecutionStatus.FAILED && 
        !stageAllowsFailure(s.getId()));
    
    if (anyFailedNotAllowed) {
      PipelineLogger.error("At least one stage failed and failure is not allowed. Marking pipeline as FAILED.");
      updatePipelineStatus(pipelineExecutionId, ExecutionStatus.FAILED);
    } else {
      PipelineLogger.info("All stages completed successfully or failures were allowed. Marking pipeline as SUCCESS.");
      updatePipelineStatus(pipelineExecutionId, ExecutionStatus.SUCCESS);
    }
  }
  
  /**
   * Check if a stage allows failure by checking if all jobs in the stage allow failure.
   *
   * @param stageExecutionId the stage execution ID
   * @return true if all jobs in the stage allow failure
   */
  private boolean stageAllowsFailure(UUID stageExecutionId) {
    StageExecutionEntity stageExecution = stageExecutionRepository.findById(stageExecutionId)
        .orElseThrow(() -> new IllegalArgumentException("Stage Execution not found"));
    
    List<JobExecutionEntity> jobs = jobExecutionRepository.findByStageExecution(stageExecution);
    
    // If there are no jobs, then we'll say the stage allows failure
    if (jobs.isEmpty()) {
      return true;
    }
    
    // Check if all jobs allow failure
    return jobs.stream().allMatch(JobExecutionEntity::isAllowFailure);
  }
  
  /**
   * Finalizes a pipeline execution.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   */
  @Transactional
  public void finalizePipelineExecution(UUID pipelineExecutionId) {
    PipelineLogger.info("Finalizing pipeline execution: " + pipelineExecutionId);
    updatePipelineStatus(pipelineExecutionId, ExecutionStatus.SUCCESS);
  }
}