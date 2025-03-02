package edu.neu.cs6510.sp25.t1.common.validation.error;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility for structured error handling in validation and YAML parsing.
 * <p>
 * Provides:
 * - **Structured error locations** (`ErrorHandler.Location`).
 * - **Standardized error formatting** for validation and parsing issues.
 * - **Consistent logging for debugging**.
 */
public class ErrorHandler {
  private static final Logger LOGGER = Logger.getLogger(ErrorHandler.class.getName());

  private static final String VALIDATION_ERROR = "Validation Error";

  /**
   * Represents a specific location in a YAML file.
   */
  public static class Location {
    private final String filename;
    private final int line;
    private final int column;
    private final String path;

    /**
     * Constructs a Location object.
     *
     * @param filename The file where the error occurred.
     * @param line     The line number of the error.
     * @param column   The column number of the error.
     * @param path     The YAML path to the error location.
     */
    public Location(String filename, int line, int column, String path) {
      this.filename = filename;
      this.line = line;
      this.column = column;
      this.path = path;
    }

    /**
     * @return The path to the error location.
     */
    public String getPath() {
      return path;
    }

    /**
     * @return A formatted string representation as `filename:line:column`
     */
    public String format() {
      return String.format("%s:%d:%d", filename, line, column);
    }
  }

  /**
   * Formats an error message with type and location.
   *
   * @param location The location where the error occurred.
   * @param message  The error message.
   * @return The formatted error message.
   */
  private static String formatError(Location location, String message) {
    return String.format("%s: %s - %s", location.format(), ErrorHandler.VALIDATION_ERROR, message);
  }

  /**
   * Formats a validation error.
   *
   * @param location The location of the error.
   * @param message  The validation error message.
   * @return A formatted validation error message.
   */
  public static String formatValidationError(Location location, String message) {
    return formatError(location, message);
  }

  /**
   * Logs an error message and its location.
   *
   * @param message The error message.
   */
  public static void logError(String message) {
    LOGGER.log(Level.SEVERE, message);
  }
}
