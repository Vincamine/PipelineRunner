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
   * Retrieves past pipeline runs for a repository.
   */
  @Transactional(readOnly = true)
  public List<PipelineReportDTO> getPipelineReports(String pipelineName) {
    List<PipelineExecutionEntity> executions = pipelineExecutionRepository.findByPipelineNameOrderByStartTimeDesc(pipelineName);
    return executions.stream().map(exec -> new PipelineReportDTO(
            exec.getId(), exec.getRunNumber(), exec.getCommitHash(), exec.getStatus(), exec.getStartTime(), exec.getCompletionTime()
    )).collect(Collectors.toList());
  }

  /**
   * Retrieves the summary of a stage for a specific pipeline execution.
   */
  @Transactional(readOnly = true)
  public StageReportDTO getStageReport(UUID pipelineExecutionId, String stageName) {
    List<StageExecutionEntity> stages = stageExecutionRepository.findByPipelineExecutionIdAndStageNameOrderByStartTimeDesc(pipelineExecutionId, stageName);

    List<StageReportDTO.ExecutionRecord> records = stages.stream()
            .map(stage -> new StageReportDTO.ExecutionRecord(
                    stage.getId(),
                    stage.getStatus(),
                    stage.getStartTime(),
                    stage.getCompletionTime()
            )).collect(Collectors.toList());

    return new StageReportDTO(stageName, records);
  }

  /**
   * Retrieves the summary of a job for a specific stage execution.
   */
  @Transactional(readOnly = true)
  public JobReportDTO getJobReport(UUID stageExecutionId, String jobName) {
    List<JobExecutionEntity> jobs = jobExecutionRepository.findByStageExecutionIdAndJobNameOrderByStartTimeDesc(stageExecutionId, jobName);

    List<JobReportDTO.ExecutionRecord> records = jobs.stream()
            .map(job -> new JobReportDTO.ExecutionRecord(
                    job.getId(),
                    job.getStatus(),
                    job.getStartTime(),
                    job.getCompletionTime(),
                    job.isAllowFailure()
            )).collect(Collectors.toList());

    return new JobReportDTO(jobName, records);
  }
}
