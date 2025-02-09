package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.cli.model.LogEntry;
import edu.neu.cs6510.sp25.t1.cli.model.LogLevel;
import edu.neu.cs6510.sp25.t1.cli.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogCommandTest {
    @Mock
    private LogService mockLogService;

    private CommandLine commandLine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandLine = new CommandLine(new LogCommand(mockLogService));
    }

    @Test
    void testRun_SuccessfulLogs() {
        final List<LogEntry> mockLogs = List.of(
            new LogEntry("123", LogLevel.INFO, "Pipeline execution started", System.currentTimeMillis())
        );

        when(mockLogService.getLogsByPipelineId("123")).thenReturn(mockLogs);

        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        commandLine.execute("--id", "123");

        assertTrue(outContent.toString().contains("Pipeline execution started"));
    }

    @Test
    void testRun_NoLogsFound() {
        when(mockLogService.getLogsByPipelineId("123")).thenReturn(List.of());

        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        commandLine.execute("--id", "123");

        assertTrue(outContent.toString().contains("No logs found for pipeline ID: 123"));
    }
}
