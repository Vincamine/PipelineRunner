package edu.neu.cs6510.sp25.t1.worker.error;

/**
 * Base exception class for worker-related errors.
 */
public class WorkerException extends RuntimeException {

    public WorkerException(String message) {
        super(message);
    }

    public WorkerException(String message, Throwable cause) {
        super(message, cause);
    }
}