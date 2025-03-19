package edu.neu.cs6510.sp25.t1.worker.service;

import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID;

/**
 * Service that manages job execution by consuming messages from a queue.
 * Instead of polling the database, it listens for messages from RabbitMQ.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerJobQueue {
    private final WorkerExecutionService executionService;
    private final JobDataService jobDataService;

    // Thread pool for executing jobs concurrently
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    // Map to track jobs being processed
    private final ConcurrentHashMap<UUID, JobExecutionDTO> processingJobs = new ConcurrentHashMap<>();

    /**
     * RabbitMQ listener that consumes job messages from the queue.
     *
     * @param job The job received from the queue
     */
    @RabbitListener(queues = "cicd-job-queue", concurrency = "5")
    public void consumeJob(JobExecutionDTO job) {
        if (job == null || job.getId() == null) {
            log.error("Received invalid job from queue");
            return;
        }

        log.info("Received job {} from queue", job.getId());

        // Skip if we're already processing this job
        if (processingJobs.containsKey(job.getId())) {
            log.warn("Job {} is already being processed, skipping", job.getId());
            return;
        }

        // Check if we're at capacity
        if (processingJobs.size() >= 5) {
            log.warn("Worker at maximum capacity, cannot process job {}", job.getId());
            // In a real implementation, you might want to reject the message
            // so it can be reprocessed later or by another worker
            return;
        }

        // Add to processing map
        processingJobs.put(job.getId(), job);

        // Submit for async execution
        executorService.submit(() -> {
            try {
                log.info("Starting execution of job {}", job.getId());

                // Update status to RUNNING before execution
                jobDataService.updateJobStatus(job.getId(), ExecutionStatus.RUNNING,
                        "Job execution started");

                // Execute the job
                executionService.executeJob(job);

                log.info("Completed execution of job {}", job.getId());
            } catch (Exception e) {
                log.error("Error executing job {}: {}", job.getId(), e.getMessage(), e);
                // Update job status on failure
                jobDataService.updateJobStatus(job.getId(), ExecutionStatus.FAILED,
                        "Job execution failed with error: " + e.getMessage());
            } finally {
                // Always remove from processing map when done
                processingJobs.remove(job.getId());
            }
        });
    }

    /**
     * Returns the number of jobs currently being processed.
     *
     * @return The count of active jobs
     */
    public int getActiveJobCount() {
        return processingJobs.size();
    }
}