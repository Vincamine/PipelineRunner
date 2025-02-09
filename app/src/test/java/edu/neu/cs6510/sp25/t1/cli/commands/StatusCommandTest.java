package edu.neu.cs6510.sp25.t1.cli.commands;


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

    statusCommand.setPipelineId(pipelineId);
    statusCommand.run();

    final String output = outputStream.toString();
    System.out.println(output);
    assertTrue(output.contains("Pipeline ID: " + pipelineId));
    assertTrue(output.contains("Status: RUNNING"));
    assertTrue(output.contains("Progress: 75%"));
  }

  @Test
  void testVerboseOutput() {

    final String pipelineId = "pipeline-123";
    final PipelineStatus mockStatus = new PipelineStatus(
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

    final String output = outputStream.toString();
    assertTrue(output.contains("Current Stage: Build"));
    assertTrue(output.contains("Message: Building project"));
    assertTrue(output.contains("Start Time:"));
    assertTrue(output.contains("Last Updated:"));
  }

  @Test
  void testServiceError() {

    final String pipelineId = "pipeline-123";
    when(statusService.getPipelineStatus(pipelineId))
        .thenThrow(new RuntimeException("Service error"));

    statusCommand.setPipelineId(pipelineId);
    statusCommand.run();

    verify(statusService).getPipelineStatus(pipelineId);
    final String output = outputStream.toString();
    assertTrue(output.contains("Error checking pipeline status"));
  }
  @Test
  void testVerboseServiceError() {
    final String pipelineId = "pipeline-123";
    final RuntimeException testException = new RuntimeException("Service error");
    when(statusService.getPipelineStatus(pipelineId)).thenThrow(testException);

    statusCommand.setPipelineId(pipelineId);
    statusCommand.setVerbose(true);
    statusCommand.run();

    verify(statusService).getPipelineStatus(pipelineId);
    final String output = outputStream.toString();

    final String error = errorStream.toString();

    assertTrue(output.contains("Error checking pipeline status: Service error"));
    assertTrue(error.contains("java.lang.RuntimeException: Service error"));
    assertTrue(error.contains("at edu.neu.cs6510.sp25.t1.cli.commands.StatusCommandTest"));
  }

}
