package edu.neu.cs6510.sp25.t1.backend.service.queue;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import edu.neu.cs6510.sp25.t1.backend.config.ServiceLocator;
import edu.neu.cs6510.sp25.t1.backend.service.JobExecutionService;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import jakarta.annotation.PostConstruct;

/**
 * Service for managing the job execution queue.
 * This service is responsible for adding job executions to the queue
 * and processing them in order.
 */
@Service
public class JobExecutionQueueService {
    
    private final ExecutionQueue<UUID> jobQueue;
    
    @Autowired
    public JobExecutionQueueService() {
        this.jobQueue = new ExecutionQueue<>();
    }
    
    /**
     * Initialize the job queue processor.
     */
    @PostConstruct
    public void init() {
        jobQueue.setProcessor(this::processJobExecution);
    }
    
    /**
     * Adds a job execution to the queue.
     *
     * @param jobExecutionId the ID of the job execution to add to the queue
     */
    public void enqueueJobExecution(UUID jobExecutionId) {
        PipelineLogger.info("Adding job execution to queue: " + jobExecutionId);
        
        // Verify the job execution exists in the database before queuing
        try {
            // Get repository
            var jobExecRepo = ServiceLocator.getBean(edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository.class);
            
            // Verify job execution exists
            var jobExecution = jobExecRepo.findById(jobExecutionId)
                .orElseThrow(() -> {
                    PipelineLogger.error("Cannot queue job execution that doesn't exist in database: " + jobExecutionId);
                    return new IllegalArgumentException("Job execution not found: " + jobExecutionId);
                });
            
            PipelineLogger.info("Verification successful. Job execution exists in database.");
            
            // All checks passed, add to queue
            jobQueue.enqueue(jobExecutionId);
            PipelineLogger.info("Job execution successfully added to queue: " + jobExecutionId);
        } catch (Exception e) {
            PipelineLogger.error("Error verifying job execution before queuing: " + e.getMessage());
            throw new RuntimeException("Failed to add job execution to queue: " + e.getMessage(), e);
        }
    }
    
    /**
     * Processes a job execution.
     *
     * @param jobExecutionId the ID of the job execution to process
     */
    private void processJobExecution(UUID jobExecutionId) {
        try {
            PipelineLogger.info("Processing job execution from queue: " + jobExecutionId);
            // Use ServiceLocator to get JobExecutionService to avoid circular dependency
            ServiceLocator.getBean(JobExecutionService.class)
                .processJobExecution(jobExecutionId);
        } catch (Exception e) {
            PipelineLogger.error("Error processing job execution: " + jobExecutionId + " - " + e.getMessage());
        }
    }
    
    /**
     * Returns the current size of the job execution queue.
     *
     * @return the number of job executions in the queue
     */
    public int getQueueSize() {
        return jobQueue.size();
    }
    
    /**
     * Checks if the job queue is currently processing.
     *
     * @return true if the job queue is processing, false otherwise
     */
    public boolean isProcessing() {
        return jobQueue.isProcessing();
    }
    
    /**
     * Clears the job execution queue.
     */
    public void clearQueue() {
        jobQueue.clear();
    }
}