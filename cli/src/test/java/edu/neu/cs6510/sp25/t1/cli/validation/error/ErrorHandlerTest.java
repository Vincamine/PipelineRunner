package edu.neu.cs6510.sp25.t1.cli.validation.error;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ErrorHandlerTest {

  // Custom handler to capture log output
  private ByteArrayOutputStream logOutput;
  private Handler customLogHandler;
  private Logger rootLogger;

  @BeforeEach
  public void setUp() {
    // Set up logging capture
    logOutput = new ByteArrayOutputStream();
    customLogHandler = new StreamHandler(logOutput, new java.util.logging.SimpleFormatter());
    rootLogger = Logger.getLogger("");
    rootLogger.addHandler(customLogHandler);

    // Set log level to ensure SEVERE messages are logged
    rootLogger.setLevel(Level.SEVERE);
  }

  @AfterEach
  public void tearDown() {
    // Clean up
    rootLogger.removeHandler(customLogHandler);
    customLogHandler.close();
  }

  @Test
  public void testLocationConstructorAndGetters() {
    // Create a Location object
    ErrorHandler.Location location = new ErrorHandler.Location("test.yml", 10, 5, "$.stages[0].jobs[0]");

    // Verify path getter
    assertEquals("$.stages[0].jobs[0]", location.getPath());

    // Verify format method
    assertEquals("test.yml:10:5", location.format());
  }

  @Test
  public void testFormatValidationError() {
    // Create a Location object
    ErrorHandler.Location location = new ErrorHandler.Location("pipeline.yml", 15, 3, "$.stages[1]");

    // Test formatValidationError
    String errorMessage = "Invalid stage configuration";
    String formattedError = ErrorHandler.formatValidationError(location, errorMessage);

    // Verify the formatted message
    assertEquals("pipeline.yml:15:3: Invalid stage configuration", formattedError);
  }

  @Test
  public void testFormatValidationErrorWithSpecialChars() {
    // Test with special characters in the filename and message
    ErrorHandler.Location location = new ErrorHandler.Location("special-chars_file.yaml", 1, 1, "$");
    String errorMessage = "Error with special chars: <>&\"'";

    String formattedError = ErrorHandler.formatValidationError(location, errorMessage);

    // Verify the formatted message includes special chars correctly
    assertEquals("special-chars_file.yaml:1:1: Error with special chars: <>&\"'", formattedError);
  }

  @Test
  public void testFormatValidationErrorWithZeroLineColumn() {
    // Test with zero line and column values
    ErrorHandler.Location location = new ErrorHandler.Location("zero.yml", 0, 0, "$.root");

    String formattedError = ErrorHandler.formatValidationError(location, "Zero position error");

    // Verify the formatted message
    assertEquals("zero.yml:0:0: Zero position error", formattedError);
  }

  @Test
  public void testFormatValidationErrorWithEmptyPath() {
    // Test with an empty path
    ErrorHandler.Location location = new ErrorHandler.Location("empty-path.yml", 5, 10, "");

    String formattedError = ErrorHandler.formatValidationError(location, "Empty path error");

    // Verify the formatted message
    assertEquals("empty-path.yml:5:10: Empty path error", formattedError);
  }

  @Test
  public void testLogError() {
    // Clear any existing output
    customLogHandler.flush();
    logOutput.reset();

    // Call the method to test
    String errorMessage = "Test error message";
    ErrorHandler.logError(errorMessage);

    // Flush the handler to ensure output is captured
    customLogHandler.flush();

    // Get logged output
    String capturedLog = logOutput.toString();

    // Verify log message was recorded (contains our message)
    assertTrue(capturedLog.contains(errorMessage));
    assertTrue(capturedLog.contains("SEVERE"));
  }

  @Test
  public void testLogErrorWithEmptyMessage() {
    // Clear any existing output
    customLogHandler.flush();
    logOutput.reset();

    // Call the method to test
    ErrorHandler.logError("");

    // Flush the handler to ensure output is captured
    customLogHandler.flush();

    // Get logged output
    String capturedLog = logOutput.toString();

    // Verify the logger captured the empty message
    assertTrue(capturedLog.contains("SEVERE"));
  }

  @Test
  public void testLogErrorWithNullMessage() {
    // Clear any existing output
    customLogHandler.flush();
    logOutput.reset();

    // Call the method to test
    ErrorHandler.logError(null);

    // Flush the handler to ensure output is captured
    customLogHandler.flush();

    // Get logged output
    String capturedLog = logOutput.toString();

    // Verify the logger captured the null message (will be shown as "null" in most loggers)
    assertTrue(capturedLog.contains("SEVERE"));
    assertTrue(capturedLog.contains("null") || capturedLog.endsWith("SEVERE: \n"));
  }

  @Test
  public void testLocationFormatWithNullFilename() {
    // Test Location with null filename
    ErrorHandler.Location location = new ErrorHandler.Location(null, 1, 1, "$.path");

    // Should handle null gracefully
    String formatted = location.format();

    // Verify the result contains "null" as the filename
    assertEquals("null:1:1", formatted);
  }
}