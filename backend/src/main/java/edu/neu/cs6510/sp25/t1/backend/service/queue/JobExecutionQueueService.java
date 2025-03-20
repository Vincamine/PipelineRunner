package edu.neu.cs6510.sp25.t1.backend.service.queue;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

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
    private JobExecutionService jobExecutionService;
    private final ApplicationContext applicationContext;
    
    @Autowired
    public JobExecutionQueueService(ApplicationContext applicationContext) {
        this.jobQueue = new ExecutionQueue<>();
        this.applicationContext = applicationContext;
    }
    
    /**
     * Initialize the job queue processor.
     * Gets the JobExecutionService lazily to break the circular dependency.
     */
    @PostConstruct
    public void init() {
        // Get the JobExecutionService lazily from the ApplicationContext
        this.jobExecutionService = applicationContext.getBean(JobExecutionService.class);
        jobQueue.setProcessor(this::processJobExecution);
    }
    
    /**
     * Adds a job execution to the queue.
     *
     * @param jobExecutionId the ID of the job execution to add to the queue
     */
    public void enqueueJobExecution(UUID jobExecutionId) {
        PipelineLogger.info("Adding job execution to queue: " + jobExecutionId);
        jobQueue.enqueue(jobExecutionId);
    }
    
    /**
     * Processes a job execution.
     *
     * @param jobExecutionId the ID of the job execution to process
     */
    private void processJobExecution(UUID jobExecutionId) {
        try {
            PipelineLogger.info("Processing job execution from queue: " + jobExecutionId);
            jobExecutionService.processJobExecution(jobExecutionId);
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