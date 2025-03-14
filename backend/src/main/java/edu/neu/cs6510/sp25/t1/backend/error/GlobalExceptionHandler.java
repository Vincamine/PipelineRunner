package edu.neu.cs6510.sp25.t1.backend.error;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Global exception handler for handling various exceptions across the application.
 * This class provides centralized exception handling using Spring's @RestControllerAdvice.
 * The handler follows a consistent pattern of exception handling:
 * 1. Catch the exception
 * 2. Log the error details
 * 3. Create a standardized error response
 * 4. Set appropriate HTTP status code
 * 5. Return a formatted error response to the client
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation exceptions and returns a BAD_REQUEST response.
     *
     * @param ex the ValidationException thrown during request validation
     * @return ResponseEntity containing an ApiError with a BAD_REQUEST status
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex) {
        PipelineLogger.error("Validation error: " + ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                ex.getMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles IllegalArgumentException and returns a BAD_REQUEST response.
     *
     * @param ex the IllegalArgumentException thrown due to invalid input parameters
     * @return ResponseEntity containing an ApiError with a BAD_REQUEST status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        PipelineLogger.error("Invalid argument: " + ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Invalid Request",
                ex.getMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles resource not found exceptions and returns a NOT_FOUND response.
     *
     * @param ex the NoSuchElementException thrown when a requested resource cannot be found
     * @return ResponseEntity containing an ApiError with a NOT_FOUND status
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNoSuchElementException(NoSuchElementException ex) {
        PipelineLogger.error("Resource not found: " + ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles method argument validation failures and returns a BAD_REQUEST response.
     * This handler processes field errors from binding results and concatenates them into a single message.
     *
     * @param ex the MethodArgumentNotValidException thrown during @Valid validation
     * @return ResponseEntity containing an ApiError with a BAD_REQUEST status and detailed field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        PipelineLogger.error("Request validation error: " + errors);
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Request Validation Failed",
                errors
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }



    /**
     * Handles database access exceptions and returns an INTERNAL_SERVER_ERROR response.
     * The detailed error message is logged but not exposed to the client for security reasons.
     *
     * @param ex the DataAccessException thrown during database operations
     * @return ResponseEntity containing an ApiError with an INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiError> handleDataAccessException(DataAccessException ex) {
        PipelineLogger.error("Database error: " + ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Database Error",
                "An error occurred while accessing the database"
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles data integrity violation exceptions and returns a BAD_REQUEST response.
     * These typically occur when database constraints are violated.
     *
     * @param ex the DataIntegrityViolationException thrown when a database constraint is violated
     * @return ResponseEntity containing an ApiError with a BAD_REQUEST status
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        PipelineLogger.error("Data integrity violation: " + ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Data Integrity Violation",
                "The request could not be processed due to a conflict with the current state of the resource"
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles runtime exceptions and returns an INTERNAL_SERVER_ERROR response.
     *
     * @param ex the RuntimeException thrown during application execution
     * @return ResponseEntity containing an ApiError with an INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException ex) {
        PipelineLogger.error("Runtime error: " + ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                ex.getMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles generic exceptions and returns an INTERNAL_SERVER_ERROR response.
     * This is a catch-all handler for any exceptions not specifically handled by other methods.
     *
     * @param ex the Exception thrown due to an unexpected error
     * @return ResponseEntity containing an ApiError with an INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        PipelineLogger.error("Unexpected error: " + ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred. Please check server logs for details."
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles resource access exceptions typically occurring during service-to-service communication.
     *
     * @param ex the ResourceAccessException thrown when communication with a dependent service fails
     * @return ResponseEntity containing an ApiError with a SERVICE_UNAVAILABLE status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        PipelineLogger.error("Resource not found: " + ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles resource access exceptions typically occurring during service-to-service communication.
     *
     * @param ex the ResourceAccessException thrown when communication with a dependent service fails
     * @return ResponseEntity containing an ApiError with a SERVICE_UNAVAILABLE status
     */
    @ExceptionHandler(WorkerCommunicationException.class)
    public ResponseEntity<ApiError> handleWorkerCommunicationException(WorkerCommunicationException ex) {
        PipelineLogger.error("Worker communication error: " + ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Worker Communication Error",
                ex.getMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handles pipeline execution exceptions and returns an INTERNAL_SERVER_ERROR response.
     * This is specific to errors that occur during pipeline processing.
     *
     * @param ex the PipelineExecutionException thrown when a pipeline execution fails
     * @return ResponseEntity containing an ApiError with an INTERNAL_SERVER_ERROR status
     */
    @ExceptionHandler(PipelineExecutionException.class)
    public ResponseEntity<ApiError> handlePipelineExecutionException(PipelineExecutionException ex) {
        PipelineLogger.error("Pipeline execution error: " + ex.getMessage());
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Pipeline Execution Error",
                ex.getMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}