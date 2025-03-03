package edu.neu.cs6510.sp25.t1.common.enums;

public enum PipelineExecutionState {
  PENDING,    // Pipeline is scheduled but not started
  RUNNING,    // Pipeline is actively executing
  SUCCESSFUL, // Pipeline completed all jobs/stages successfully
  FAILED;     // Pipeline execution failed due to at least one job failure

  /**
   * Determines if the transition from the current state to the new state is valid.
   *
   * @param newState The target state to transition to.
   * @return true if the transition is valid, false otherwise.
   */
  public boolean canTransitionTo(PipelineExecutionState newState) {
    return switch (this) {
      case PENDING -> newState == RUNNING;
      case RUNNING -> newState == SUCCESSFUL || newState == FAILED;
      case SUCCESSFUL, FAILED -> false; // Terminal states (cannot transition further)
    };
  }
}