package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.data.repository.PipelineExecutionRepository;

/**
 * Service for managing pipeline execution data.
 */
@Service
public class PipelineService {
  private final PipelineExecutionRepository pipelineExecutionRepository;

  public PipelineService(PipelineExecutionRepository pipelineExecutionRepository) {
    this.pipelineExecutionRepository = pipelineExecutionRepository;
  }

  /**
   * Retrieves all unique pipeline names from the database.
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
   * Retrieves all executions for a specific pipeline.
   *
   * @param pipelineName The name of the pipeline.
   * @return A list of pipeline execution entities.
   */
  public List<PipelineExecutionEntity> getPipelineExecutions(String pipelineName) {
    return pipelineExecutionRepository.findByPipelineName(pipelineName);
  }

  /**
   * Retrieves a specific execution of a pipeline by its run ID.
   *
   * @param pipelineName The name of the pipeline.
   * @param runId        The execution ID.
   * @return The execution entity, if found.
   */
  public PipelineExecutionEntity getPipelineExecution(String pipelineName, Long runId) {
    return pipelineExecutionRepository.findByPipelineNameAndId(pipelineName, runId)
            .orElseThrow(() -> new RuntimeException("Pipeline execution not found for " + pipelineName + " run " + runId));
  }

  /**
   * Retrieves the latest execution of a given pipeline.
   *
   * @param pipelineName The name of the pipeline.
   * @return The latest execution entity, if available.
   */
  public PipelineExecutionEntity getLatestPipelineExecution(String pipelineName) {
    return pipelineExecutionRepository.findTopByPipelineNameOrderByCreatedAtDesc(pipelineName)
            .orElseThrow(() -> new RuntimeException("No executions found for pipeline: " + pipelineName));
  }
}
