package edu.neu.cs6510.sp25.t1.worker.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DockerExecutionException.class)
    public ResponseEntity<WorkerApiError> handleDockerExecutionException(
            DockerExecutionException ex, WebRequest request) {
        log.error("Docker execution error: {}", ex.getMessage());

        WorkerApiError error = new WorkerApiError(
                "DOCKER_ERROR",
                "Docker Execution Failed",
                ex.getMessage() + (ex.getExitCode() != -1 ? " (Exit code: " + ex.getExitCode() + ")" : ""),
                request.getDescription(false)
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BackendCommunicationException.class)
    public ResponseEntity<WorkerApiError> handleBackendCommunicationException(
            BackendCommunicationException ex, WebRequest request) {
        log.error("Backend communication error: {}", ex.getMessage());

        WorkerApiError error = new WorkerApiError(
                "BACKEND_COMMUNICATION_ERROR",
                "Backend Communication Failed",
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(JobExecutionConfigException.class)
    public ResponseEntity<WorkerApiError> handleJobExecutionConfigException(
            JobExecutionConfigException ex, WebRequest request) {
        log.error("Job configuration error: {}", ex.getMessage());

        WorkerApiError error = new WorkerApiError(
                "CONFIG_ERROR",
                "Job Configuration Error",
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<WorkerApiError> handleResourceAccessException(
            ResourceAccessException ex, WebRequest request) {
        log.error("Resource access error: {}", ex.getMessage());

        WorkerApiError error = new WorkerApiError(
                "RESOURCE_ACCESS_ERROR",
                "Resource Access Failed",
                "Failed to access backend resource: " + ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WorkerApiError> handleGenericException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        WorkerApiError error = new WorkerApiError(
                "INTERNAL_ERROR",
                "Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}