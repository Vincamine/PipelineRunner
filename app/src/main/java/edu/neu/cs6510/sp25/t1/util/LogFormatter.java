package edu.neu.cs6510.sp25.t1.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.neu.cs6510.sp25.t1.model.LogEntry;

/**
 * Utility class for formatting log entries for display.
 * This class provides a method to format log entries into a readable
 * string format, including timestamps, log levels, and messages.
 */
public class LogFormatter {
  /**
   * Date format used for timestamp representation in logs.
   * Format: {@code yyyy-MM-dd HH:mm:ss}
   */
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * Formats a {@link LogEntry} into a human-readable string.
   * The formatted log entry follows the structure:
   * {@code [timestamp] [log level] message}
   *
   * @param log The log entry to format.
   * @return A formatted string representation of the log entry.
   */
  public static String format(LogEntry log) {
    return String.format("[%s] [%s] %s",
        DATE_FORMAT.format(new Date(log.getTimestamp())),
        log.getLevel().name(),
        log.getMessage());
  }
}
