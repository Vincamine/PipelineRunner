package edu.neu.cs6510.sp25.t1.worker.error;


/**
 * Exception thrown when communication with the backend fails.
 */
public class BackendCommunicationException extends WorkerException {

    public BackendCommunicationException(String message) {
        super(message);
    }

    public BackendCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
