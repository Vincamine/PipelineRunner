package edu.neu.cs6510.sp25.t1.cli.util;

import edu.neu.cs6510.sp25.t1.cli.commands.ApiResponse;
import edu.neu.cs6510.sp25.t1.cli.model.PipelineState;
import edu.neu.cs6510.sp25.t1.cli.model.PipelineStatus;
import java.time.Duration;
import java.time.Instant;

/**
 * Handles errors that occur during pipeline execution.
 * Provides comprehensive error handling for different pipeline states and stages.
 */
public class ExecutionErrorHandler {
  private String currentStage;
  private Instant executionStartTime;

  public ExecutionErrorHandler() {
    this.executionStartTime = Instant.now();
  }

  /**
   * Handles pipeline status errors and provides appropriate error messages.
   *
   * @param status Current pipeline status
   * @return true if execution can continue, false if it should stop
   */
  public boolean handlePipelineStatus(PipelineStatus status) {
    switch (status.getState()) {
      case FAILED:
        System.err.printf("Pipeline failed in stage '%s': %s%n",
            status.getCurrentStage(),
            status.getMessage() != null ? status.getMessage() : "No error message provided");
        return false;

      case CANCELLED:
        System.err.printf("Pipeline was cancelled in stage '%s' after running for %s%n",
            status.getCurrentStage(),
            getExecutionDuration(status));
        return false;

      case UNKNOWN:
        System.err.println("Unable to determine pipeline status. Please check manually.");
        return false;

      case PENDING:
        if (Duration.between(status.getLastUpdated(), Instant.now()).toMinutes() > 10) {
          System.err.println("Pipeline has been pending for too long. Please check for issues.");
          return false;
        }
        return true;

      default:
        return true;
    }
  }

  /**
   * Handles API response errors during pipeline execution.
   *
   * @param response The API response to check
   * @return true if the operation should continue
   */
  public boolean handleApiError(ApiResponse response) {
    if (response.isNotFound()) {
      System.err.println("Error: Resource not found - " + response.getResponseBody());
      return false;
    }

    System.err.printf("Error: API request failed (%d) - %s%n",
        response.getStatusCode(),
        response.getResponseBody());
    return false;
  }

  /**
   * Handles stage execution failures.
   *
   * @param status Current pipeline status
   * @return true if execution can continue, false if it should stop
   */
  public boolean handleStageFailure(PipelineStatus status) {
    if (status.getState() != PipelineState.FAILED) {
      return true;
    }

    // 更新当前阶段
    if (!status.getCurrentStage().equals(currentStage)) {
      currentStage = status.getCurrentStage();
    }

    // 输出详细的失败信息
    System.err.println("\nStage Failure Details:");
    System.err.println("----------------------");
    System.err.printf("Failed Stage: %s%n", currentStage);
    System.err.printf("Error Message: %s%n",
        status.getMessage() != null ? status.getMessage() : "No error message provided");
    System.err.printf("Execution Time: %s%n", getExecutionDuration(status));
    System.err.printf("Last Updated: %s%n", status.getLastUpdated());
    System.err.println("----------------------");

    return false;
  }

  /**
   * Handles configuration file errors.
   *
   * @param filePath The path to the configuration file
   * @param error The error message
   */
  public void handleConfigError(String filePath, String error) {
    System.err.println("\nConfiguration Error:");
    System.err.println("-------------------");
    System.err.printf("File: %s%n", filePath);
    System.err.printf("Error: %s%n", error);
    System.err.println("Please check your pipeline configuration file for errors.");
    System.err.println("-------------------");
  }

  /**
   * Handles startup errors during pipeline initialization.
   *
   * @param e The exception that occurred
   */
  public void handleStartupError(Exception e) {
    System.err.println("\nStartup Error:");
    System.err.println("--------------");
    System.err.printf("Error: %s%n", e.getMessage());
    System.err.println("Unable to start pipeline execution.");
    System.err.println("--------------");
  }

  /**
   * Handles monitoring errors during pipeline execution.
   *
   * @param e The exception that occurred
   */
  public void handleMonitoringError(Exception e) {
    System.err.println("\nMonitoring Error:");
    System.err.println("-----------------");
    System.err.printf("Error: %s%n", e.getMessage());
    System.err.println("Unable to monitor pipeline status.");
    System.err.println("-----------------");
  }

  /**
   * Calculates the execution duration from start to last update.
   *
   * @param status The pipeline status
   * @return A string representing the execution duration
   */
  private String getExecutionDuration(PipelineStatus status) {
    final Duration duration = Duration.between(status.getStartTime(), status.getLastUpdated());
    final long minutes = duration.toMinutes();
    final long seconds = duration.minusMinutes(minutes).getSeconds();
    return String.format("%d minutes, %d seconds", minutes, seconds);
  }
}