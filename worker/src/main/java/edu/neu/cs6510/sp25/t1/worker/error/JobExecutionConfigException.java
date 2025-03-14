package edu.neu.cs6510.sp25.t1.worker.error;

/**
 * Exception thrown when job configuration is invalid.
 */
public class JobExecutionConfigException extends WorkerException {

    public JobExecutionConfigException(String message) {
        super(message);
    }

    public JobExecutionConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}