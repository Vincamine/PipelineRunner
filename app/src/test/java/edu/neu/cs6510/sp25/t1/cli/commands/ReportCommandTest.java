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

class ReportCommandTest {
    private ReportService mockReportService;
    private ReportCommand reportCommand;

    @BeforeEach
    void setUp() {
        mockReportService = mock(ReportService.class);
        reportCommand = new ReportCommand(mockReportService);
    }

    /** Ensures reports are retrieved and printed correctly */
    @Test
    void testReports_ValidPipeline_Success() {
        final ReportEntry mockReport = new ReportEntry("123", ReportLevel.SUCCESS, "Pipeline executed successfully",
            Instant.now().toEpochMilli(), "SUCCESS", Collections.emptyList());
        when(mockReportService.getPipelineRuns("repo-url", "123")).thenReturn(List.of(mockReport));

        try (MockedStatic<ReportFormatter> reportFormatterMock = mockStatic(ReportFormatter.class)) {
            reportFormatterMock.when(() -> ReportFormatter.format(mockReport))
                .thenReturn("SUCCESS: Pipeline executed successfully");

            final CommandLine cmd = new CommandLine(reportCommand);
            final int exitCode = cmd.execute("--repo", "repo-url", "--pipeline", "123");

            assertEquals(0, exitCode, "Reports should be printed successfully for a valid pipeline.");
        }
    }

    /** Ensures correct handling when no reports exist */
    @Test
    void testReports_NoReportsFound() {
        when(mockReportService.getPipelineRuns("repo-url", "123")).thenReturn(Collections.emptyList());

        final CommandLine cmd = new CommandLine(reportCommand);
        final int exitCode = cmd.execute("--repo", "repo-url", "--pipeline", "123");

        assertEquals(0, exitCode, "Command should execute successfully but print 'No reports found'.");
    }

    /** Ensures an error is printed when repository URL is missing */
    @Test
    void testReports_MissingRepoUrl() {
        final CommandLine cmd = new CommandLine(reportCommand);
        final int exitCode = cmd.execute();

        assertNotEquals(0, exitCode, "Command should fail when repository URL is missing.");
    }

    /** Ensures exception handling works properly */
    @Test
    void testReports_ServiceThrowsException() {
        when(mockReportService.getPipelineRuns("repo-url", "123")).thenThrow(new RuntimeException("Database error"));

        try (MockedStatic<ErrorHandler> errorHandlerMock = mockStatic(ErrorHandler.class)) {
            final CommandLine cmd = new CommandLine(reportCommand);
            final int exitCode = cmd.execute("--repo", "repo-url", "--pipeline", "123");

            errorHandlerMock.verify(() -> ErrorHandler.reportError("Database error"), times(1));

            assertEquals(0, exitCode, "Command should not crash, but should report an error.");
        }
    }
}
