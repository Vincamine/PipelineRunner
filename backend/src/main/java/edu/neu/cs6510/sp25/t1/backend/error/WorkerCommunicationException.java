package edu.neu.cs6510.sp25.t1.backend.error;


/**
 * Exception thrown when communication with worker service fails.
 */
public class WorkerCommunicationException extends RuntimeException {

    public WorkerCommunicationException(String message) {
        super("Failed to communicate with worker: " + message);
    }

    public WorkerCommunicationException(String message, Throwable cause) {
        super("Failed to communicate with worker: " + message, cause);
    }
}
