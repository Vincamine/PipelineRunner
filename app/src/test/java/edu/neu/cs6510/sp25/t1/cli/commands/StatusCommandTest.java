package edu.neu.cs6510.sp25.t1.cli.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import edu.neu.cs6510.sp25.t1.cli.model.PipelineState;
import edu.neu.cs6510.sp25.t1.cli.model.PipelineStatus;
import edu.neu.cs6510.sp25.t1.cli.service.StatusService;

@ExtendWith(MockitoExtension.class)
public class StatusCommandTest {

    @Mock
    private StatusService statusService;
    private StatusCommand statusCommand;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
        statusCommand = new StatusCommand(statusService);
    }

    @Test
    void testBasicStatusCheck() {
        final String pipelineId = "pipeline-123";
        final PipelineStatus mockStatus = new PipelineStatus(
                pipelineId,
                PipelineState.RUNNING,
                75,
                "Build",
                Instant.now(),
                Instant.now()
        );
        when(statusService.getPipelineStatus(pipelineId)).thenReturn(mockStatus);

        // Use the setter method instead of accessing pipelineId directly
        statusCommand.setPipelineId(pipelineId);

        statusCommand.run();

        final String output = outputStream.toString();
        assertTrue(output.contains("Pipeline ID: " + pipelineId));
        assertTrue(output.contains("Status: RUNNING"));
        assertTrue(output.contains("Progress: 75%"));
    }
}
