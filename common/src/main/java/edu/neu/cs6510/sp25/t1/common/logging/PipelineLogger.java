package edu.neu.cs6510.sp25.t1.common.logging;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * PipelineLogger provides advanced logging with structured log levels.
 * Uses java.util.logging for better log management.
 */
public class PipelineLogger {
  private static final Logger LOGGER = Logger.getLogger(PipelineLogger.class.getName());
  private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  /**
   * Logs an informational message.
   *
   * @param message The message to log.
   */
  public static void info(String message) {
    log(Level.INFO, message);
  }

  /**
   * Logs a warning message.
   *
   * @param message The warning message.
   */
  public static void warn(String message) {
    log(Level.WARNING, message);
  }

  /**
   * Logs an error message.
   *
   * @param message The error message.
   */
  public static void error(String message) {
    log(Level.SEVERE, message);
  }

  /**
   * Logs an error message with an exception stack trace.
   *
   * @param message   The error message.
   * @param exception The exception to log.
   */
  public static void error(String message, Throwable exception) {
    LOGGER.log(Level.SEVERE, message, exception);
  }

  /**
   * Internal logging function that prints formatted log messages.
   *
   * @param level   The log level (INFO, WARNING, ERROR).
   * @param message The message to log.
   */
  private static void log(Level level, String message) {
    String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    LOGGER.log(level, "[{0}] {1}", new Object[]{timestamp, message});
  }
}
