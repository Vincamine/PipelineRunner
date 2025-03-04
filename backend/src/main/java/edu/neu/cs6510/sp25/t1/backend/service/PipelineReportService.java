package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.backend.data.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.dto.PipelineReportDTO;
import edu.neu.cs6510.sp25.t1.backend.data.dto.StageExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.data.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.data.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Service for generating execution reports for pipelines, stages, and jobs.
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
   * Generates a full execution report for a pipeline.
   *
   * @param runId The unique run ID of the pipeline execution.
   * @return A PipelineReportDTO containing execution details.
   */
  public PipelineReportDTO generatePipelineExecutionReport(String runId) {
    PipelineExecutionEntity pipelineExecution = fetchPipelineExecution(runId);

    List<StageExecutionDTO> stageExecutions = fetchStageExecutions(runId);
    List<JobExecutionDTO> jobExecutions = fetchJobExecutions(runId);
    Map<String, Object> metrics = calculateExecutionMetrics(pipelineExecution, stageExecutions, jobExecutions);

    return new PipelineReportDTO(
            PipelineExecutionDTO.fromEntity(pipelineExecution),
            stageExecutions,
            jobExecutions,
            metrics
    );
  }

  /**
   * Fetches a pipeline execution entity by run ID.
   */
  private PipelineExecutionEntity fetchPipelineExecution(String runId) {
    return pipelineExecutionRepository.findByRunId(runId)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline execution not found for runId: " + runId));
  }

  /**
   * Fetches stage execution DTOs for a pipeline execution.
   */
  private List<StageExecutionDTO> fetchStageExecutions(String runId) {
    return stageExecutionRepository.findByPipelineExecutionRunId(runId)
            .stream()
            .map(StageExecutionDTO::fromEntity)
            .collect(Collectors.toList());
  }

  /**
   * Fetches job execution DTOs for a pipeline execution.
   */
  private List<JobExecutionDTO> fetchJobExecutions(String runId) {
    return jobExecutionRepository.findByStageExecution_PipelineExecution_RunId(runId)
            .stream()
            .map(JobExecutionDTO::fromEntity)
            .collect(Collectors.toList());
  }

  /**
   * Calculates execution metrics for a pipeline run.
   */
  private Map<String, Object> calculateExecutionMetrics(
          PipelineExecutionEntity pipelineExecution,
          List<StageExecutionDTO> stageExecutions,
          List<JobExecutionDTO> jobExecutions) {

    Map<String, Object> metrics = new HashMap<>();

    // Compute total execution time
    if (pipelineExecution.getCompletionTime() != null) {
      Duration duration = Duration.between(pipelineExecution.getStartTime(), pipelineExecution.getCompletionTime());
      metrics.put("totalExecutionTimeSeconds", duration.getSeconds());
    }

    // Count job statuses
    Map<ExecutionStatus, Long> jobStatusCounts = jobExecutions.stream()
            .collect(Collectors.groupingBy(JobExecutionDTO::getStatus, Collectors.counting()));
    metrics.put("jobStatusCounts", jobStatusCounts);

    // Count stage statuses
    Map<ExecutionStatus, Long> stageStatusCounts = stageExecutions.stream()
            .collect(Collectors.groupingBy(StageExecutionDTO::getStatus, Collectors.counting()));
    metrics.put("stageStatusCounts", stageStatusCounts);

    // Compute success rate
    long totalJobs = jobExecutions.size();
    long successfulJobs = jobStatusCounts.getOrDefault(ExecutionStatus.SUCCESS, 0L);
    double successRate = totalJobs > 0 ? ((double) successfulJobs / totalJobs) * 100 : 0;
    metrics.put("jobSuccessRate", successRate);

    // Identify slowest jobs
    metrics.put("slowestJobs", identifySlowestJobs(jobExecutions));

    return metrics;
  }

  /**
   * Identifies the slowest jobs in execution.
   */
  private List<Map<String, Object>> identifySlowestJobs(List<JobExecutionDTO> jobExecutions) {
    return jobExecutions.stream()
            .filter(job -> job.getCompletionTime() != null)
            .sorted(Comparator.comparing(job ->
                            Duration.between(job.getStartTime(), job.getCompletionTime()).toMillis(),
                    Comparator.reverseOrder()))
            .limit(3)
            .map(job -> {
              Map<String, Object> jobInfo = new HashMap<>();
              jobInfo.put("jobName", job.getJobName());
              jobInfo.put("stageName", job.getStageName());
              Duration jobDuration = Duration.between(job.getStartTime(), job.getCompletionTime());
              jobInfo.put("durationSeconds", jobDuration.getSeconds());
              return jobInfo;
            })
            .collect(Collectors.toList());
  }

  /**
   * Fetches the latest execution history of a pipeline.
   */
  public List<PipelineExecutionDTO> getRecentPipelineExecutions(int limit) {
    return pipelineExecutionRepository.findAll().stream()
            .sorted(Comparator.comparing(PipelineExecutionEntity::getStartTime).reversed())
            .limit(limit)
            .map(PipelineExecutionDTO::fromEntity)
            .collect(Collectors.toList());
  }

  /**
   * Fetches execution history for a specific pipeline.
   */
  public List<PipelineExecutionDTO> getPipelineExecutionHistory(String pipelineName, int limit) {
    return pipelineExecutionRepository.findByPipelineName(pipelineName).stream()
            .sorted(Comparator.comparing(PipelineExecutionEntity::getStartTime).reversed())
            .limit(limit)
            .map(PipelineExecutionDTO::fromEntity)
            .collect(Collectors.toList());
  }

  /**
   * Retrieves a specific pipeline execution by run ID.
   */
  public PipelineExecutionDTO getPipelineExecution(String pipelineName, String runId) {
    return pipelineExecutionRepository.findByPipelineNameAndRunId(pipelineName, runId)
            .map(PipelineExecutionDTO::fromEntity)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline execution not found"));
  }

  /**
   * Retrieves summary of a stage execution.
   */
  public StageExecutionDTO getStageExecutionSummary(String runId, String stageName) {
    return stageExecutionRepository.findByPipelineExecutionRunIdAndStageName(runId, stageName)
            .map(StageExecutionDTO::fromEntity)
            .orElseThrow(() -> new IllegalArgumentException("Stage execution not found"));
  }

  /**
   * Retrieves summary of a job execution.
   */
  public JobExecutionDTO getJobExecutionSummary(String runId, String stageName, String jobName) {
    return jobExecutionRepository.findByPipelineExecutionRunIdAndStageNameAndJobName(runId, stageName, jobName)
            .map(JobExecutionDTO::fromEntity)
            .orElseThrow(() -> new IllegalArgumentException("Job execution not found"));
  }
}
