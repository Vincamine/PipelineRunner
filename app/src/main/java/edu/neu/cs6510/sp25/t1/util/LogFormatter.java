package edu.neu.cs6510.sp25.t1.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.neu.cs6510.sp25.t1.model.LogEntry;

/**
 * Utility class for formatting log entries.
 * Formats logs into a readable structure including timestamps, log levels, and messages.
 */
public class LogFormatter {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * Formats a {@link LogEntry} into a structured log message.
   *
   * @param log The log entry to format.
   * @return A formatted log message.
   */
  public static String format(LogEntry log) {
    return String.format("[%s] [%s] %s",
        DATE_FORMAT.format(new Date(log.getTimestamp())),
        log.getLevel().name(),
        log.getMessage());
  }
}
