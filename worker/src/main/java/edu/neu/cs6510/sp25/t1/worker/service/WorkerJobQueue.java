package edu.neu.cs6510.sp25.t1.worker.service;

import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    // Set to track job IDs being processed - using Collections.synchronizedSet for thread safety
    private final Set<UUID> processingJobIds = Collections.synchronizedSet(new HashSet<>());
    private final ConcurrentHashMap<UUID, Future<?>> jobFutures = new ConcurrentHashMap<>();

    /**
     * RabbitMQ listener that consumes job UUID messages from the queue.
     * This method receives a string representation of a job execution UUID,
     * converts it to UUID, fetches the complete job data from the database,
     * and submits the job for execution.
     *
     * @param jobExecutionIdStr String representation of the job execution UUID
     */
    @RabbitListener(queues = "cicd-job-queue", concurrency = "5")
    public void consumeJob(String jobExecutionIdStr) {
        if (jobExecutionIdStr == null || jobExecutionIdStr.isEmpty()) {
            log.error("Received invalid job ID from queue");
            return;
        }

        try {
            // Convert string to UUID
            UUID jobExecutionId = UUID.fromString(jobExecutionIdStr);
            log.info("Received job ID {} from queue", jobExecutionId);

            // Check if job is already being processed
            if (processingJobIds.contains(jobExecutionId)) {
                log.warn("Job {} is already being processed, skipping", jobExecutionId);
                return;
            }

            // Check if worker is at capacity
            if (processingJobIds.size() >= 5) {
                log.warn("Worker at maximum capacity, cannot process job {}", jobExecutionId);
                return;
            }

            // Fetch complete job data from database
            jobDataService.getJobExecutionById(jobExecutionId).ifPresentOrElse(
                    job -> {
                        // Add to processing set
                        processingJobIds.add(jobExecutionId);

                        // Submit for async execution
                        Future<?> jobFuture = executorService.submit(() -> {
                            try {
                                log.info("Starting execution of job {}", jobExecutionId);

                                // Update status to RUNNING
                                jobDataService.updateJobStatus(jobExecutionId, ExecutionStatus.RUNNING,
                                        "Job execution started");

                                // Execute the job
                                executionService.executeJob(job);

                                log.info("Completed execution of job {}", jobExecutionId);
                            } catch (Exception e) {
                                log.error("Error executing job {}: {}", jobExecutionId, e.getMessage(), e);
                                // Update status to FAILED on error
                                jobDataService.updateJobStatus(jobExecutionId, ExecutionStatus.FAILED,
                                        "Job execution failed with error: " + e.getMessage());
                            } finally {
                                // Remove from processing collections when done
                                processingJobIds.remove(jobExecutionId);
                                jobFutures.remove(jobExecutionId);
                            }
                        });

                        jobFutures.put(jobExecutionId, jobFuture);
                    },
                    () -> log.error("Job with ID {} not found in database", jobExecutionId)
            );
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format received: {}", jobExecutionIdStr);
        }
    }

    /**
     * Returns the number of jobs currently being processed.
     *
     * @return The count of active jobs
     */
    public int getActiveJobCount() {
        return processingJobIds.size();
    }

    /**
     * Returns the list of currently processing job IDs.
     *
     * @return List of active job execution IDs
     */
    public List<UUID> getActiveJobIds() {
        synchronized(processingJobIds) {
            return new ArrayList<>(processingJobIds);
        }
    }

    /**
     * Attempts to cancel a running job by its execution ID.
     * If the job is currently being executed, this method will attempt to
     * cancel its Future and remove it from the tracking collections.
     *
     * @param jobExecutionId The ID of the job to cancel
     * @return true if cancellation was successful, false otherwise
     */
    public boolean cancelJob(UUID jobExecutionId) {
        Future<?> jobFuture = jobFutures.get(jobExecutionId);

        if (jobFuture != null && !jobFuture.isDone()) {
            boolean cancelled = jobFuture.cancel(true);

            if (cancelled) {
                // Clean up our tracking collections
                processingJobIds.remove(jobExecutionId);
                jobFutures.remove(jobExecutionId);
                log.info("Job {} cancelled successfully", jobExecutionId);
            }

            return cancelled;
        }

        log.warn("Job {} not found or already completed", jobExecutionId);
        return false;
    }
}