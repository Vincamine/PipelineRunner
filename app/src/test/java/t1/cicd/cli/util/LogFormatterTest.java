package t1.cicd.cli.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import t1.cicd.cli.model.LogEntry;
import t1.cicd.cli.model.LogLevel;

import java.text.SimpleDateFormat;
import java.util.Date;

class LogFormatterTest {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * Test formatting of a log entry with an INFO level.
   */
  @Test
  void testFormatInfoLog() {
    final long timestamp = System.currentTimeMillis();
    final LogEntry log = new LogEntry("123", LogLevel.INFO, "Pipeline started", timestamp);
    final String formattedLog = LogFormatter.format(log);

    final String expectedTimestamp = DATE_FORMAT.format(new Date(timestamp));
    final String expectedOutput = String.format("[%s] [%s] %s", expectedTimestamp, "INFO", "Pipeline started");

    assertEquals(expectedOutput, formattedLog);
  }

  /**
   * Test formatting of a log entry with an ERROR level.
   */
  @Test
  void testFormatErrorLog() {
    final long timestamp = System.currentTimeMillis();
    final LogEntry log = new LogEntry("456", LogLevel.ERROR, "Pipeline failed", timestamp);
    final String formattedLog = LogFormatter.format(log);

    final String expectedTimestamp = DATE_FORMAT.format(new Date(timestamp));
    final String expectedOutput = String.format("[%s] [%s] %s", expectedTimestamp, "ERROR", "Pipeline failed");

    assertEquals(expectedOutput, formattedLog, "ERROR log entry should be formatted correctly.");
  }

  /**
   * Test formatting of a log entry with a null message.
   */
  @Test
  void testFormatLogWithNullMessage() {
    final long timestamp = System.currentTimeMillis();
    final LogEntry log = new LogEntry("789", LogLevel.WARN, null, timestamp);
    final String formattedLog = LogFormatter.format(log);

    final String expectedTimestamp = DATE_FORMAT.format(new Date(timestamp));
    final String expectedOutput = String.format("[%s] [%s] %s", expectedTimestamp, "WARN", "null");

    assertEquals(expectedOutput, formattedLog, "Log entry with a null message should handle gracefully.");
  }
}