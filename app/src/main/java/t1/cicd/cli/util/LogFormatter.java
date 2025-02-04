package t1.cicd.cli.util;

import t1.cicd.cli.model.LogEntry;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for formatting log entries for display.
 * <p>
 * This class provides a method to format log entries into a readable
 * string format, including timestamps, log levels, and messages.
 * </p>
 */
public class LogFormatter {
  /**
   * Date format used for timestamp representation in logs.
   * Format: {@code yyyy-MM-dd HH:mm:ss}
   */
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * Formats a {@link LogEntry} into a human-readable string.
   * <p>
   * The formatted log entry follows the structure:
   * {@code [timestamp] [log level] message}
   * </p>
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
