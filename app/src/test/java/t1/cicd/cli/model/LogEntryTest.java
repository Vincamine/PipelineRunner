package t1.cicd.cli.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LogEntryTest {
  /**
   * Test LogEntry object creation and field retrieval.
   */
  @Test
  void testLogEntryCreation() {
    long timestamp = System.currentTimeMillis();
    LogEntry log = new LogEntry("123", LogLevel.INFO, "Pipeline started", timestamp);

    assertEquals("123", log.getPipelineId(), "Pipeline ID should match.");
    assertEquals(LogLevel.INFO, log.getLevel(), "Log level should be INFO.");
    assertEquals("Pipeline started", log.getMessage(), "Message should match.");
    assertEquals(timestamp, log.getTimestamp(), "Timestamp should match.");
  }

  /**
   * Test LogEntry with a null message.
   */
  @Test
  void testLogEntryWithNullMessage() {
    long timestamp = System.currentTimeMillis();
    LogEntry log = new LogEntry("456", LogLevel.WARN, null, timestamp);

    assertEquals("456", log.getPipelineId(), "Pipeline ID should match.");
    assertEquals(LogLevel.WARN, log.getLevel(), "Log level should be WARN.");
    assertNull(log.getMessage(), "Message should be null.");
    assertEquals(timestamp, log.getTimestamp(), "Timestamp should match.");
  }

  /**
   * Test LogEntry with an empty message.
   */
  @Test
  void testLogEntryWithEmptyMessage() {
    long timestamp = System.currentTimeMillis();
    LogEntry log = new LogEntry("789", LogLevel.ERROR, "", timestamp);

    assertEquals("789", log.getPipelineId(), "Pipeline ID should match.");
    assertEquals(LogLevel.ERROR, log.getLevel(), "Log level should be ERROR.");
    assertEquals("", log.getMessage(), "Message should be empty.");
    assertEquals(timestamp, log.getTimestamp(), "Timestamp should match.");
  }

}