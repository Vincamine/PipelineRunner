package edu.neu.cs6510.sp25.t1.backend.service.queue;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import jakarta.annotation.PostConstruct;

/**
 * Service for managing the pipeline execution queue.
 * This service is responsible for adding pipeline executions to the queue
 * and processing them in order.
 */
@Service
public class PipelineExecutionQueueService {
    
    private final ExecutionQueue<UUID> pipelineQueue;
    private final PipelineExecutionService pipelineExecutionService;
    
    @Autowired
    public PipelineExecutionQueueService(
            PipelineExecutionService pipelineExecutionService) {
        this.pipelineQueue = new ExecutionQueue<>();
        this.pipelineExecutionService = pipelineExecutionService;
    }
    
    /**
     * Initialize the pipeline queue processor.
     */
    @PostConstruct
    public void init() {
        pipelineQueue.setProcessor(this::processPipelineExecution);
    }
    
    /**
     * Adds a pipeline execution to the queue.
     *
     * @param pipelineExecutionId the ID of the pipeline execution to add to the queue
     */
    public void enqueuePipelineExecution(UUID pipelineExecutionId) {
        PipelineLogger.info("Adding pipeline execution to queue: " + pipelineExecutionId);
        pipelineQueue.enqueue(pipelineExecutionId);
    }
    
    /**
     * Processes a pipeline execution.
     *
     * @param pipelineExecutionId the ID of the pipeline execution to process
     */
    private void processPipelineExecution(UUID pipelineExecutionId) {
        try {
            PipelineLogger.info("Processing pipeline execution from queue: " + pipelineExecutionId);
            pipelineExecutionService.processPipelineExecution(pipelineExecutionId);
        } catch (Exception e) {
            PipelineLogger.error("Error processing pipeline execution: " + pipelineExecutionId + " - " + e.getMessage());
        }
    }
    
    /**
     * Returns the current size of the pipeline execution queue.
     *
     * @return the number of pipeline executions in the queue
     */
    public int getQueueSize() {
        return pipelineQueue.size();
    }
    
    /**
     * Checks if the pipeline queue is currently processing.
     *
     * @return true if the pipeline queue is processing, false otherwise
     */
    public boolean isProcessing() {
        return pipelineQueue.isProcessing();
    }
    
    /**
     * Clears the pipeline execution queue.
     */
    public void clearQueue() {
        pipelineQueue.clear();
    }
}