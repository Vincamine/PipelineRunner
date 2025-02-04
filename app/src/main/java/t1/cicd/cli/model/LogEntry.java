package t1.cicd.cli.model;

/**
 * Represents a log entry in the CI/CD system.
 * Each log entry contains information about the pipeline,
 * log level, message, and timestamp.
 */
public class LogEntry {
  private String pipelineId;
  private LogLevel level;
  private String message;
  private long timestamp;

  /**
   * Constructs a new LogEntry instance.
   *
   * @param pipelineId The ID of the pipeline associated with the log entry.
   * @param level      The severity level of the log (e.g., INFO, WARN, ERROR).
   * @param message    A description of the event being logged.
   * @param timestamp  The timestamp when the log entry was created, in milliseconds.
   */
  public LogEntry(String pipelineId, LogLevel level, String message, long timestamp) {
    this.pipelineId = pipelineId;
    this.level = level;
    this.message = message;
    this.timestamp = timestamp;
  }

  /**
   * Retrieves the ID of the pipeline associated with this log entry.
   *
   * @return The pipeline ID.
   */
  public String getPipelineId() {
    return pipelineId;
  }

  /**
   * Retrieves the severity level of the log entry.
   *
   * @return The log level as an enum (e.g., LogLevel.INFO, LogLevel.WARN).
   */
  public LogLevel getLevel() {
    return level;
  }

  /**
   * Retrieves the message describing the log entry.
   *
   * @return The log message.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Retrieves the timestamp of the log entry.
   *
   * @return The timestamp in milliseconds.
   */
  public long getTimestamp() {
    return timestamp;
  }
}
