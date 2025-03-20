package edu.neu.cs6510.sp25.t1.backend.service.queue;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import edu.neu.cs6510.sp25.t1.backend.service.StageExecutionService;
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
    private StageExecutionService stageExecutionService;
    private final ApplicationContext applicationContext;
    
    @Autowired
    public StageExecutionQueueService(ApplicationContext applicationContext) {
        this.stageQueue = new ExecutionQueue<>();
        this.applicationContext = applicationContext;
    }
    
    /**
     * Initialize the stage queue processor.
     * Gets the StageExecutionService lazily to break the circular dependency.
     */
    @PostConstruct
    public void init() {
        // Get the StageExecutionService lazily from the ApplicationContext
        this.stageExecutionService = applicationContext.getBean(StageExecutionService.class);
        stageQueue.setProcessor(this::processStageExecution);
    }
    
    /**
     * Adds a stage execution to the queue.
     *
     * @param stageExecutionId the ID of the stage execution to add to the queue
     */
    public void enqueueStageExecution(UUID stageExecutionId) {
        PipelineLogger.info("Adding stage execution to queue: " + stageExecutionId);
        stageQueue.enqueue(stageExecutionId);
    }
    
    /**
     * Processes a stage execution.
     *
     * @param stageExecutionId the ID of the stage execution to process
     */
    private void processStageExecution(UUID stageExecutionId) {
        try {
            PipelineLogger.info("Processing stage execution from queue: " + stageExecutionId);
            stageExecutionService.processStageExecution(stageExecutionId);
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