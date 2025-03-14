package edu.neu.cs6510.sp25.t1.backend.error;

/**
 * Exception thrown when a requested resource cannot be found.
 * Used for entities like Pipeline, Stage, or Job executions.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(resourceType + " not found with ID: " + resourceId);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
