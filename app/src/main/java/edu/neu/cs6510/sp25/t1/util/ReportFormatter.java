package edu.neu.cs6510.sp25.t1.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.neu.cs6510.sp25.t1.model.ReportEntry;

/**
 * Utility class for formatting log entries.
 * Formats logs into a readable structure including timestamps, log levels, and
 * messages.
 */
public class ReportFormatter {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * Formats a {@link ReportEntry} into a structured log message.
   *
   * @param report The log entry to format.
   * @return A formatted log message.
   */
  public static String format(ReportEntry report) {
    return String.format("[%s] [%s] %s",
        DATE_FORMAT.format(new Date(report.getTimestamp())),
        report.getLevel().name(),
        report.getMessage());
  }
}
