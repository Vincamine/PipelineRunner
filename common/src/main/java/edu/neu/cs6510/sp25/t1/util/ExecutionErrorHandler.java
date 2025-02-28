// package edu.neu.cs6510.sp25.t1.util;


// import java.time.Duration;
// import java.time.Instant;
// import edu.neu.cs6510.sp25.t1.model.PipelineState;
// import edu.neu.cs6510.sp25.t1.model.execution.PipelineExecution;

// /**
//  * Handles errors that occur during pipeline execution.
//  * Provides comprehensive error handling for different pipeline states and
//  * stages.
//  */
// public class ExecutionErrorHandler {
//   private String currentStage;
//   @SuppressWarnings("unused")
//   private Instant executionStartTime;

//   /**
//    * Initializes the error handler and sets the execution start time.
//    */
//   public ExecutionErrorHandler() {
//     this.executionStartTime = Instant.now();
//   }

//   /**
//    * Handles pipeline status errors and determines if execution should continue.
//    *
//    * @param status The current pipeline status.
//    * @return {@code true} if execution can proceed, {@code false} if it should
//    *         stop.
//    */
//   public boolean handlePipelineStatus(PipelineExecution status) {
//     switch (status.getState()) {
//       case FAILED:
//         System.err.printf("Pipeline failed at stage '%s': %s%n",
//             status.getCurrentStage(),
//             (status.getMessage() != null) ? status.getMessage() : "No error message provided");
//         return false;

//       case CANCELED:
//         System.err.printf("Pipeline was cancelled at stage '%s' after running for %s%n",
//             status.getCurrentStage(),
//             getExecutionDuration(status));
//         return false;

//       case UNKNOWN:
//         System.err.println("Unknown pipeline status. Please check manually.");
//         return false;

//       case PENDING:
//         if (Duration.between(status.getLastUpdated(), Instant.now()).toMinutes() > 10) {
//           System.err.println("Pipeline has been pending for too long. Check for issues.");
//           return false;
//         }
//         return true;

//       default:
//         return true;
//     }
//   }

//   /**
//    * Handles API response errors during pipeline execution.
//    *
//    * @param response The API response to check.
//    * @return {@code true} if execution can continue, {@code false} if an error
//    *         occurs.
//    */
//   public boolean handleApiError(ApiResponse response) {
//     if (response.isNotFound()) {
//       System.err.println("API Error: Resource not found - " + response.getResponseBody());
//       return false;
//     }

//     System.err.printf("API request failed (Status Code: %d) - %s%n",
//         response.getStatusCode(),
//         response.getResponseBody());
//     return false;
//   }

//   /**
//    * Handles stage execution failures.
//    *
//    * @param status The pipeline status at the time of failure.
//    * @return {@code true} if execution can continue, {@code false} if it should
//    *         stop.
//    */
//   public boolean handleStageFailure(PipelineExecution status) {
//     if (status.getState() != PipelineState.FAILED) {
//       return true;
//     }

//     // Update the current stage
//     if (!status.getCurrentStage().equals(currentStage)) {
//       currentStage = status.getCurrentStage();
//     }

//     // Log failure details
//     System.err.println("\nStage Failure Details:");
//     System.err.println("------------------------");
//     System.err.printf("Failed Stage: %s%n", currentStage);
//     System.err.printf("Error Message: %s%n",
//         (status.getMessage() != null) ? status.getMessage() : "No error message provided");
//     System.err.printf("Execution Time: %s%n", getExecutionDuration(status));
//     System.err.printf("Last Updated: %s%n", status.getLastUpdated());
//     System.err.println("------------------------");

//     return false;
//   }

//   /**
//    * Returns the execution duration in a human-readable format.
//    *
//    * @param status The pipeline status.
//    * @return A formatted duration string.
//    */
//   private String getExecutionDuration(PipelineExecution status) {
//     final Duration duration = Duration.between(status.getStartTime(), status.getLastUpdated());
//     final long minutes = duration.toMinutes();
//     final long seconds = duration.minusMinutes(minutes).getSeconds();
//     return String.format("%d minutes, %d seconds", minutes, seconds);
//   }
// }
