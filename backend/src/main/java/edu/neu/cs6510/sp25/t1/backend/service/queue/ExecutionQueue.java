package edu.neu.cs6510.sp25.t1.backend.service.queue;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;

/**
 * Generic execution queue for pipeline, stage, and job executions.
 * This queue maintains the order of execution tasks and processes them sequentially.
 */
@Component
public class ExecutionQueue<T> {
    private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();
    private boolean isProcessing = false;
    private final Object lock = new Object();

    /**
     * Adds an execution task to the queue.
     *
     * @param executionId the ID of the execution to add to the queue
     */
    public void enqueue(T executionId) {
        PipelineLogger.info("Adding execution to queue: " + executionId);
        queue.add(executionId);
        startProcessing();
    }

    /**
     * Starts processing the queue if it's not already being processed.
     */
    private void startProcessing() {
        synchronized (lock) {
            if (!isProcessing && !queue.isEmpty()) {
                isProcessing = true;
                processNextExecution();
            }
        }
    }

    /**
     * Processes the next execution in the queue.
     */
    private void processNextExecution() {
        PipelineLogger.info("Processing next execution from queue");
        synchronized (lock) {
            if (queue.isEmpty()) {
                isProcessing = false;
                PipelineLogger.info("Queue is empty. Processing complete.");
                return;
            }
        }
        
        T executionId = queue.poll();
        if (executionId != null) {
            try {
                PipelineLogger.info("Processing execution: " + executionId);
                if (processor != null) {
                    processor.accept(executionId);
                } else {
                    PipelineLogger.error("No processor configured for execution queue");
                }
            } catch (Exception e) {
                PipelineLogger.error("Error processing execution: " + executionId + " - " + e.getMessage());
            }
            
            // Continue processing the queue
            processNextExecution();
        } else {
            synchronized (lock) {
                isProcessing = false;
            }
        }
    }
    
    private Consumer<T> processor;
    
    /**
     * Sets the processor function to be called for each item in the queue.
     *
     * @param processor the function to process each execution ID
     */
    public void setProcessor(Consumer<T> processor) {
        this.processor = processor;
    }
    
    /**
     * Checks if the queue is currently processing items.
     *
     * @return true if the queue is processing, false otherwise
     */
    public boolean isProcessing() {
        synchronized (lock) {
            return isProcessing;
        }
    }
    
    /**
     * Returns the number of items in the queue.
     *
     * @return the queue size
     */
    public int size() {
        return queue.size();
    }
    
    /**
     * Clears all items from the queue.
     */
    public void clear() {
        synchronized (lock) {
            queue.clear();
            isProcessing = false;
        }
    }
}