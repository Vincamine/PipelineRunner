package edu.neu.cs6510.sp25.t1.cli.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;

import edu.neu.cs6510.sp25.t1.cli.CliApp;
import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ReportCommandTest {

  // Subclass CliApp to make pipeline field visible for testing
  static class TestCliApp extends CliApp {
    public void setPipeline(String value) {
      pipeline = value;
    }
  }

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @Mock
  private CliBackendClient backendClientMock;

  private TestCliApp parent;
  private ReportCommand reportCommand;

  private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

  // Helper method to normalize line endings for comparison
  private String normalizeLineEndings(String s) {
    return s.replace("\r\n", "\n");
  }

  @BeforeEach
  public void setUp() throws Exception {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));

    // Create test parent app
    parent = new TestCliApp();

    // Create report command
    reportCommand = new ReportCommand();

    // Set parent
    setPrivateField(reportCommand, "parent", parent);

    // Set backend client
    setPrivateField(reportCommand, "backendClient", backendClientMock);
  }

  @AfterEach
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  public void testCallWithoutPipeline() {
    // No need to set the pipeline (it's null by default)

    // Execute the command
    int result = reportCommand.call();

    // Verify
    assertEquals(1, result);
    assertEquals(normalizeLineEndings("[Error] Please specify a pipeline using --pipeline.\n"),
            normalizeLineEndings(errContent.toString()));
  }

  @Test
  public void testFetchPipelineHistory() throws Exception {
    // Set pipeline name
    parent.setPipeline("test-pipeline");

    // Mock backend response
    doReturn("Pipeline history mock response").when(backendClientMock)
            .fetchPipelineReport(anyString(), anyInt(), any(), any());

    // Execute
    int result = reportCommand.call();

    // Verify
    assertEquals(0, result);
    assertEquals(normalizeLineEndings("Fetching past runs for pipeline: test-pipeline\nPipeline history mock response\n"),
            normalizeLineEndings(outContent.toString()));
  }

  @Test
  public void testFetchPipelineRunSummary() throws Exception {
    // Set pipeline name
    parent.setPipeline("test-pipeline");

    // Set runNumber
    setPrivateField(reportCommand, "runNumber", 123);

    // Mock backend response
    doReturn("Pipeline run summary mock response").when(backendClientMock)
            .fetchPipelineReport(anyString(), anyInt(), any(), any());

    // Execute
    int result = reportCommand.call();

    // Verify
    assertEquals(0, result);
    assertEquals(normalizeLineEndings("Fetching run summary for pipeline: test-pipeline, Run: 123\nPipeline run summary mock response\n"),
            normalizeLineEndings(outContent.toString()));
  }

  @Test
  public void testFetchStageSummary() throws Exception {
    // Set pipeline name
    parent.setPipeline("test-pipeline");

    // Set parameters
    setPrivateField(reportCommand, "runNumber", 123);
    setPrivateField(reportCommand, "stageName", "build");

    // Mock backend response
    doReturn("Stage summary mock response").when(backendClientMock)
            .fetchPipelineReport(anyString(), anyInt(), any(), any());

    // Execute
    int result = reportCommand.call();

    // Verify
    assertEquals(0, result);
    assertEquals(normalizeLineEndings("Fetching stage summary for pipeline: test-pipeline, Run: 123, Stage: build\nStage summary mock response\n"),
            normalizeLineEndings(outContent.toString()));
  }

  @Test
  public void testFetchJobSummary() throws Exception {
    // Set pipeline name
    parent.setPipeline("test-pipeline");

    // Set parameters
    setPrivateField(reportCommand, "runNumber", 123);
    setPrivateField(reportCommand, "stageName", "build");
    setPrivateField(reportCommand, "jobName", "compile");

    // Mock backend response
    doReturn("Job summary mock response").when(backendClientMock)
            .fetchPipelineReport(anyString(), anyInt(), any(), any());

    // Execute
    int result = reportCommand.call();

    // Verify
    assertEquals(0, result);
    assertEquals(normalizeLineEndings("Fetching job summary for pipeline: test-pipeline, Run: 123, Stage: build, Job: compile\nJob summary mock response\n"),
            normalizeLineEndings(outContent.toString()));
  }

  @Test
  public void testHandleIOException() throws Exception {
    // Set pipeline name
    parent.setPipeline("test-pipeline");

    // Mock backend to throw exception
    doThrow(new IOException("Network error")).when(backendClientMock)
            .fetchPipelineReport(anyString(), anyInt(), any(), any());

    // Execute
    int result = reportCommand.call();

    // Verify
    assertEquals(1, result);
    assertEquals(normalizeLineEndings("[Error] API request failed: Network error\n"),
            normalizeLineEndings(errContent.toString()));
  }
}