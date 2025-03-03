package edu.neu.cs6510.sp25.t1.backend.service;

import edu.neu.cs6510.sp25.t1.backend.data.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.dto.PipelineReportDTO;
import edu.neu.cs6510.sp25.t1.backend.data.dto.StageExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.data.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.data.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating pipeline execution reports.
 */
@Service
public class PipelineReportService {

  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final StageExecutionRepository stageExecutionRepository;
  private final JobExecutionRepository jobExecutionRepository;

  public PipelineReportService(
          PipelineExecutionRepository pipelineExecutionRepository,
          StageExecutionRepository stageExecutionRepository,
          JobExecutionRepository jobExecutionRepository) {
    this.pipelineExecutionRepository = pipelineExecutionRepository;
    this.stageExecutionRepository = stageExecutionRepository;
    this.jobExecutionRepository = jobExecutionRepository;
  }

  /**
   * Generates a full report for a pipeline execution.
   *
   * @param runId The unique run ID of the pipeline execution.
   * @return A PipelineReportDTO containing complete execution details.
   */
  public PipelineReportDTO generateExecutionReport(String runId) {
    // Get pipeline execution
    PipelineExecutionEntity pipelineExecution = pipelineExecutionRepository.findByRunId(runId)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline execution not found for runId: " + runId));

    // Convert to DTO
    PipelineExecutionDTO pipelineExecutionDTO = PipelineExecutionDTO.fromEntity(pipelineExecution);

    // Get stage executions
    List<StageExecutionDTO> stageExecutions = stageExecutionRepository.findByPipelineExecutionRunId(runId)
            .stream()
            .map(StageExecutionDTO::fromEntity)
            .collect(Collectors.toList());

    // Get job executions
    List<JobExecutionDTO> jobExecutions = jobExecutionRepository.findByStageExecution_PipelineExecution_RunId(runId)
            .stream()
            .map(JobExecutionDTO::fromEntity)
            .collect(Collectors.toList());

    // Calculate metrics
    Map<String, Object> metrics = calculateExecutionMetrics(runId);

    // Create and return report
    return new PipelineReportDTO(
            pipelineExecutionDTO,
            stageExecutions,
            jobExecutions,
            metrics
    );
  }

  /**
   * Calculates execution metrics for a pipeline run.
   *
   * @param runId The unique run ID of the pipeline execution.
   * @return A map of metric names to values.
   */
  private Map<String, Object> calculateExecutionMetrics(String runId) {
    Map<String, Object> metrics = new HashMap<>();

    // Get entities from repositories
    PipelineExecutionEntity pipelineExecution = pipelineExecutionRepository.findByRunId(runId)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline execution not found for runId: " + runId));

    List<StageExecutionEntity> stageExecutions = stageExecutionRepository.findByPipelineExecutionRunId(runId);
    List<JobExecutionEntity> jobExecutions = jobExecutionRepository.findByStageExecution_PipelineExecution_RunId(runId);

    // Total execution time (if completed)
    if (pipelineExecution.getCompletionTime() != null) {
      Duration duration = Duration.between(pipelineExecution.getStartTime(), pipelineExecution.getCompletionTime());
      metrics.put("totalExecutionTimeSeconds", duration.getSeconds());
    }

    // Count jobs by status
    Map<ExecutionStatus, Long> jobStatusCounts = jobExecutions.stream()
            .collect(Collectors.groupingBy(JobExecutionEntity::getStatus, Collectors.counting()));
    metrics.put("jobStatusCounts", jobStatusCounts);

    // Count stages by status
    Map<ExecutionStatus, Long> stageStatusCounts = stageExecutions.stream()
            .collect(Collectors.groupingBy(StageExecutionEntity::getStatus, Collectors.counting()));
    metrics.put("stageStatusCounts", stageStatusCounts);

    // Success rate
    long totalJobs = jobExecutions.size();
    long successfulJobs = jobStatusCounts.getOrDefault(ExecutionStatus.SUCCESS, 0L);
    double successRate = totalJobs > 0 ? (double) successfulJobs / totalJobs * 100 : 0;
    metrics.put("jobSuccessRate", successRate);

    // Identify bottlenecks (jobs that took longest to execute)
    List<Map<String, Object>> slowestJobs = jobExecutions.stream()
            .filter(job -> job.getCompletionTime() != null) // Only completed jobs
            .sorted(Comparator.comparing(job ->
                            Duration.between(job.getStartTime(), job.getCompletionTime()).toMillis(),
                    Comparator.reverseOrder()))
            .limit(3) // Top 3 slowest
            .map(job -> {
              Map<String, Object> jobInfo = new HashMap<>();
              jobInfo.put("jobName", job.getJob().getName());
              jobInfo.put("stageName", job.getStageExecution().getStageName());
              Duration jobDuration = Duration.between(job.getStartTime(), job.getCompletionTime());
              jobInfo.put("durationSeconds", jobDuration.getSeconds());
              return jobInfo;
            })
            .collect(Collectors.toList());
    metrics.put("slowestJobs", slowestJobs);

    return metrics;
  }

