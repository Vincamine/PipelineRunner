package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.backend.api.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.repository.PipelineExecutionRepository;

/**
 * Service for managing pipeline execution reports.
 */
@Service
public class PipelineReportService {
  private final PipelineExecutionRepository pipelineExecutionRepository;

  public PipelineReportService(PipelineExecutionRepository pipelineExecutionRepository) {
    this.pipelineExecutionRepository = pipelineExecutionRepository;
  }

  /**
   * Retrieves all pipelines.
   *
   * @return A list of pipeline names.
   */
  public List<String> getAllPipelines() {
    return pipelineExecutionRepository.findAll()
            .stream()
            .map(PipelineExecutionEntity::getPipelineName)
            .distinct()
            .collect(Collectors.toList());
  }

  /**
   * Retrieves executions for a specific pipeline.
   *
   * @param pipelineName The pipeline name.
   * @return A list of pipeline execution summaries.
   */
  public List<PipelineExecutionEntity> getPipelineExecutions(String pipelineName) {
    return pipelineExecutionRepository.findByPipelineName(pipelineName);
  }

  /**
   * Retrieves a specific pipeline execution by run ID.
   *
   * @param pipelineName The pipeline name.
   * @param runId The unique run ID.
   * @return The pipeline execution entity.
   */
  public PipelineExecutionEntity getPipelineExecution(String pipelineName, String runId) {
    return pipelineExecutionRepository.findByPipelineNameAndRunId(pipelineName, runId)
            .orElseThrow(() -> new RuntimeException("Pipeline execution not found."));
  }

  /**
   * Retrieves the latest execution of a pipeline.
   *
   * @param pipelineName The pipeline name.
   * @return The latest pipeline execution as a DTO.
   */
  public PipelineDTO getLatestPipelineRun(String pipelineName) {
    Optional<PipelineExecutionEntity> latestExecution = pipelineExecutionRepository
            .findTopByPipelineNameOrderByStartTimeDesc(pipelineName);

    return latestExecution.map(exec -> new PipelineDTO(exec.getPipelineName(), List.of()))
            .orElseThrow(() -> new RuntimeException("No executions found for pipeline: " + pipelineName));
  }
}
