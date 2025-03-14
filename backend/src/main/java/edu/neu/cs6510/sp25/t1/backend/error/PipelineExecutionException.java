package edu.neu.cs6510.sp25.t1.backend.error;

/**
 * Exception thrown when an error occurs during pipeline execution.
 */
public class PipelineExecutionException extends RuntimeException {

    public PipelineExecutionException(String message) {
        super(message);
    }

    public PipelineExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}