package edu.neu.cs6510.sp25.t1.backend.service.pipeline;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.service.queue.StageExecutionQueueService;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for managing execution queues.
 * This includes queueing pipeline executions, stage executions, and processing them.
 */
@Service
@RequiredArgsConstructor
public class ExecutionQueueService {
  private final StageExecutionRepository stageExecutionRepository;
  private final StageExecutionQueueService stageExecutionQueueService;
  private final PipelineStatusService pipelineStatusService;
  
  /**
   * Process a pipeline execution from the queue.
   * This method is called by the queue service to process a pipeline execution.
   *
   * @param pipelineExecutionId ID of the pipeline execution to process
   */
  @Transactional
  public void processPipelineExecution(UUID pipelineExecutionId) {
    PipelineLogger.info("Processing pipeline execution: " + pipelineExecutionId);
    
    try {
      // Update pipeline status to RUNNING
      pipelineStatusService.updatePipelineStatus(pipelineExecutionId, ExecutionStatus.RUNNING);
      
      // Queue stages for execution
      queueStagesForExecution(pipelineExecutionId);
    } catch (Exception e) {
      PipelineLogger.error("Failed to process pipeline execution: " + e.getMessage());
      pipelineStatusService.updatePipelineStatus(pipelineExecutionId, ExecutionStatus.FAILED);
    }
  }
  
  /**
   * Queue stages for execution based on their order.
   *
   * @param pipelineExecutionId ID of the pipeline execution
   */
  private void queueStagesForExecution(UUID pipelineExecutionId) {
    List<StageExecutionEntity> stages = stageExecutionRepository.findByPipelineExecutionId(pipelineExecutionId);
    
    if (stages.isEmpty()) {
      PipelineLogger.error("No stages found for pipeline execution: " + pipelineExecutionId);
      throw new RuntimeException("No stages found for execution");
    }
    
    PipelineLogger.info("Queueing " + stages.size() + " stages for execution");
    
    // Sort stages by execution order
    stages.stream()
        .sorted((s1, s2) -> Integer.compare(s1.getExecutionOrder(), s2.getExecutionOrder()))
        .forEach(stage -> {
          // Add each stage to the stage execution queue
          stageExecutionQueueService.enqueueStageExecution(stage.getId());
          PipelineLogger.info("Stage queued for execution: " + stage.getId() + " (order: " + stage.getExecutionOrder() + ")");
        });
  }
}