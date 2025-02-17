package edu.neu.cs6510.sp25.t1.util;

import org.yaml.snakeyaml.error.Mark;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides structured error handling for validation errors.
 * Formats error messages with precise location information.
 */
public class ErrorHandler {
    private static final Logger LOGGER = Logger.getLogger(ErrorHandler.class.getName());

    private static final String TYPE_ERROR = "Type Error";
    private static final String DEPENDENCY_ERROR = "Dependency Error";
    private static final String MISSING_FIELD_ERROR = "Missing Field Error";
    private static final String FILE_ERROR = "File Error";

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
         * @param filename The filename where the error occurred.
         * @param line The line number of the error.
         * @param column The column number of the error.
         * @param path The YAML path to the error location.
         */
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
         * Returns a formatted string representation of the location.
         *
         * @return A string formatted as "filename:line:column".
         */
        public String format() {
            return String.format("%s:%d:%d", filename, line, column);
        }
    }

    /**
     * Formats error messages with error type and location.
     *
     * @param location The location where the error occurred.
     * @param errorType The type of error.
     * @param message The error message.
     * @return The formatted error message.
     */
    private static String formatErrorWithType(Location location, String errorType, String message) {
        return String.format("%s: %s - %s", location.format(), errorType, message);
    }

    /**
     * Formats a type mismatch error.
     *
     * @param location The location of the error.
     * @param key The field with incorrect type.
     * @param actualValue The actual value.
     * @param expectedType The expected data type.
     * @return A formatted error message.
     */
    public static String formatTypeError(Location location, String key, Object actualValue, Class<?> expectedType) {
        final String actualType = actualValue != null ? actualValue.getClass().getSimpleName() : "null";
        final String message = String.format("Expected type '%s' for '%s', but got '%s' (%s)",
                expectedType.getSimpleName(), key, actualType, actualValue);
        return formatErrorWithType(location, TYPE_ERROR, message);
    }

    /**
     * Formats a dependency cycle error.
     *
     * @param location The location where the cycle was detected.
     * @param cycle The sequence of jobs forming a cycle.
     * @return A formatted error message.
     */
    public static String formatCycleError(Location location, List<String> cycle) {
        final String cycleStr = String.join(" -> ", cycle) + " -> " + cycle.get(0);
        return formatErrorWithType(location, DEPENDENCY_ERROR, "Dependency cycle detected: " + cycleStr);
    }

    /**
     * Formats a missing field error.
     *
     * @param location The location where the field is missing.
     * @param fieldName The name of the missing field.
     * @return A formatted error message.
     */
    public static String formatMissingFieldError(Location location, String fieldName) {
        return formatErrorWithType(location, MISSING_FIELD_ERROR, "Required field '" + fieldName + "' is missing.");
    }

    /**
     * Formats a file-related error.
     *
     * @param location The file location.
     * @param message The error message.
     * @return A formatted error message.
     */
    public static String formatFileError(Location location, String message) {
        return formatErrorWithType(location, FILE_ERROR, message);
    }

    /**
     * Formats a general exception message.
     *
     * @param location The error location.
     * @param message The error message.
     * @return A formatted exception message.
     */
    public static String formatException(Location location, String message) {
        return formatErrorWithType(location, "Exception", message);
    }

    /**
     * Creates a Location object from a YAML parsing mark.
     *
     * @param filename The file where the error occurred.
     * @param mark The YAML mark containing position info.
     * @param path The path within the YAML structure.
     * @return A Location object.
     */
    public static Location createLocation(String filename, Mark mark, String path) {
        return (mark == null)
                ? new Location(filename, 1, 1, path)
                : new Location(filename, mark.getLine() + 1, mark.getColumn() + 1, path);
    }

    /**
     * Logs an error message and its location.
     *
     * @param message The error message.
     */
    public static void reportError(String message) {
        final StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
        final Location location = new Location(
                stackTrace.getFileName(),
                stackTrace.getLineNumber(),
                1,
                "pipeline.execute"
        );

        String formattedError = formatException(location, message);
        LOGGER.log(Level.SEVERE, formattedError);
    }
}
