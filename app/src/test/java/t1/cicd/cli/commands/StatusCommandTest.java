package t1.cicd.cli.commands;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import t1.cicd.cli.model.PipelineState;
import t1.cicd.cli.model.PipelineStatus;
import t1.cicd.cli.service.StatusService;

@ExtendWith(MockitoExtension.class)
public class StatusCommandTest {
  @Mock
  private StatusService statusService;
  private StatusCommand statusCommand;
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @BeforeEach
  void setUp(){
    System.setOut(new PrintStream(outputStream));
    statusCommand = new StatusCommand(statusService);
  }


  @Test
  void testBasicStatusCheck() {
    String pipelineId = "pipeline-123";
    PipelineStatus mockStatus = new PipelineStatus(
        pipelineId,
        PipelineState.RUNNING,
        75,
        "Build",
        Instant.now(),
        Instant.now()
    );
    when(statusService.getPipelineStatus(pipelineId)).thenReturn(mockStatus);

    statusCommand.setPipelineId(pipelineId);
    statusCommand.run();

    String output = outputStream.toString();
    System.out.println(output);
    assertTrue(output.contains("Pipeline ID: " + pipelineId));
    assertTrue(output.contains("Status: RUNNING"));
    assertTrue(output.contains("Progress: 75%"));
  }

  @Test
  void testVerboseOutput() {

    String pipelineId = "pipeline-123";
    PipelineStatus mockStatus = new PipelineStatus(
        pipelineId,
        PipelineState.RUNNING,
        75,
        "Build",
        Instant.now(),
        Instant.now()
    );
    mockStatus.setMessage("Building project");
    when(statusService.getPipelineStatus(pipelineId)).thenReturn(mockStatus);

    statusCommand.setPipelineId(pipelineId);
    statusCommand.setVerbose(true);
    statusCommand.run();

    String output = outputStream.toString();
    assertTrue(output.contains("Current Stage: Build"));
    assertTrue(output.contains("Message: Building project"));
    assertTrue(output.contains("Start Time:"));
    assertTrue(output.contains("Last Updated:"));
  }

  @Test
  void testServiceError() {

    String pipelineId = "pipeline-123";
    when(statusService.getPipelineStatus(pipelineId))
        .thenThrow(new RuntimeException("Service error"));

    statusCommand.setPipelineId(pipelineId);
    statusCommand.run();

    verify(statusService).getPipelineStatus(pipelineId);
    String output = outputStream.toString();
    assertTrue(output.contains("Error checking pipeline status"));
  }
}
