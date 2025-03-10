package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.common.dto.JobReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.StageReportDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Service class for generating reports based on pipeline, stage, and job executions.
 */
@Service
public class ReportService {

  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final StageExecutionRepository stageExecutionRepository;
  private final JobExecutionRepository jobExecutionRepository;

  /**
   * Constructor for **ReportService**.
   *
   * @param pipelineExecutionRepository pipeline execution repository
   * @param stageExecutionRepository    stage execution repository
   * @param jobExecutionRepository      job execution repository
   */
  public ReportService(PipelineExecutionRepository pipelineExecutionRepository,
                       StageExecutionRepository stageExecutionRepository,
                       JobExecutionRepository jobExecutionRepository) {
    this.pipelineExecutionRepository = pipelineExecutionRepository;
    this.stageExecutionRepository = stageExecutionRepository;
    this.jobExecutionRepository = jobExecutionRepository;
  }

  /**
   * Get a list of all available pipelines by giving no pipeline name.
   *
   * @return list of pipeline names
   */
  @Transactional(readOnly = true)
  public List<String> getAvailablePipelines() {
    return pipelineExecutionRepository.findAll().stream()
            .map(exec -> pipelineExecutionRepository.findPipelineNameByPipelineId(exec.getPipelineId())
                    .orElse("Unknown Pipeline"))
            .distinct() // Ensures only unique pipeline names
            .collect(Collectors.toList());
  }


  /**
   * Get a list of all pipeline reports for a given pipeline name.
   *
   * @param pipelineName pipeline name
   * @return list of pipeline reports
   */
  @Transactional(readOnly = true)
  public List<PipelineReportDTO> getPipelineReports(String pipelineName) {
    List<PipelineExecutionEntity> executions = pipelineExecutionRepository.findByPipelineNameOrderByStartTimeDesc(pipelineName);

    return executions.stream().map(exec -> {
      String fetchedPipelineName = pipelineExecutionRepository.findPipelineNameByPipelineId(exec.getPipelineId()).orElse(pipelineName);
      List<StageExecutionEntity> stages = stageExecutionRepository.findByPipelineExecutionId(exec.getId());

      ExecutionStatus pipelineStatus = calculatePipelineStatus(stages);

      return new PipelineReportDTO(
              exec.getId(),
              fetchedPipelineName,
              exec.getRunNumber(),
              exec.getCommitHash(),
              pipelineStatus,
              exec.getStartTime(),
              exec.getCompletionTime(),
              null // Not including detailed stage reports in summary
      );
    }).collect(Collectors.toList());
  }

  /**
   * Get a summary of a pipeline run by giving the pipeline name and run number.
   *
   * @param pipelineName pipeline name
   * @param runNumber    run number
   * @return pipeline report
   */
  @Transactional(readOnly = true)
  public PipelineReportDTO getPipelineRunSummary(String pipelineName, int runNumber) {
    PipelineExecutionEntity pipelineExecution = pipelineExecutionRepository.findByPipelineIdAndRunNumber(
            pipelineExecutionRepository.findPipelineIdByName(pipelineName)
                    .orElseThrow(() -> new IllegalArgumentException("Pipeline not found: " + pipelineName)), runNumber
    ).orElseThrow(() -> new IllegalArgumentException("Pipeline execution not found for run: " + runNumber));

    List<StageExecutionEntity> stages = stageExecutionRepository.findByPipelineExecutionId(pipelineExecution.getId());

    List<StageReportDTO> stageReports = stages.stream()
            .map(this::createStageReport)
            .collect(Collectors.toList());

    ExecutionStatus pipelineStatus = calculatePipelineStatus(stages);

    return new PipelineReportDTO(
            pipelineExecution.getId(),
            pipelineName,
            pipelineExecution.getRunNumber(),
            pipelineExecution.getCommitHash(),
            pipelineStatus,
            pipelineExecution.getStartTime(),
            pipelineExecution.getCompletionTime(),
            stageReports
    );
  }

