package edu.neu.cs6510.sp25.t1.backend.executor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import edu.neu.cs6510.sp25.t1.backend.entity.PipelineExecution;
import edu.neu.cs6510.sp25.t1.backend.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.common.execution.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.runtime.PipelineRunState;

/**
 * Responsible for executing pipelines, tracking execution progress,
 * and updating the execution status in the database.
 */
@Service
public class PipelineExecutor {

  private final PipelineExecutionRepository pipelineExecutionRepository;

  public PipelineExecutor(PipelineExecutionRepository pipelineExecutionRepository) {
    this.pipelineExecutionRepository = pipelineExecutionRepository;
  }

  /**
   * Starts execution for a given pipeline.
   *
   * @param pipelineRunState The pipeline execution object.
   */
  @Transactional
  public void executePipeline(PipelineRunState pipelineRunState) {
    PipelineExecution pipelineExecution = new PipelineExecution(
            pipelineRunState.getPipelineName(),
            ExecutionState.RUNNING, // ✅ Pass `ExecutionState` enum instead of `String`
            Instant.now() // ✅ Provide the missing `createdAt` timestamp
    );

    pipelineExecutionRepository.save(pipelineExecution);

    // TODO: Implement logic for running pipeline stages and jobs

    // For now, assume execution completes successfully
    pipelineExecution.setState(ExecutionState.SUCCESS);
    pipelineExecutionRepository.save(pipelineExecution);
  }


  /**
   * Updates the execution state of a pipeline.
   *
   * @param executionId The execution ID.
   * @param newState    The new execution state.
   */
  @Transactional
  public void updatePipelineExecutionState(Long executionId, ExecutionState newState) {
    pipelineExecutionRepository.findById(executionId).ifPresent(execution -> {
      execution.setState(newState);
      pipelineExecutionRepository.save(execution);
    });
  }
}
