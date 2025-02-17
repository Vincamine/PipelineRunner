package edu.neu.cs6510.sp25.t1.util;



import org.yaml.snakeyaml.error.Mark;
import java.util.List;


import org.yaml.snakeyaml.error.Mark;
import java.util.List;

/**
 * Enhanced error handler for YAML validation that provides consistent error formatting
 * and detailed context information for all validation errors.
 */
public class ErrorHandler {

  // Error type constants
  private static final String TYPE_ERROR = "type error";
  private static final String DEPENDENCY_ERROR = "dependency error";
  private static final String MISSING_FIELD_ERROR = "missing field error";
  private static final String FILE_ERROR = "file error";

  /**
   * Represents a location within a YAML document, including file position and path context.
   */
  public static class Location {
    private final String filename;
    private final int line;
    private final int column;
    private final String path;  // YAML path, e.g., "jobs[0].name"

    public Location(String filename, int line, int column, String path) {
      this.filename = filename;
      this.line = line;
      this.column = column;
      this.path = path;
    }

    public String getFilename() {
      return filename;
    }

    public int getLine() {
      return line;
    }

    public int getColumn() {
      return column;
    }

    public String getPath() {
      return path;
    }

    /**
     * Formats the location according to IDE-friendly format.
     * @return String in format "filename:line:column"
     */
    public String format() {
      return String.format("%s:%d:%d", filename, line, column);
    }
  }

  /**
   * Base method for formatting errors with type prefix
   */
  private static String formatErrorWithType(Location location, String errorType, String message) {
    return String.format("%s: %s, %s", location.format(), errorType, message);
  }

  /**
   * Formats type mismatch error messages.
   *
   * @param location The location where the error occurred
   * @param key The key with the wrong type
   * @param actualValue The actual value provided
   * @param expectedType The expected type for the value
   * @return Formatted error message
   */
  public static String formatTypeError(Location location, String key, Object actualValue, Class<?> expectedType) {
    final String actualType = actualValue != null ? actualValue.getClass().getSimpleName() : "null";
    final String message = String.format("wrong type for value '%s' in key '%s', expected %s but got %s",
        actualValue, key, expectedType.getSimpleName(), actualType);
    return formatErrorWithType(location, TYPE_ERROR, message);
  }

  /**
   * Formats dependency cycle error messages.
   *
   * @param location The location where the cycle was detected
   * @param cycle List of job names forming the cycle
   * @return Formatted error message
   */
  public static String formatCycleError(Location location, List<String> cycle) {
    final String cycleStr = String.join(" -> ", cycle) + " -> " + cycle.get(0);
    return formatErrorWithType(location, DEPENDENCY_ERROR, String.format("cycle detected in: %s", cycleStr));
  }

  /**
   * Formats missing field error messages.
   *
   * @param location The location where the missing field should be
   * @param fieldName The name of the missing field
   * @return Formatted error message
   */
  public static String formatMissingFieldError(Location location, String fieldName) {
    return formatErrorWithType(location, MISSING_FIELD_ERROR, String.format("required field '%s' not found", fieldName));
  }

  /**
   * Formats file-related error messages.
   *
   * @param location The location in the file
   * @param message The error message
   * @return Formatted error message
   */
  public static String formatFileError(Location location, String message) {
    return formatErrorWithType(location, FILE_ERROR, message);
  }

  /**
   * Formats general exception messages.
   *
   * @param location The location where the exception occurred
   * @param message The exception message
   * @return Formatted error message
   */
  public static String formatException(Location location, String message) {
    return formatErrorWithType(location, "error", message);
  }

  /**
   * Creates a Location object from a SnakeYAML Mark.
   *
   * @param mark The SnakeYAML mark containing position information
   * @param path The YAML path to the current location
   * @return A new Location object
   */
  public static Location createLocation(String filename, Mark mark, String path) {
//    if (mark == null) {
//      return new Location("DEFAULT_FILENAME", 1, 1, path);
//    }
//    return new Location("DEFAULT_FILENAME", mark.getLine() + 1, mark.getColumn() + 1, path);

//    final String filename = (mark != null && mark.getName() != null) ? mark.getName() : "unknown.yaml";
//    return new Location(filename, mark != null ? mark.getLine() + 1 : 1, mark != null ? mark.getColumn() + 1 : 1, path);
    {
      if (mark == null) {
        return new Location(filename, 1, 1, path);
      }
      return new Location(filename, mark.getLine() + 1, mark.getColumn() + 1, path);
  }}

  /**
   * Reports an error with stack trace information.
   *
   * @param message The error message to report
   */
  public static void reportError(String message) {
    final StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[1];
    final Location location = new Location(
        stackTrace.getFileName(),
        stackTrace.getLineNumber(),
        1,
        "pipeline.execute"
    );
    System.err.println(formatException(location, message));
  }
}
