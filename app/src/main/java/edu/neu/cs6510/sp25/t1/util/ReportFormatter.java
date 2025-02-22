package edu.neu.cs6510.sp25.t1.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import edu.neu.cs6510.sp25.t1.model.ReportEntry;
import edu.neu.cs6510.sp25.t1.model.StageInfo;

/**
 * Utility class for formatting report entries.
 * Formats reports into a readable structure including timestamps, log levels,
 * messages, statuses, and stage details.
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
    StringBuilder formattedReport = new StringBuilder();
    formattedReport.append(String.format("[%s] [%s] [%s] %s\n",
        DATE_FORMAT.format(new Date(report.getTimestamp())),
        report.getLevel().name(),
        report.getStatus(),
        report.getMessage()));

    if (report.getStages() != null && !report.getStages().isEmpty()) {
      formattedReport.append("Stages:\n");
      for (StageInfo stage : report.getStages()) {
        formattedReport.append(String.format("  - [%s] Status: %s, Start: %s, End: %s\n",
            stage.getStageName(),
            stage.getStageStatus(),
            DATE_FORMAT.format(new Date(stage.getStartTime())),
            DATE_FORMAT.format(new Date(stage.getCompletionTime()))));
      }
    }

    return formattedReport.toString();
  }
}