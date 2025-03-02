package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import edu.neu.cs6510.sp25.t1.backend.dto.PipelineExecutionSummary;
import edu.neu.cs6510.sp25.t1.backend.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.repository.PipelineRepository;
import edu.neu.cs6510.sp25.t1.backend.executor.PipelineExecutor;
import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.runtime.PipelineRunState;

/**
 * Service for managing pipeline execution.
 */
@Service
public class PipelineExecutionService {
  private final PipelineRepository pipelineRepository;
  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final PipelineExecutor pipelineExecutor;

  /**
   * Constructor.
   * @param pipelineRepository The pipeline repository.
   * @param pipelineExecutionRepository The execution repository.
   * @param pipelineExecutor The executor responsible for pipeline execution.
   */
  public PipelineExecutionService(
          PipelineRepository pipelineRepository,
          PipelineExecutionRepository pipelineExecutionRepository,
          PipelineExecutor pipelineExecutor) {
    this.pipelineRepository = pipelineRepository;
    this.pipelineExecutionRepository = pipelineExecutionRepository;
    this.pipelineExecutor = pipelineExecutor;
  }

  /**
   * Starts a new pipeline execution.
   *
   * @param pipelineName The name of the pipeline.
   * @return A DTO representing the started pipeline execution.
   */
  @Transactional
  public Optional<PipelineExecutionSummary> startPipeline(String pipelineName) {
    return pipelineRepository.findById(pipelineName).map(pipeline -> {
      PipelineRunState execution = new PipelineRunState(pipelineName);
      execution.setState(ExecutionState.PENDING);

      execution = pipelineExecutionRepository.save(execution);

      // Execute pipeline asynchronously
      pipelineExecutor.executePipeline(execution);

      return PipelineExecutionSummary.fromEntity(execution);
    });
  }

  /**
   * Retrieves execution status of a pipeline.
   *
   * @param pipelineName The pipeline name.
   * @return A DTO with the execution status.
   */
  public Optional<PipelineExecutionSummary> getPipelineExecution(String pipelineName) {
    return pipelineExecutionRepository.findFirstByPipelineNameOrderByStartTimeDesc(pipelineName)
            .map(PipelineExecutionSummary::fromEntity);
  }
}
