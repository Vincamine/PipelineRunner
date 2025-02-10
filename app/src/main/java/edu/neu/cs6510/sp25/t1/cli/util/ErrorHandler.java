package edu.neu.cs6510.sp25.t1.cli.util;

import org.yaml.snakeyaml.error.Mark;
import java.util.List;

/**
 * Enhanced error handler for YAML validation that provides consistent error formatting
 * and detailed context information for all validation errors.
 */
public class ErrorHandler {
  private static final String DEFAULT_FILENAME = "pipeline.yaml";

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
    final String message = String.format("Wrong type for value '%s' in key '%s', expected %s but got %s",
        actualValue, key, expectedType.getSimpleName(), actualType);
    return String.format("%s: %s", location.format(), message);
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
    return String.format("%s: Dependency cycle detected: %s", location.format(), cycleStr);
  }

  /**
   * Formats missing field error messages.
   *
   * @param location The location where the missing field should be
   * @param fieldName The name of the missing field
   * @return Formatted error message
   */
  public static String formatMissingFieldError(Location location, String fieldName) {
    return String.format("%s: Missing required field '%s'", location.format(), fieldName);
  }

  /**
   * Creates a Location object from a SnakeYAML Mark.
   *
   * @param mark The SnakeYAML mark containing position information
   * @param path The YAML path to the current location
   * @return A new Location object
   */
  public static Location createLocation(Mark mark, String path) {
    if (mark == null) {
      return new Location(DEFAULT_FILENAME, 1, 1, path);
    }
    return new Location(DEFAULT_FILENAME, mark.getLine() + 1, mark.getColumn() + 1, path);
  }
}