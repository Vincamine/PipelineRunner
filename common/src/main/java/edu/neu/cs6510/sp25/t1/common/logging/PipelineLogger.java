package edu.neu.cs6510.sp25.t1.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

/**
 * Centralized logger for pipeline-related logging.
 * Uses SLF4J with Logback and ensures logs print to the console.
 */
public class PipelineLogger {
  private static final Logger logger = LoggerFactory.getLogger(PipelineLogger.class);

  static {
    configureConsoleLogging();
  }

  /**
   * Configures Logback to ensure logs appear in the console.
   */
  private static void configureConsoleLogging() {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    context.reset();

    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(context);
    encoder.setPattern("[%d{HH:mm:ss}] %-5level - %msg%n"); // Custom log format
    encoder.start();

    ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
    consoleAppender.setContext(context);
    consoleAppender.setEncoder(encoder);
    consoleAppender.start();

    ch.qos.logback.classic.Logger rootLogger = context.getLogger("edu.neu.cs6510.sp25.t1");
    rootLogger.setLevel(Level.DEBUG); // Ensure all levels print
    rootLogger.addAppender(consoleAppender);
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