  /**
   * Get a detailed report of a stage by giving the pipeline name, run number, and stage name.
   *
   * @param pipelineName pipeline name
   * @param runNumber    run number
   * @param stageName    stage name
   * @return stage report
   */
  @Transactional(readOnly = true)
  public StageReportDTO getStageReport(String pipelineName, int runNumber, String stageName) {
    UUID pipelineId = pipelineExecutionRepository.findPipelineIdByName(pipelineName)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline not found: " + pipelineName));

    PipelineExecutionEntity pipelineExecution = pipelineExecutionRepository.findByPipelineIdAndRunNumber(pipelineId, runNumber)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline execution not found for run: " + runNumber));

    List<StageExecutionEntity> stages = stageExecutionRepository.findByPipelineExecutionIdAndStageNameOrderByStartTimeDesc(pipelineExecution.getId(), stageName);

    if (stages.isEmpty()) {
      throw new IllegalArgumentException("No executions found for stage: " + stageName);
    }

    return createStageReport(stages.getFirst()); // Get the most recent execution
  }

  /**
   * Get a detailed report of a job by giving the pipeline name, run number, stage name, and job name.
   *
   * @param pipelineName pipeline name
   * @param runNumber    run number
   * @param stageName    stage name
   * @param jobName      job name
   * @return job report
   */
  @Transactional(readOnly = true)
  public JobReportDTO getJobReport(String pipelineName, int runNumber, String stageName, String jobName) {
    UUID pipelineId = pipelineExecutionRepository.findPipelineIdByName(pipelineName)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline not found: " + pipelineName));

    PipelineExecutionEntity pipelineExecution = pipelineExecutionRepository.findByPipelineIdAndRunNumber(pipelineId, runNumber)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline execution not found for run: " + runNumber));

    List<StageExecutionEntity> stages = stageExecutionRepository.findByPipelineExecutionIdAndStageNameOrderByStartTimeDesc(pipelineExecution.getId(), stageName);

    if (stages.isEmpty()) {
      throw new IllegalArgumentException("Stage execution not found for stage: " + stageName);
    }

    List<JobExecutionEntity> jobs = jobExecutionRepository.findByStageExecutionIdAndJobNameOrderByStartTimeDesc(stages.getFirst().getId(), jobName);

    if (jobs.isEmpty()) {
      throw new IllegalArgumentException("Job execution not found for job: " + jobName);
    }

    JobExecutionEntity job = jobs.getFirst(); // Get the most recent execution
    return new JobReportDTO(
            jobExecutionRepository.findJobNameByJobId(job.getJobId()).orElse(jobName),
            List.of(new JobReportDTO.ExecutionRecord(
                    job.getId(),
                    job.getStatus(),
                    job.getStartTime(),
                    job.getCompletionTime(),
                    job.isAllowFailure()
            ))
    );
  }

  /**
   * Helper method to create a stage report from a stage execution entity.
   *
   * @param stage stage execution entity
   * @return stage report
   */
  private StageReportDTO createStageReport(StageExecutionEntity stage) {
    List<JobExecutionEntity> jobs = jobExecutionRepository.findByStageExecutionId(stage.getId());

    List<JobReportDTO> jobReports = jobs.stream()
            .map(job -> new JobReportDTO(
                    jobExecutionRepository.findJobNameByJobId(job.getJobId()).orElse("Unknown Job"),
                    List.of(new JobReportDTO.ExecutionRecord(
                            job.getId(),
                            job.getStatus(),
                            job.getStartTime(),
                            job.getCompletionTime(),
                            job.isAllowFailure()
                    ))
            )).toList();

    return new StageReportDTO(
            stage.getId(),
            stageExecutionRepository.findStageNameByStageId(stage.getStageId()).orElse("Unknown Stage"),
            stage.getStatus(),
            stage.getStartTime(),
            stage.getCompletionTime(),
            jobReports
    );
  }

  /**
   * Calculate the pipeline status based on the statuses of its stages.
   *
   * @param stages list of stage execution entities
   * @return pipeline status
   */
  private ExecutionStatus calculatePipelineStatus(List<StageExecutionEntity> stages) {
    boolean hasFailed = false;
    boolean hasCanceled = false;

    for (StageExecutionEntity stage : stages) {
      if (stage.getStatus() == ExecutionStatus.FAILED) {
        hasFailed = true;
      }
      if (stage.getStatus() == ExecutionStatus.CANCELED) {
        hasCanceled = true;
      }
    }

    if (hasFailed) {
      return ExecutionStatus.FAILED;
    }
    if (hasCanceled) {
      return ExecutionStatus.CANCELED;
    }
    return ExecutionStatus.SUCCESS;
  }
}
