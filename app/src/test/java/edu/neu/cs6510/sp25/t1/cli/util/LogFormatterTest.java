package edu.neu.cs6510.sp25.t1.cli.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import edu.neu.cs6510.sp25.t1.model.LogEntry;
import edu.neu.cs6510.sp25.t1.model.LogLevel;
import edu.neu.cs6510.sp25.t1.util.LogFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

class LogFormatterTest {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @ParameterizedTest
  @EnumSource(LogLevel.class)
  void testFormatLogForDifferentLevels(LogLevel level) {
    final long timestamp = System.currentTimeMillis();
    final String message = "Test message";
    final LogEntry log = new LogEntry("test-id", level, message, timestamp);

    final String formattedLog = LogFormatter.format(log);

    final String expectedTimestamp = DATE_FORMAT.format(new Date(timestamp));
    final String expectedOutput = String.format("[%s] [%s] %s",
        expectedTimestamp, level.name(), message);
    assertEquals(expectedOutput, formattedLog);
  }

  @Test
  void testFormatLogWithNullMessage() {
    final long timestamp = System.currentTimeMillis();
    final LogEntry log = new LogEntry("test-id", LogLevel.INFO, null, timestamp);

    final String formattedLog = LogFormatter.format(log);

    final String expectedTimestamp = DATE_FORMAT.format(new Date(timestamp));
    final String expectedOutput = String.format("[%s] [%s] %s",
        expectedTimestamp, "INFO", "null");
    assertEquals(expectedOutput, formattedLog);
  }

  @Test
  void testFormatLogWithLongMessage() {
    final long timestamp = System.currentTimeMillis();
    final String longMessage = "A".repeat(1000);
    final LogEntry log = new LogEntry("test-id", LogLevel.INFO, longMessage, timestamp);

    final String formattedLog = LogFormatter.format(log);

    final String expectedTimestamp = DATE_FORMAT.format(new Date(timestamp));
    final String expectedOutput = String.format("[%s] [%s] %s",
        expectedTimestamp, "INFO", longMessage);
    assertEquals(expectedOutput, formattedLog);
  }
}

