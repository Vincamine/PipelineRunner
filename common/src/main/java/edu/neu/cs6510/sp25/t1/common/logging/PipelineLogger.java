package edu.neu.cs6510.sp25.t1.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;

/**
 * Centralized logger for pipeline-related logging.
 * Uses SLF4J with Logback and ensures logs print to the console and a log file.
 */
public class PipelineLogger {
  private static final Logger logger = LoggerFactory.getLogger(PipelineLogger.class);
  private static final String LOG_FILE_PATH = "logs/pipeline_system.log"; // Log file path

  static {
    configureLogging();
  }

  /**
   * Configures Logback to ensure logs appear in the console and a log file.
   */
  private static void configureLogging() {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    context.reset();

    // Define log pattern (includes time, level, thread, and message)
    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(context);
    encoder.setPattern("[%d{HH:mm:ss}] [%thread] %-5level %logger{36} - %msg%n"); // Enhanced log format
    encoder.start();

    // Console Appender
    ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
    consoleAppender.setContext(context);
    consoleAppender.setEncoder(encoder);
    consoleAppender.start();

    // File Appender (logs to a file)
    FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
    fileAppender.setContext(context);
    fileAppender.setFile(LOG_FILE_PATH);
    fileAppender.setEncoder(encoder);
    fileAppender.start();

    // Get the root logger and attach appenders
    ch.qos.logback.classic.Logger rootLogger = context.getLogger("edu.neu.cs6510.sp25.t1");
    rootLogger.setLevel(Level.DEBUG); // Capture DEBUG and above
    rootLogger.addAppender(consoleAppender);
    rootLogger.addAppender(fileAppender);
  }

  /**
   * Logs an informational message.
   *
   * @param message The message to log.
   */
  public static void info(String message) {
    logger.info("[PipelineLogger] {}", message);
  }

  /**
   * Logs a warning message.
   *
   * @param message The message to log.
   */
  public static void warn(String message) {
    logger.warn("[PipelineLogger] {}", message);
  }

  /**
   * Logs an error message.
   *
   * @param message The message to log.
   */
  public static void error(String message) {
    logger.error("[PipelineLogger] {}", message);
  }

  /**
   * Logs a debug message.
   *
   * @param message The message to log.
   */
  public static void debug(String message) {
    logger.debug("[PipelineLogger] {}", message);
  }
}
