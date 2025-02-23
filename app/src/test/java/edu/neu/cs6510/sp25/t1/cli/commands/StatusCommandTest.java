package edu.neu.cs6510.sp25.t1.cli.commands;

import edu.neu.cs6510.sp25.t1.model.PipelineState;
import edu.neu.cs6510.sp25.t1.model.PipelineStatus;
import edu.neu.cs6510.sp25.t1.model.StageInfo;
import edu.neu.cs6510.sp25.t1.model.JobInfo;
import edu.neu.cs6510.sp25.t1.service.StatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

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
        // Mock pipeline status with the new constructor that includes stages and jobs
        final PipelineStatus mockStatus = new PipelineStatus(
                "123",
                PipelineState.RUNNING,
                50, // Progress percentage
                "Pipeline is running...",
                List.of(
                        new StageInfo("Build", "SUCCESS", System.currentTimeMillis() - 5000, System.currentTimeMillis()),
                        new StageInfo("Test", "RUNNING", System.currentTimeMillis() - 3000, 0) // 0 if not completed
                ),
                List.of(
                        new JobInfo("Compile", "SUCCESS", false),
                        new JobInfo("Unit Test", "RUNNING", false)
                )
        );
    
        doReturn(mockStatus).when(statusService).getPipelineStatus(anyString());
    
        final int exitCode = new CommandLine(statusCommand).execute("--pipeline-id", "123");
        assertEquals(0, exitCode);
    }    
}
