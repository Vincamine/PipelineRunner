package edu.neu.cs6510.sp25.t1.common.logging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.ConsoleAppender;

import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

/**
 * Unit tests for the PipelineLogger class.
 */
@ExtendWith(MockitoExtension.class)
public class PipelineLoggerTest {

    @Mock
    private Appender<ILoggingEvent> mockAppender;

    private Logger cicdLogger;
    private Level originalLevel;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    public void setup() {
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        cicdLogger = context.getLogger("cicd-logger");
        originalLevel = cicdLogger.getLevel();

        // Ensure we have a clean logger
        cicdLogger.detachAndStopAllAppenders();

        // Add mock appender
        cicdLogger.addAppender(mockAppender);
        when(mockAppender.getName()).thenReturn("MOCK_APPENDER");
    }

    @AfterEach
    public void teardown() {
        // Restore original console output
        System.setOut(originalOut);

        // Restore original logger
        cicdLogger.setLevel(originalLevel);
        cicdLogger.detachAppender(mockAppender);
    }

    @Test
    public void testInfoLogging() {
        // Act
        PipelineLogger.info("Test info message");

        // Assert
        verify(mockAppender, times(1)).doAppend(argThat(event -> {
            return event.getLevel().equals(Level.INFO) &&
                    event.getFormattedMessage().equals("Test info message");
        }));
    }

    @Test
    public void testWarnLogging() {
        // Act
        PipelineLogger.warn("Test warning message");

        // Assert
        verify(mockAppender, times(1)).doAppend(argThat(event -> {
            return event.getLevel().equals(Level.WARN) &&
                    event.getFormattedMessage().equals("Test warning message");
        }));
    }

    @Test
    public void testErrorLogging() {
        // Act
        PipelineLogger.error("Test error message");

        // Assert
        verify(mockAppender, times(1)).doAppend(argThat(event -> {
            return event.getLevel().equals(Level.ERROR) &&
                    event.getFormattedMessage().equals("Test error message");
        }));
    }

    @Test
    public void testDebugLogging() {
        // Set logger level to DEBUG to ensure our debug messages are captured
        cicdLogger.setLevel(Level.DEBUG);

        // Act
        PipelineLogger.debug("Test debug message");

        // Assert
        verify(mockAppender, times(1)).doAppend(argThat(event -> {
            return event.getLevel().equals(Level.DEBUG) &&
                    event.getFormattedMessage().equals("Test debug message");
        }));
    }

    @Test
    public void testSetVerboseEnabled() {
        // Arrange
        cicdLogger.setLevel(Level.INFO);  // Start with INFO level

        // Act
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
            LoggerContext mockContext = mock(LoggerContext.class);
            Logger mockLogger = mock(Logger.class);

            loggerFactory.when(LoggerFactory::getILoggerFactory).thenReturn(mockContext);
            when(mockContext.getLogger("cicd-logger")).thenReturn(mockLogger);

            PipelineLogger.setVerbose(true);

            // Assert
            verify(mockLogger).setLevel(Level.DEBUG);
        }
    }

    @Test
    public void testSetVerboseDisabled() {
        // Arrange
        cicdLogger.setLevel(Level.DEBUG);  // Start with DEBUG level

        // Act
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
            LoggerContext mockContext = mock(LoggerContext.class);
            Logger mockLogger = mock(Logger.class);

            loggerFactory.when(LoggerFactory::getILoggerFactory).thenReturn(mockContext);
            when(mockContext.getLogger("cicd-logger")).thenReturn(mockLogger);

            PipelineLogger.setVerbose(false);

            // Assert
            verify(mockLogger).setLevel(Level.INFO);
        }
    }

    @Test
    public void testConfigurationCreatesAppenders() {
        // We need to reset the static configuration to test it properly
        try {
            // Use reflection to reset the logger
            Field loggerField = PipelineLogger.class.getDeclaredField("logger");
            loggerField.setAccessible(true);

            // Force reconfiguration
            PipelineLogger.class.getDeclaredMethod("configureLogging").setAccessible(true);
            PipelineLogger.class.getDeclaredMethod("configureLogging").invoke(null);

            // Get the cicd-logger after reconfiguration
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger reconfiguredLogger = context.getLogger("cicd-logger");

            // Check that we have both appenders attached
            boolean hasConsoleAppender = false;
            boolean hasFileAppender = false;

            // Use Iterator instead of enhanced for loop
            java.util.Iterator<Appender<ILoggingEvent>> appenderIterator = reconfiguredLogger.iteratorForAppenders();
            while (appenderIterator.hasNext()) {
                Appender<ILoggingEvent> appender = appenderIterator.next();
                if (appender instanceof ConsoleAppender) {
                    hasConsoleAppender = true;
                } else if (appender instanceof FileAppender) {
                    hasFileAppender = true;
                    FileAppender<ILoggingEvent> fileAppender = (FileAppender<ILoggingEvent>) appender;
                    assertEquals("logs/pipeline_system.log", fileAppender.getFile());
                }
            }

            assertTrue(hasConsoleAppender, "Logger should have a console appender");
            assertTrue(hasFileAppender, "Logger should have a file appender");

        } catch (Exception e) {
            fail("Exception during configuration test: " + e.getMessage());
        }
    }
}