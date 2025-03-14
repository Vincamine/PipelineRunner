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
  private final PipelineRepository pipelineRepository;
  private final PipelineExecutionMapper pipelineExecutionMapper;
  private final StageExecutionService stageExecutionService;
  private final StageExecutionRepository stageExecutionRepository;
  private final StageRepository stageRepository;
  private final JobExecutionRepository jobExecutionRepository;
  private final JobRepository jobRepository;
  private final JobScriptRepository jobScriptRepository;

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
   * Creates or retrieves an existing pipeline entity based on the request.
   *
   * @param request the pipeline execution request 
   * @param pipelineConfig the pipeline configuration from YAML
   * @return the ID of the pipeline entity
   */
  @Transactional
  protected UUID createOrGetPipelineEntity(PipelineExecutionRequest request, Map<String, Object> pipelineConfig) {
    // Check if a pipeline ID was provided in the request
    if (request.getPipelineId() != null) {
      // Check if the pipeline exists in the database
      if (pipelineRepository.existsById(request.getPipelineId())) {
        PipelineLogger.info("Using existing pipeline with ID: " + request.getPipelineId());
        return request.getPipelineId();
      }
    }
    
    // No valid pipeline ID provided or pipeline doesn't exist, create a new one
    PipelineLogger.info("Creating new pipeline entity from YAML configuration");
    
    // Extract pipeline properties from config
    String name = pipelineConfig.containsKey("name") 
        ? (String) pipelineConfig.get("name") 
        : "pipeline-" + UUID.randomUUID().toString().substring(0, 8);
        
    String repoUrl = pipelineConfig.containsKey("repository") 
        ? (String) pipelineConfig.get("repository") 
        : "local-repository";
        
    String branch = pipelineConfig.containsKey("branch") 
        ? (String) pipelineConfig.get("branch") 
        : "main";
    
    // Create and save pipeline entity
    PipelineEntity pipeline = PipelineEntity.builder()
        .name(name)
        .repositoryUrl(repoUrl)
        .branch(branch)
        .commitHash(request.getCommitHash())
        .build();
    
    pipeline = pipelineRepository.save(pipeline);
    pipelineRepository.flush();
    
    PipelineLogger.info("Created new pipeline entity with ID: " + pipeline.getId());
    return pipeline.getId();
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
    
    Object stagesObj = pipelineConfig.get("stages");
    if (!(stagesObj instanceof List<?> rawStages)) {
      PipelineLogger.error("Invalid pipeline structure: 'stages' must be a list.");
      throw new RuntimeException("Invalid pipeline configuration: 'stages' key is missing or incorrect.");
    }
    
    List<Map<String, Object>> stages = rawStages.stream()
        .filter(item -> item instanceof Map)
        .map(item -> (Map<String, Object>) item)
        .toList();
    
    if (stages.isEmpty()) {
      PipelineLogger.error("No valid stages found in pipeline configuration!");
      throw new RuntimeException("Pipeline must contain at least one valid stage.");
    }
    
    for (int order = 0; order < stages.size(); order++) {
      Map<String, Object> stageConfig = stages.get(order);
      String stageName = (String) stageConfig.get("name");
      
      // Create and save the stage entity
      StageEntity stage = StageEntity.builder()
          .pipelineId(pipelineId)
          .name(stageName)
          .executionOrder(order)
          .build();
      
      stage = stageRepository.save(stage);
      stageRepository.flush();
      
      // Create and save job entities for this stage
      createJobDefinitions(stage.getId(), stageConfig);
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
    List<Map<String, Object>> jobsConfig = (List<Map<String, Object>>) stageConfig.get("jobs");
    
    if (jobsConfig == null || jobsConfig.isEmpty()) {
      PipelineLogger.warn("No jobs defined in YAML for stage: " + stageId);
      return;
    }
    
    for (Map<String, Object> jobConfig : jobsConfig) {
      String jobName = (String) jobConfig.get("name");
      String dockerImage = jobConfig.containsKey("image") 
          ? (String) jobConfig.get("image") 
          : "docker.io/library/alpine:latest";
      
      boolean allowFailure = jobConfig.containsKey("allow_failure") 
          ? (boolean) jobConfig.get("allow_failure") 
          : false;
      
      // Create and save the job entity
      JobEntity job = JobEntity.builder()
          .stageId(stageId)
          .name(jobName)
          .dockerImage(dockerImage)
          .allowFailure(allowFailure)
          .build();
      
      job = jobRepository.save(job);
      jobRepository.flush();
      
      // Handle job scripts if present
      if (jobConfig.containsKey("script")) {
        Object scriptObj = jobConfig.get("script");
        if (scriptObj instanceof String) {
          // Single script line
          jobScriptRepository.saveScript(job.getId(), (String) scriptObj);
        } else if (scriptObj instanceof List) {
          // Multiple script lines
          List<String> scripts = ((List<?>) scriptObj).stream()
              .filter(s -> s instanceof String)
              .map(s -> (String) s)
              .toList();
          
          for (String script : scripts) {
            jobScriptRepository.saveScript(job.getId(), script);
          }
        }
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

    for (int order = 0; order < stages.size(); order++) {
      // Find the matching stage entity for this order
      int finalOrder = order;
      StageEntity matchingStage = pipelineStages.stream()
              .filter(stage -> stage.getExecutionOrder() == finalOrder)
              .findFirst()
              .orElseThrow(() -> new RuntimeException("Stage definition not found for order: " + finalOrder));
      
      // Get pipeline execution to retrieve commit hash
      PipelineExecutionEntity pipelineExec = pipelineExecutionRepository.findById(pipelineExecutionId)
              .orElseThrow(() -> new RuntimeException("Pipeline execution not found"));
      
      // Create the stage execution entity
      StageExecutionEntity stageExecution = StageExecutionEntity.builder()
              .pipelineExecutionId(pipelineExecutionId)
              .stageId(matchingStage.getId())  // Use actual stage ID 
              .executionOrder(order)
              .commitHash(pipelineExec.getCommitHash())  // Use commit hash from pipeline execution
              .isLocal(pipelineExec.isLocal())        // Use isLocal from pipeline execution
              .status(ExecutionStatus.PENDING)
              .startTime(Instant.now())
              .build();

      // Save the stage execution
      stageExecution = stageExecutionRepository.save(stageExecution);
      stageExecutionRepository.flush();  
      PipelineLogger.info("Saved stage execution with ID: " + stageExecution.getId() + " for stage: " + matchingStage.getId());

      // Extract and create job executions
      List<JobExecutionEntity> jobs = createJobExecutions(matchingStage.getId(), stageExecution);

      // Save job executions
      if (!jobs.isEmpty()) {
        PipelineLogger.info("Saving " + jobs.size() + " jobs for stage: " + stageExecution.getId());
        jobs = jobExecutionRepository.saveAll(jobs);
        jobExecutionRepository.flush(); 

        PipelineLogger.info("✅ Saved stage execution: " + stageExecution.getId() + " with " + jobs.size() + " jobs.");
      } else {
        PipelineLogger.warn("⚠ No jobs defined for stage: " + stageExecution.getId());
      }
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
  
  /**
   * Saves a script for a job directly to the database.
   * Since we don't have a dedicated JobScriptRepository yet, we're implementing this method
   * directly in the service.
   *
   * @param jobId  the job ID
   * @param script the script content
   */
  @Transactional
  private void saveJobScript(UUID jobId, String script) {
    if (script == null || script.trim().isEmpty()) {
      PipelineLogger.warn("Attempted to save empty script for job: " + jobId);
      return;
    }
    
    try {
      // Execute a direct SQL query to insert the script
      // In a real implementation, this should use a proper repository
      String insertQuery = "INSERT INTO job_scripts (job_id, script) VALUES (?, ?)";
      
      // Use JdbcTemplate or EntityManager to execute the query
      // For demonstration purposes, we'll just log that we would save the script
      PipelineLogger.info("Would save script for job " + jobId + ": " + script.substring(0, Math.min(30, script.length())) + "...");
      
      // In a real implementation, you would include code similar to:
      // jdbcTemplate.update(insertQuery, jobId, script);
    } catch (Exception e) {
      PipelineLogger.error("Failed to save script for job " + jobId + ": " + e.getMessage());
    }
  }
}