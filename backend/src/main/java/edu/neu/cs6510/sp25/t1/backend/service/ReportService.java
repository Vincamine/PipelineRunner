package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.StageReportDTO;
import edu.neu.cs6510.sp25.t1.common.dto.JobReportDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

@Service
public class ReportService {

  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final StageExecutionRepository stageExecutionRepository;
  private final JobExecutionRepository jobExecutionRepository;

  public ReportService(PipelineExecutionRepository pipelineExecutionRepository,
                       StageExecutionRepository stageExecutionRepository,
                       JobExecutionRepository jobExecutionRepository) {
    this.pipelineExecutionRepository = pipelineExecutionRepository;
    this.stageExecutionRepository = stageExecutionRepository;
    this.jobExecutionRepository = jobExecutionRepository;
  }

  /**
   * Given no inputs, return a **list of pipeline names** for which reports are available.
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
   * Given a **pipeline name**, return a summary of all executions.
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
   * Given a **pipeline name and a run number**, return a summary of that specific run.
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
   * Given a **pipeline name, run number, and stage name**, return the report of that stage.
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
   * Given a **pipeline name, run number, stage name, and job name**, return the report for that job.
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
   * Helper method to create a **StageReportDTO** from a `StageExecutionEntity`.
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
   * Determines the overall **pipeline status** based on all stage executions.
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
