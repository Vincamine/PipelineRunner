package edu.neu.cs6510.sp25.t1.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for handling and reporting errors in pipeline reporting
 * operations.
 * Provides standardized error reporting and formatting.
 */
public class ReportErrorHandler {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Error message templates
    private static final String INVALID_REPO = "Invalid repository: %s";
    private static final String MISSING_PIPELINE = "Pipeline not found: %s";
    private static final String INVALID_FORMAT = "Invalid report format: %s";
    private static final String ACCESS_ERROR = "Unable to access repository: %s";
    private static final String PIPELINE_RUN_NOT_FOUND = "Pipeline run #%d not found for pipeline: %s";

    /**
     * Reports an error for invalid repository configuration or access.
     *
     * @param repoUrl The repository URL that caused the error
     * @param cause   The underlying exception (optional)
     */
    public static void reportInvalidRepository(String repoUrl, Throwable cause) {
        String message = String.format(INVALID_REPO, repoUrl);
        reportError(message, cause);
    }

    /**
     * Reports an error when a specified pipeline cannot be found.
     *
     * @param pipelineId The pipeline identifier that wasn't found
     */
    public static void reportMissingPipeline(String pipelineId) {
        String message = String.format(MISSING_PIPELINE, pipelineId);
        reportError(message);
    }

    /**
     * Reports an error when a specific pipeline run is not found.
     *
     * @param pipelineId The pipeline identifier
     * @param runNumber  The run number that wasn't found
     */
    public static void reportPipelineRunNotFound(String pipelineId, int runNumber) {
        String message = String.format(PIPELINE_RUN_NOT_FOUND, runNumber, pipelineId);
        reportError(message);
    }

    /**
     * Reports an error for invalid report format or structure.
     *
     * @param details Details about the format error
     * @param cause   The underlying exception (optional)
     */
    public static void reportInvalidFormat(String details, Throwable cause) {
        String message = String.format(INVALID_FORMAT, details);
        reportError(message, cause);
    }

    /**
     * Reports an error when unable to access the repository.
     *
     * @param repoUrl The repository URL that couldn't be accessed
     * @param cause   The underlying exception
     */
    public static void reportRepositoryAccessError(String repoUrl, Throwable cause) {
        String message = String.format(ACCESS_ERROR, repoUrl);
        reportError(message, cause);
    }

    /**
     * Reports a general error message with timestamp.
     *
     * @param message The error message to be reported
     */
    public static void reportError(String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        System.err.println(String.format("[%s] Error: %s", timestamp, message));
    }

    /**
     * Reports an error with both the message and its cause.
     *
     * @param message The error message to be reported
     * @param cause   The underlying exception that caused the error
     */
    public static void reportError(String message, Throwable cause) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        System.err.println(String.format("[%s] Error: %s", timestamp, message));
        if (cause != null) {
            System.err.println(String.format("[%s] Cause: %s", timestamp, cause.getMessage()));
            if (isDebugMode()) {
                cause.printStackTrace(System.err);
            }
        }
    }

    /**
     * Formats an error message with additional context.
     *
     * @param message The base error message
     * @param context Additional context information
     * @return Formatted error message with context
     */
    public static String formatErrorMessage(String message, String context) {
        return String.format("%s (Context: %s)", message, context);
    }

    /**
     * Checks if debug mode is enabled.
     * Debug mode can be enabled by setting the system property 'app.debug' to true.
     *
     * @return true if debug mode is enabled
     */
    private static boolean isDebugMode() {
        return Boolean.getBoolean("app.debug");
    }

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class that should only be used statically.
     */
    private ReportErrorHandler() {
        // Private constructor to prevent instantiation
    }
}