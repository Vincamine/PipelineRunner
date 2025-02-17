package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.LogEntry;
import edu.neu.cs6510.sp25.t1.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LogCommandTest {

    private LogCommand logCommand;
    private LogService logService;

    @BeforeEach
    void setUp() {
        logService = mock(LogService.class);
        logCommand = new LogCommand(logService);
    }

    @Test
    void testLogs_ValidPipeline_Success() {
        final List<LogEntry> mockLogs = List.of(new LogEntry(null, null, null, 0));
        doReturn(mockLogs).when(logService).getLogsByPipelineId(anyString());

        final int exitCode = new CommandLine(logCommand).execute("--id", "123");
        assertEquals(0, exitCode);
    }

    @Test
    void testLogs_NoLogs_Failure() {
        doReturn(List.of()).when(logService).getLogsByPipelineId(anyString());

        final int exitCode = new CommandLine(logCommand).execute("--id", "123");
        assertNotEquals(0, exitCode);
    }
}
