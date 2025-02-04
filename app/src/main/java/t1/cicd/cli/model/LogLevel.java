package t1.cicd.cli.model;

/**
 * Defines log levels for logging in the CI/CD system.
 */
public enum LogLevel {
  DEBUG,  // Detailed debugging information
  INFO,   // General operational messages
  WARN,   // Warnings that may require attention
  ERROR,  // Errors affecting functionality
  FATAL   // Critical errors causing termination
}
