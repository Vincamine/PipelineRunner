package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.LogEntry;
import edu.neu.cs6510.sp25.t1.model.LogLevel;
import edu.neu.cs6510.sp25.t1.service.LogService;
import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.util.LogFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import picocli.CommandLine;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogCommandTest {
    private LogService mockLogService;
    private LogCommand logCommand;

    @BeforeEach
    void setUp() {
        mockLogService = mock(LogService.class);
        logCommand = new LogCommand(mockLogService);
    }

    /** Ensures logs are retrieved and printed correctly */
    @Test
    void testLogs_ValidPipeline_Success() {
        final LogEntry mockLog = new LogEntry("123", LogLevel.INFO, "Pipeline executed successfully", Instant.now().toEpochMilli());
        when(mockLogService.getLogsByPipelineId("123")).thenReturn(List.of(mockLog));

        try (MockedStatic<LogFormatter> logFormatterMock = mockStatic(LogFormatter.class)) {
            logFormatterMock.when(() -> LogFormatter.format(mockLog)).thenReturn("INFO: Pipeline executed successfully");

            final CommandLine cmd = new CommandLine(logCommand);
            final int exitCode = cmd.execute("--id", "123");

            assertEquals(0, exitCode, "Logs should be printed successfully for a valid pipeline.");
        }
    }

    /** Ensures correct handling when no logs exist */
    @Test
    void testLogs_NoLogsFound() {
        when(mockLogService.getLogsByPipelineId("123")).thenReturn(Collections.emptyList());

        final CommandLine cmd = new CommandLine(logCommand);
        final int exitCode = cmd.execute("--id", "123");

        assertEquals(0, exitCode, "Command should execute successfully but print 'No logs found'.");
    }

    /** Ensures an error is printed when pipeline ID is missing */
    @Test
    void testLogs_MissingPipelineId() {
        final CommandLine cmd = new CommandLine(logCommand);
        final int exitCode = cmd.execute();

        assertNotEquals(0, exitCode, "Command should fail when pipeline ID is missing.");
    }

    /** Ensures exception handling works properly */
    @Test
    void testLogs_ServiceThrowsException() {
        when(mockLogService.getLogsByPipelineId("123")).thenThrow(new RuntimeException("Database error"));

        try (MockedStatic<ErrorHandler> errorHandlerMock = mockStatic(ErrorHandler.class)) {
            final CommandLine cmd = new CommandLine(logCommand);
            final int exitCode = cmd.execute("--id", "123");

            errorHandlerMock.verify(() -> ErrorHandler.reportError("Database error"), times(1));

            assertEquals(0, exitCode, "Command should not crash, but should report an error.");
        }
    }
}
