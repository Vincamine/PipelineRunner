package edu.neu.cs6510.sp25.t1.common.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link PipelineLogger}.
 * Tests logging functionality for different log levels and ensures
 * that log entries are correctly written to the log file.
 */
class PipelineLoggerTest {

  private static final String TEST_LOG_MESSAGE = "Test log message";
  private static final String LOG_FILE_PATH = "logs/pipeline_system.log";

  private Logger pipelineLogger;
  private File logFile;

  /**
   * Sets up the test environment before each test method.
   * Creates necessary directories, initializes loggers, and prepares log capture.
   *
   * @throws IOException If there's an error creating the log directory or file
   */
  @BeforeEach
  void setUp() throws IOException {
    // Create logs directory if it doesn't exist
    Path logDir = Paths.get("logs");
    if (!Files.exists(logDir)) {
      Files.createDirectories(logDir);
    }

    // Get the logger instance
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    pipelineLogger = context.getLogger(PipelineLogger.class);

    // Ensure log file exists
    logFile = new File(LOG_FILE_PATH);
    if (!logFile.exists()) {
      logFile.createNewFile();
    }
  }

  /**
   * Cleans up after each test method.
   */
  @AfterEach
  void tearDown() {
    // No cleanup needed since we removed the ListAppender
  }

  /**
   * Tests the info logging functionality.
   * Verifies that INFO level messages are correctly logged to the file.
   *
   * @throws IOException If there's an error reading the log file
   */
  @Test
  void info_ShouldLogInfoMessage() throws IOException {
    // Arrange
    String message = TEST_LOG_MESSAGE;

    // Act
    PipelineLogger.info(message);

    // Assert - Check that it was written to the file
    String fileContent = readLastLineOfLogFile();
    assertTrue(fileContent.contains("INFO"), "Log in file should have the INFO level");
    assertTrue(fileContent.contains("[PipelineLogger]"), "Log in file should have the prefix");
    assertTrue(fileContent.contains(message), "Log in file should contain the message");
  }

  /**
   * Tests the warning logging functionality.
   * Verifies that WARN level messages are correctly logged to the file.
   *
   * @throws IOException If there's an error reading the log file
   */
  @Test
  void warn_ShouldLogWarningMessage() throws IOException {
    // Arrange
    String message = TEST_LOG_MESSAGE;

    // Act
    PipelineLogger.warn(message);

    // Assert - Check that it was written to the file
    String fileContent = readLastLineOfLogFile();
    assertTrue(fileContent.contains("WARN"), "Log in file should have the WARN level");
    assertTrue(fileContent.contains(message), "Log in file should contain the message");
  }

  /**
   * Tests the error logging functionality.
   * Verifies that ERROR level messages are correctly logged to the file.
   *
   * @throws IOException If there's an error reading the log file
   */
  @Test
  void error_ShouldLogErrorMessage() throws IOException {
    // Arrange
    String message = TEST_LOG_MESSAGE;

    // Act
    PipelineLogger.error(message);

    // Assert - Check that it was written to the file
    String fileContent = readLastLineOfLogFile();
    assertTrue(fileContent.contains("ERROR"), "Log in file should have the ERROR level");
    assertTrue(fileContent.contains(message), "Log in file should contain the message");
  }

  /**
   * Tests the debug logging functionality.
   * Verifies that DEBUG level messages are correctly logged to the file.
   *
   * @throws IOException If there's an error reading the log file
   */
  @Test
  void debug_ShouldLogDebugMessage() throws IOException {
    // Arrange
    String message = TEST_LOG_MESSAGE;

    // Act
    PipelineLogger.debug(message);

    // Assert - Check that it was written to the file
    String fileContent = readLastLineOfLogFile();
    assertTrue(fileContent.contains("DEBUG"), "Log in file should have the DEBUG level");
    assertTrue(fileContent.contains(message), "Log in file should contain the message");
  }

  /**
   * Helper method to read the last line of the log file.
   *
   * @return The last line of the log file
   * @throws IOException If an I/O error occurs
   */
  private String readLastLineOfLogFile() throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
      String lastLine = null;
      String currentLine;

      while ((currentLine = reader.readLine()) != null) {
        lastLine = currentLine;
      }

      return lastLine != null ? lastLine : "";
    }
  }

  /**
   * Tests that the log file exists after logger initialization.
   */
  @Test
  void logFileShouldExist() {
    assertTrue(logFile.exists(), "Log file should exist");
  }

  /**
   * Tests that the logger uses the correct pattern for log messages.
   * Verifies that timestamps and thread information are included.
   *
   * @throws IOException If there's an error reading the log file
   */
  @Test
  void loggerShouldUseCorrectPattern() throws IOException {
    // Arrange & Act
    PipelineLogger.info("Test pattern");

    // Assert
    String logLine = readLastLineOfLogFile();
    assertTrue(logLine.matches("\\[\\d{2}:\\d{2}:\\d{2}\\] \\[.*\\] INFO.*"),
            "Log should match the expected pattern with timestamp and thread");
  }
}