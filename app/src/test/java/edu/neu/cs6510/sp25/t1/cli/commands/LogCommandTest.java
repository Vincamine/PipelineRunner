package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.ReportEntry;
import edu.neu.cs6510.sp25.t1.model.ReportLevel;
import edu.neu.cs6510.sp25.t1.service.ReportService;
import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.util.ReportFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import picocli.CommandLine;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

class LogCommandTest {
    private ReportService mockLogService;
    private ReportCommand logCommand;

    @BeforeEach
    void setUp() {
        mockLogService = mock(ReportService.class);
        logCommand = new ReportCommand(mockLogService);
    }

    /** Ensures logs are retrieved and printed correctly */
    @Test
    void testLogs_ValidPipeline_Success() {
        final ReportEntry mockLog = new ReportEntry("123", ReportLevel.SUCCESS, "Pipeline executed successfully",
                Instant.now().toEpochMilli());
        when(mockLogService.getLogsByPipelineId("123")).thenReturn(List.of(mockLog));

        try (MockedStatic<ReportFormatter> logFormatterMock = mockStatic(ReportFormatter.class)) {
            logFormatterMock.when(() -> ReportFormatter.format(mockLog))
                    .thenReturn("SUCCESSSUCCESS: Pipeline executed successfully");

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
