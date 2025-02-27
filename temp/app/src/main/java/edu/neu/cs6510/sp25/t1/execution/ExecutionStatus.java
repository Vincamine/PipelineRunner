package edu.neu.cs6510.sp25.t1.execution;

/**
 * Represents the possible states of a CI/CD pipeline execution.
 */
public enum ExecutionStatus {
  PENDING,
  RUNNING,
  SUCCESSFUL,
  FAILED;
}
