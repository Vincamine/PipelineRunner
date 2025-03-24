package edu.neu.cs6510.sp25.t1.backend.service.queue;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import edu.neu.cs6510.sp25.t1.backend.config.ServiceLocator;
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
    private final ApplicationContext applicationContext;
    
    @Autowired
    public PipelineExecutionQueueService(ApplicationContext applicationContext) {
        this.pipelineQueue = new ExecutionQueue<>();
        this.applicationContext = applicationContext;
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
     * This is the entry point for the pipeline → stage → job hierarchy of queues.
     *
     * @param pipelineExecutionId the ID of the pipeline execution to add to the queue
     */
    public void enqueuePipelineExecution(UUID pipelineExecutionId) {
        PipelineLogger.info("Adding pipeline execution to queue: " + pipelineExecutionId);
        
        // Verify the pipeline execution exists in the database before queuing
        try {
            // Get repositories
            var pipelineExecRepo = ServiceLocator.getBean(edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository.class);
            var stageExecRepo = ServiceLocator.getBean(edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository.class);
            var jobExecRepo = ServiceLocator.getBean(edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository.class);
            
            // Verify pipeline execution exists
            if (!pipelineExecRepo.existsById(pipelineExecutionId)) {
                PipelineLogger.error("Cannot queue pipeline execution that doesn't exist in database: " + pipelineExecutionId);
                throw new IllegalArgumentException("Pipeline execution not found: " + pipelineExecutionId);
            }
            
            // Verify that stages exist for this pipeline execution
            var stages = stageExecRepo.findByPipelineExecutionId(pipelineExecutionId);
            if (stages.isEmpty()) {
                PipelineLogger.error("Cannot queue pipeline execution with no stages: " + pipelineExecutionId);
                throw new IllegalArgumentException("Pipeline execution has no stages: " + pipelineExecutionId);
            }
            
            // Verify that jobs exist for these stages
            boolean hasJobs = false;
            for (var stage : stages) {
                var jobs = jobExecRepo.findByStageExecution(stage);
                if (!jobs.isEmpty()) {
                    hasJobs = true;
                    break;
                }
            }
            
            if (!hasJobs) {
                PipelineLogger.error("Cannot queue pipeline execution with no jobs: " + pipelineExecutionId);
                throw new IllegalArgumentException("Pipeline execution has no jobs: " + pipelineExecutionId);
            }
            
            PipelineLogger.info("Verification successful. Pipeline execution has all required entities.");
            
            // All checks passed, add to queue
            pipelineQueue.enqueue(pipelineExecutionId);
            PipelineLogger.info("Pipeline execution successfully added to queue: " + pipelineExecutionId);
        } catch (Exception e) {
            PipelineLogger.error("Error verifying pipeline execution before queuing: " + e.getMessage());
            throw new RuntimeException("Failed to add pipeline execution to queue: " + e.getMessage(), e);
        }
    }
    
    /**
     * Processes a pipeline execution.
     *
     * @param pipelineExecutionId the ID of the pipeline execution to process
     */
    private void processPipelineExecution(UUID pipelineExecutionId) {
        try {
            PipelineLogger.info("Processing pipeline execution from queue: " + pipelineExecutionId);
            // Use ServiceLocator to get PipelineExecutionService to avoid circular dependency
            ServiceLocator.getBean(edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService.class)
                .processPipelineExecution(pipelineExecutionId);
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