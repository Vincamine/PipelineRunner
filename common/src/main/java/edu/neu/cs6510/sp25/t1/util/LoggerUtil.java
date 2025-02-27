package edu.neu.cs6510.sp25.t1.util;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger utility for standardized logging across the CLI.
 * Provides support for different log levels and formatted timestamps.
 */
public class LoggerUtil {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Logs an informational message.
     *
     * @param message The message to log.
     */
    public static void info(String message) {
        log(System.out, "INFO", message);
    }

    /**
     * Logs a warning message.
     *
     * @param message The warning message.
     */
    public static void warn(String message) {
        log(System.out, "WARN", message);
    }

    /**
     * Logs an error message.
     *
     * @param message The error message.
     */
    public static void error(String message) {
        log(System.err, "ERROR", message);
    }

    /**
     * Logs an error message with an exception stack trace.
     *
     * @param message The error message.
     * @param exception The exception to log.
     */
    public static void error(String message, Throwable exception) {
        log(System.err, "ERROR", message);
        exception.printStackTrace(System.err);
    }

    /**
     * Internal logging function that prints formatted log messages.
     *
     * @param stream The output stream (System.out or System.err).
     * @param level The log level (INFO, WARN, ERROR).
     * @param message The message to log.
     */
    private static void log(PrintStream stream, String level, String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        stream.println(String.format("[%s] [%s] %s", timestamp, level, message));
    }
}
