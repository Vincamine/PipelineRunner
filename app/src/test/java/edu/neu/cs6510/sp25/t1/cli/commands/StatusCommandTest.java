package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.PipelineStatus;
import edu.neu.cs6510.sp25.t1.service.StatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class StatusCommandTest {

    private StatusCommand statusCommand;
    private StatusService statusService;

    @BeforeEach
    void setUp() {
        statusService = mock(StatusService.class);
        statusCommand = new StatusCommand(statusService);
    }

    @Test
    void testStatus_ValidPipeline_Success() {
        final PipelineStatus mockStatus = new PipelineStatus("123");
        doReturn(mockStatus).when(statusService).getPipelineStatus(anyString());

        final int exitCode = new CommandLine(statusCommand).execute("--pipeline-id", "123");
        assertEquals(0, exitCode);
    }
}
