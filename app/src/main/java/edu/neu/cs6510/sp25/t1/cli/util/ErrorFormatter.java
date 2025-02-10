package edu.neu.cs6510.sp25.t1.cli.util;

/**
 * Utility class for formatting error messages in a structured format.
 * 
 * <p>
 * Ensures that all error messages follow the standardized format:
 * {@code <filename>:<line>:<col>: <error-message>}
 * </p>
 *
 * <h2>Usage:</h2>
 * <pre>
 * String formattedError = ErrorFormatter.format("pipeline.yaml", 10, 22, "Invalid value for key 'name'.");
 * System.out.println(formattedError);
 * // Output: pipeline.yaml:10:22: Invalid value for key 'name'.
 * </pre>
 */
public class ErrorFormatter {

    /**
     * Formats an error message using the standard structure.
     *
     * @param filename The name of the file where the error occurred.
     * @param line The line number of the error.
     * @param column The column number of the error.
     * @param message The error message.
     * @return A formatted string in the format {@code <filename>:<line>:<col>: <error-message>}
     */
    public static String format(String filename, int line, int column, String message) {
        return String.format("%s:%d:%d: %s", filename, line, column, message);
    }
}
