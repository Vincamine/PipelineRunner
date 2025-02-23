package edu.neu.cs6510.sp25.t1.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import edu.neu.cs6510.sp25.t1.model.ReportEntry;

/**
 * Utility class for formatting report entries.
 * Formats reports into a readable structure including timestamps, log levels,
 * messages, statuses, and additional details.
 */
public class ReportFormatter {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * Formats a {@link ReportEntry} into a structured report message.
   *
   * @param report The report entry to format.
   * @return A formatted report message.
   */
  public static String format(ReportEntry report) {
    if (report == null) {
      return "No report available";
    }

    StringBuilder formattedReport = new StringBuilder();

    // Format header with timestamp, level, and status
    formattedReport.append(String.format("[%s] [%s] [%s] %s\n",
            DATE_FORMAT.format(new Date(report.getTimestamp())),
            report.getLevel().name(),
            report.getStatus(),
            report.getMessage()));

    // Add stages if available
    if (report.getStages() != null && !report.getStages().isEmpty()) {
      formattedReport.append("\nStages:\n");
      for (String stage : report.getStages()) {
        formattedReport.append(String.format("  - %s\n", stage));
      }
    }

    // Add details if available
    if (report.getDetails() != null && !report.getDetails().isEmpty()) {
      formattedReport.append("\nDetails:\n");
      for (String detail : report.getDetails()) {
        formattedReport.append(String.format("  - %s\n", detail));
      }
    }

    return formattedReport.toString();
  }

  /**
   * Private constructor to prevent instantiation.
   * This is a utility class that should only be used statically.
   */
  private ReportFormatter() {
    // Private constructor to prevent instantiation
  }
}