package edu.neu.cs6510.sp25.t1.backend.service;

import edu.neu.cs6510.sp25.t1.backend.entity.PipelineExecution;
import edu.neu.cs6510.sp25.t1.backend.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.runtime.PipelineRunState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Service for handling pipeline execution logic.
 */
@Service
public class PipelineExecutionService {
  private static final Logger logger = LoggerFactory.getLogger(PipelineExecutionService.class);
  private final PipelineExecutionRepository pipelineExecutionRepository;

  public PipelineExecutionService(PipelineExecutionRepository pipelineExecutionRepository) {
    this.pipelineExecutionRepository = pipelineExecutionRepository;
  }

  /**
   * Starts execution for a given pipeline.
   *
   * @param pipelineName The pipeline name.
   * @return The execution summary if successful.
   */
  @Transactional
  public Optional<PipelineRunState> startPipeline(String pipelineName) {
    logger.info("Starting pipeline execution: {}", pipelineName);

    PipelineExecution pipelineExecution = new PipelineExecution(pipelineName, ExecutionState.RUNNING, Instant.now());
    pipelineExecutionRepository.save(pipelineExecution);

    return Optional.of(convertToPipelineRunState(pipelineExecution));
  }

  /**
   * Retrieves the execution details of a pipeline.
   *
   * @param pipelineName The pipeline name.
   * @return Optional containing pipeline execution state.
   */
  @Transactional(readOnly = true)
  public Optional<PipelineRunState> getPipelineExecution(String pipelineName) {
    return pipelineExecutionRepository.findFirstByPipelineNameOrderByCreatedAtDesc(pipelineName)
            .map(this::convertToPipelineRunState);
  }

  /**
   * Logs pipeline execution state updates.
   *
   * @param pipelineName The pipeline name.
   * @param state        The new execution state.
   */
  @Transactional
  public void logPipelineExecution(String pipelineName, String state) {
    Optional<PipelineExecution> pipelineExecutionOpt = pipelineExecutionRepository.findFirstByPipelineNameOrderByCreatedAtDesc(pipelineName);

    if (pipelineExecutionOpt.isPresent()) {
      PipelineExecution pipelineExecution = pipelineExecutionOpt.get();
      pipelineExecution.setState(ExecutionState.valueOf(state));
      pipelineExecutionRepository.save(pipelineExecution);
      logger.info("Updated pipeline execution state: {} -> {}", pipelineName, state);
    } else {
      logger.warn("Pipeline execution not found: {}", pipelineName);
    }
  }

  /**
   * Converts a PipelineExecution entity to a PipelineRunState.
   */
  private PipelineRunState convertToPipelineRunState(PipelineExecution execution) {
    return new PipelineRunState(
            execution.getPipelineName()
    );
  }
}
