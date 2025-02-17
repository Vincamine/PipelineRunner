package edu.neu.cs6510.sp25.t1.util;

import org.yaml.snakeyaml.error.Mark;
import java.util.List;

/**
 * Provides structured error handling for YAML validation errors.
 * Formats error messages with precise location information.
 */
public class ErrorHandler {

  private static final String TYPE_ERROR = "type error";
  private static final String DEPENDENCY_ERROR = "dependency error";
  private static final String MISSING_FIELD_ERROR = "missing field error";
  private static final String FILE_ERROR = "file error";

  /**
   * Represents a specific location in a YAML file.
   */
  public static class Location {
    private final String filename;
    private final int line;
    private final int column;
    private final String path;

    public Location(String filename, int line, int column, String path) {
      this.filename = filename;
      this.line = line;
      this.column = column;
      this.path = path;
    }

    public String getFilename() { return filename; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public String getPath() { return path; }

    /**
     * Returns formatted location string for easier debugging.
     */
    public String format() {
      return String.format("%s:%d:%d", filename, line, column);
    }
  }

  /**
   * Formats error messages with error type and location.
   */
  private static String formatErrorWithType(Location location, String errorType, String message) {
    return String.format("%s: %s, %s", location.format(), errorType, message);
  }

  public static String formatTypeError(Location location, String key, Object actualValue, Class<?> expectedType) {
    final String actualType = actualValue != null ? actualValue.getClass().getSimpleName() : "null";
    final String message = String.format("Expected type %s for '%s', but got %s (%s)",
        expectedType.getSimpleName(), key, actualType, actualValue);
    return formatErrorWithType(location, TYPE_ERROR, message);
  }

  public static String formatCycleError(Location location, List<String> cycle) {
    final String cycleStr = String.join(" -> ", cycle) + " -> " + cycle.get(0);
    return formatErrorWithType(location, DEPENDENCY_ERROR, String.format("Dependency cycle detected: %s", cycleStr));
  }

  public static String formatMissingFieldError(Location location, String fieldName) {
    return formatErrorWithType(location, MISSING_FIELD_ERROR, "Required field '" + fieldName + "' is missing.");
  }

  public static String formatFileError(Location location, String message) {
    return formatErrorWithType(location, FILE_ERROR, message);
  }

  public static String formatException(Location location, String message) {
    return formatErrorWithType(location, "exception", message);
  }

  /**
   * Creates a Location object from a YAML parsing mark.
   */
  public static Location createLocation(String filename, Mark mark, String path) {
    return (mark == null)
        ? new Location(filename, 1, 1, path)
        : new Location(filename, mark.getLine() + 1, mark.getColumn() + 1, path);
  }

  public static void reportError(String message) {
    final StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
    final Location location = new Location(
        stackTrace.getFileName(),
        stackTrace.getLineNumber(),
        1,
        "pipeline.execute"
    );
    System.err.println(formatException(location, message));
  }
}
