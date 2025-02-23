package edu.neu.cs6510.sp25.t1.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReportErrorHandler class.
 * Tests error reporting and formatting functionality.
 */
class ReportErrorHandlerTest {
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 2, 22, 10, 15, 30);
    private static final String TIMESTAMP = FIXED_TIME.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @BeforeEach
    void setUp() {
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setErr(originalErr);
    }

    /**
     * Tests reporting an invalid repository error.
     */
    @Test
    void testReportInvalidRepository() {
        try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
            mockedTime.when(LocalDateTime::now).thenReturn(FIXED_TIME);

            String repoUrl = "https://invalid-repo.com";
            Exception cause = new IllegalArgumentException("Invalid URL format");
            ReportErrorHandler.reportInvalidRepository(repoUrl, cause);

            String expected = String.format("[%s] Error: Invalid repository: %s\n[%s] Cause: Invalid URL format\n",
                    TIMESTAMP, repoUrl, TIMESTAMP);
            assertEquals(expected, errContent.toString());
        }
    }

    /**
     * Tests reporting a missing pipeline error.
     */
    @Test
    void testReportMissingPipeline() {
        try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
            mockedTime.when(LocalDateTime::now).thenReturn(FIXED_TIME);

            String pipelineId = "non-existent-pipeline";
            ReportErrorHandler.reportMissingPipeline(pipelineId);

            String expected = String.format("[%s] Error: Pipeline not found: %s\n",
                    TIMESTAMP, pipelineId);
            assertEquals(expected, errContent.toString());
        }
    }

    /**
     * Tests reporting a pipeline run not found error.
     */
    @Test
    void testReportPipelineRunNotFound() {
        try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
            mockedTime.when(LocalDateTime::now).thenReturn(FIXED_TIME);

            String pipelineId = "test-pipeline";
            int runNumber = 123;
            ReportErrorHandler.reportPipelineRunNotFound(pipelineId, runNumber);

            String expected = String.format("[%s] Error: Pipeline run #%d not found for pipeline: %s\n",
                    TIMESTAMP, runNumber, pipelineId);
            assertEquals(expected, errContent.toString());
        }
    }

    /**
     * Tests reporting an invalid format error.
     */
    @Test
    void testReportInvalidFormat() {
        try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
            mockedTime.when(LocalDateTime::now).thenReturn(FIXED_TIME);

            String details = "Missing required field";
            Exception cause = new IllegalStateException("Parse error");
            ReportErrorHandler.reportInvalidFormat(details, cause);

            String expected = String.format("[%s] Error: Invalid report format: %s\n[%s] Cause: Parse error\n",
                    TIMESTAMP, details, TIMESTAMP);
            assertEquals(expected, errContent.toString());
        }
    }

    /**
     * Tests reporting a repository access error.
     */
    @Test
    void testReportRepositoryAccessError() {
        try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
            mockedTime.when(LocalDateTime::now).thenReturn(FIXED_TIME);

            String repoUrl = "https://repo.com";
            Exception cause = new SecurityException("Access denied");
            ReportErrorHandler.reportRepositoryAccessError(repoUrl, cause);

            String expected = String.format("[%s] Error: Unable to access repository: %s\n[%s] Cause: Access denied\n",
                    TIMESTAMP, repoUrl, TIMESTAMP);
            assertEquals(expected, errContent.toString());
        }
    }

    /**
     * Tests reporting a simple error message without cause.
     */
    @Test
    void testReportError_MessageOnly() {
        try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
            mockedTime.when(LocalDateTime::now).thenReturn(FIXED_TIME);

            String message = "Simple error message";
            ReportErrorHandler.reportError(message);

            String expected = String.format("[%s] Error: %s\n",
                    TIMESTAMP, message);
            assertEquals(expected, errContent.toString());
        }
    }

    /**
     * Tests reporting an error message with cause.
     */
    @Test
    void testReportError_WithCause() {
        try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
            mockedTime.when(LocalDateTime::now).thenReturn(FIXED_TIME);

            String message = "Error with cause";
            Exception cause = new RuntimeException("Root cause");
            ReportErrorHandler.reportError(message, cause);

            String expected = String.format("[%s] Error: %s\n[%s] Cause: Root cause\n",
                    TIMESTAMP, message, TIMESTAMP);
            assertEquals(expected, errContent.toString());
        }
    }

    /**
     * Tests formatting an error message with context.
     */
    @Test
    void testFormatErrorMessage() {
        String message = "Base error";
        String context = "Additional info";
        String formatted = ReportErrorHandler.formatErrorMessage(message, context);
        assertEquals("Base error (Context: Additional info)", formatted);
    }

    /**
     * Tests debug mode with stack trace printing.
     */
    @Test
    void testDebugMode() {
        try {
            System.setProperty("app.debug", "true");
            try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
                mockedTime.when(LocalDateTime::now).thenReturn(FIXED_TIME);

                Exception cause = new RuntimeException("Debug test");
                ReportErrorHandler.reportError("Debug error", cause);

                String output = errContent.toString();
                assertTrue(output.contains("Debug error"));
                assertTrue(output.contains("Debug test"));
                assertTrue(output.contains("java.lang.RuntimeException"));
            }
        } finally {
            System.clearProperty("app.debug");
        }
    }
}