package edu.neu.cs6510.sp25.t1.backend.service.queue;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import edu.neu.cs6510.sp25.t1.backend.config.ServiceLocator;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import jakarta.annotation.PostConstruct;

/**
 * Service for managing the stage execution queue.
 * This service is responsible for adding stage executions to the queue
 * and processing them in order.
 */
@Service
public class StageExecutionQueueService {
    
    private final ExecutionQueue<UUID> stageQueue;
    private final ApplicationContext applicationContext;
    
    @Autowired
    public StageExecutionQueueService(ApplicationContext applicationContext) {
        this.stageQueue = new ExecutionQueue<>();
        this.applicationContext = applicationContext;
    }
    
    /**
     * Initialize the stage queue processor.
     */
    @PostConstruct
    public void init() {
        stageQueue.setProcessor(this::processStageExecution);
    }
    
    /**
     * Adds a stage execution to the queue.
     * This is the middle part of the pipeline → stage → job hierarchy.
     *
     * @param stageExecutionId the ID of the stage execution to add to the queue
     */
    public void enqueueStageExecution(UUID stageExecutionId) {
        PipelineLogger.info("Adding stage execution to queue: " + stageExecutionId);
        
        // Verify the stage execution exists in the database before queuing
        try {
            boolean exists = ServiceLocator.getBean(edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository.class)
                .existsById(stageExecutionId);
            
            if (!exists) {
                PipelineLogger.error("Cannot queue stage execution that doesn't exist in database: " + stageExecutionId);
                return;
            }
            
            // All checks passed, add to queue
            stageQueue.enqueue(stageExecutionId);
        } catch (Exception e) {
            PipelineLogger.error("Error verifying stage execution before queuing: " + e.getMessage());
        }
    }
    
    /**
     * Processes a stage execution.
     *
     * @param stageExecutionId the ID of the stage execution to process
     */
    private void processStageExecution(UUID stageExecutionId) {
        try {
            PipelineLogger.info("Processing stage execution from queue: " + stageExecutionId);
            // Use ServiceLocator to get StageExecutionService to avoid circular dependency
            ServiceLocator.getBean(edu.neu.cs6510.sp25.t1.backend.service.StageExecutionService.class)
                .processStageExecution(stageExecutionId);
        } catch (Exception e) {
            PipelineLogger.error("Error processing stage execution: " + stageExecutionId + " - " + e.getMessage());
        }
    }
    
    /**
     * Returns the current size of the stage execution queue.
     *
     * @return the number of stage executions in the queue
     */
    public int getQueueSize() {
        return stageQueue.size();
    }
    
    /**
     * Checks if the stage queue is currently processing.
     *
     * @return true if the stage queue is processing, false otherwise
     */
    public boolean isProcessing() {
        return stageQueue.isProcessing();
    }
    
    /**
     * Clears the stage execution queue.
     */
    public void clearQueue() {
        stageQueue.clear();
    }
}