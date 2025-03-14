package edu.neu.cs6510.sp25.t1.backend.error;



/**
 * Exception thrown when an error occurs during stage execution.
 */
public class StageExecutionException extends RuntimeException {

    public StageExecutionException(String message) {
        super(message);
    }

    public StageExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
