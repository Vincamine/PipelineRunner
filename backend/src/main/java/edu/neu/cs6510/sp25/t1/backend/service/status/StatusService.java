package edu.neu.cs6510.sp25.t1.backend.service.status;

import edu.neu.cs6510.sp25.t1.backend.database.repository.*;
import edu.neu.cs6510.sp25.t1.backend.database.entity.*;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatusService {
  private final PipelineRepository pipelineRepository;
  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final StageRepository stageRepository;
  private final StageExecutionRepository stageExecutionRepository;
  private final JobRepository jobRepository;
  private final JobExecutionRepository jobExecutionRepository;

  public StatusService(PipelineRepository pipelineRepository,
                       PipelineExecutionRepository pipelineExecutionRepository,
                       StageRepository stageRepository,
                       StageExecutionRepository stageExecutionRepository,
                       JobRepository jobRepository,
                       JobExecutionRepository jobExecutionRepository) {
    this.pipelineRepository = pipelineRepository;
    this.pipelineExecutionRepository = pipelineExecutionRepository;
    this.stageRepository = stageRepository;
    this.stageExecutionRepository = stageExecutionRepository;
    this.jobRepository = jobRepository;
    this.jobExecutionRepository = jobExecutionRepository;
  }

  public Map<String, Object> getStatusForPipeline(String pipelineName) {
    Map<String, Object> result = new LinkedHashMap<>();

    Optional<PipelineEntity> pipelineOpt = pipelineRepository.findByName(pipelineName);
    if (pipelineOpt.isEmpty()) {
      PipelineLogger.error("Pipeline not found: " + pipelineName);
      throw new IllegalArgumentException("Pipeline not found: " + pipelineName);
    }

    UUID pipelineId = pipelineOpt.get().getId();

    // Fetch the latest execution for this pipeline
    Optional<PipelineExecutionEntity> executionOpt = pipelineExecutionRepository
        .findByPipelineId(pipelineId);

    if (executionOpt.isEmpty()) {
      PipelineLogger.error("PipelineExecution not found: " + pipelineName);
      throw new IllegalArgumentException("PipelineExecution not found: " + pipelineName);
    }
    // retrieve pipeline execution ID for look up the status and set status
    UUID pipelineExecutionId = executionOpt.get().getId();

    List<StageEntity> stages = stageRepository.findByPipelineId(pipelineId);

    // put pipelineName
    result.put("pipeline", pipelineName);
    // create List<Map<String, Object>> for all stages
    List<Map<String, Object>> stageResults = new ArrayList<>();

    // sum of the pipeline status
    ExecutionStatus sumOfPipelineStatus = ExecutionStatus.SUCCESS;

    // look for each stage
    for (StageEntity stage : stages) {
      //Linked hash Map for each stage
      Map<String, Object> stageResult = new LinkedHashMap<>();

      UUID stageID = stage.getId();
      Optional<StageExecutionEntity> stageExecution = stageExecutionRepository
          .findByStageIdAndPipelineExecutionId(
              stageID,
              pipelineExecutionId
          );
      // put stage Name
      stageResult.put("stage", stage.getName());

      List<JobEntity> jobs = jobRepository.findByStageId(stageID);
      ExecutionStatus stageStatus;
      if (stageExecution.isPresent()) {
        // put stage Execution ID
        stageResult.put("stageExecution", stageExecution.get().getId());
      } else {
        PipelineLogger.error("StageExecution not found: " + pipelineName);
        throw new IllegalArgumentException("StageExecution not found: " + pipelineName);
      }
      ExecutionStatus sumOfStatus = ExecutionStatus.SUCCESS;

      // use List<Map<String, Object>> to save all jobs
      List<Map<String, Object>> jobResults = new ArrayList<>();

      for (JobEntity job : jobs) {
        // create Linked Hash Map for each Job
        Map<String, Object> jobResult = new LinkedHashMap<>();

        // put job Name
        jobResult.put("job", job.getName());

        UUID jobID = job.getId();
        Optional<JobExecutionEntity> jobExecute = jobExecutionRepository.findByJobId(jobID);
        ExecutionStatus jobStatus;
        if (jobExecute.isPresent()) {
          jobStatus = jobExecute.get().getStatus();
          // put job Execution ID
          jobResult.put("jobExecution", jobExecute.get().getId());
          // put Job Status
          jobResult.put("jobExecutionStatus", jobStatus);

        } else {
          PipelineLogger.error("JobExecution not found: " + pipelineName);
          throw new IllegalArgumentException("JobExecution not found: " + pipelineName);
        }
        if (!jobStatus.equals(ExecutionStatus.SUCCESS)) {
          sumOfStatus = jobStatus;
        }
        jobResults.add(jobResult);
      }
      // sum all job status and put to the map
      stageResult.put("stageExecutionStatus", sumOfStatus);
      // put all jobResult to the map
      stageResult.put("jobs", jobResults);
      // update Stage Execution
      if (!sumOfStatus.equals(stageExecution.get().getStatus())) {
        stageExecution.get().updateState(sumOfStatus);
      }

      if (!sumOfStatus.equals(sumOfPipelineStatus)){
        sumOfPipelineStatus = sumOfStatus;
      }
      stageResults.add(stageResult);
      // save and flush the stage repository
      stageExecutionRepository.saveAndFlush(stageExecution.get());
    }

    // put all stage Result to the pipeline Map
    result.put("pipelineStatus", sumOfPipelineStatus);
    // put all stage result to the map
    result.put("stageResult", stageResults);

    // save to pipeline repo
    executionOpt.get().setStatus(sumOfPipelineStatus);
    // save and flush the pipeline execution repository
    pipelineExecutionRepository.saveAndFlush(executionOpt.get());

    return result;
  }
}