  /**
   * Gets recent pipeline executions.
   *
   * @param limit The maximum number of executions to return.
   * @return List of recent pipeline execution DTOs.
   */
  public List<PipelineExecutionDTO> getRecentExecutions(int limit) {
    return pipelineExecutionRepository.findAll().stream()
            .sorted(Comparator.comparing(PipelineExecutionEntity::getStartTime).reversed())
            .limit(limit)
            .map(PipelineExecutionDTO::fromEntity)
            .collect(Collectors.toList());
  }

  /**
   * Gets execution history for a specific pipeline.
   *
   * @param pipelineName The name of the pipeline.
   * @param limit The maximum number of executions to return.
   * @return List of pipeline execution DTOs.
   */
  public List<PipelineExecutionDTO> getPipelineExecutionHistory(String pipelineName, int limit) {
    return pipelineExecutionRepository.findByPipelineName(pipelineName).stream()
            .sorted(Comparator.comparing(PipelineExecutionEntity::getStartTime).reversed())
            .limit(limit)
            .map(PipelineExecutionDTO::fromEntity)
            .collect(Collectors.toList());
  }

  // Get all pipelines in the repository
  public List<PipelineExecutionDTO> getAllPipelines() {
    return pipelineExecutionRepository.findAll().stream()
            .map(PipelineExecutionDTO::fromEntity)
            .collect(Collectors.toList());
  }

  // Get executions for a specific pipeline
  public List<PipelineExecutionDTO> getPipelineExecutions(String pipelineName) {
    return pipelineExecutionRepository.findByPipelineName(pipelineName).stream()
            .map(PipelineExecutionDTO::fromEntity)
            .collect(Collectors.toList());
  }

  // Get a specific pipeline execution by run ID
  public PipelineExecutionDTO getPipelineExecution(String pipelineName, String runId) {
    PipelineExecutionEntity pipelineExecution = pipelineExecutionRepository.findByPipelineNameAndRunId(pipelineName, runId)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline execution not found"));
    return PipelineExecutionDTO.fromEntity(pipelineExecution);
  }

  // Get the latest pipeline run
  public PipelineExecutionDTO getLatestPipelineRun(String pipelineName) {
    return pipelineExecutionRepository.findTopByPipelineNameOrderByStartTimeDesc(pipelineName)
            .map(PipelineExecutionDTO::fromEntity)
            .orElseThrow(() -> new IllegalArgumentException("No runs found for pipeline"));
  }

  // Get stage summary for a given pipeline execution
  public StageExecutionDTO getStageSummary(String pipelineName, String runId, String stageName) {
    StageExecutionEntity stageExecution = stageExecutionRepository.findByPipelineExecutionRunIdAndStageName(runId, stageName)
            .orElseThrow(() -> new IllegalArgumentException("Stage not found"));
    return StageExecutionDTO.fromEntity(stageExecution);
  }

  // Get job summary for a given pipeline execution and stage
  public JobExecutionDTO getJobSummary(String pipelineName, String runId, String stageName, String jobName) {
    JobExecutionEntity jobExecution = jobExecutionRepository.findByPipelineExecutionRunIdAndStageNameAndJobName(runId, stageName, jobName)
            .orElseThrow(() -> new IllegalArgumentException("Job not found"));
    return JobExecutionDTO.fromEntity(jobExecution);
  }
}