package edu.neu.cs6510.sp25.t1.cli.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import edu.neu.cs6510.sp25.t1.cli.CliApp;
import edu.neu.cs6510.sp25.t1.cli.validation.utils.GitUtils;
import edu.neu.cs6510.sp25.t1.cli.validation.validator.YamlPipelineValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

/**
 * Test class for RunCommand.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RunCommandTest {

  // Subclass of CliApp for testing
  static class TestCliApp extends CliApp {
    public void setRepo(String value) {
      repo = value;
    }

    public void setBranch(String value) {
      branch = value;
    }

    public void setCommit(String value) {
      commit = value;
    }

    public void setFilePath(String value) {
      filePath = value;
    }

    public void setPipeline(String value) {
      pipeline = value;
    }

    public void setLocalRun(boolean value) {
      localRun = value;
    }
  }

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  private TestCliApp parent;
  private RunCommand runCommand;

  // Test variable flags to control behavior
  private boolean shouldRemoteExecutionFail = false;
  private boolean shouldThrowIOException = false;


  private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }


  private Object invokePrivateMethod(Object target, String methodName, Class<?>[] paramTypes, Object[] params) throws Exception {
    Method method = target.getClass().getDeclaredMethod(methodName, paramTypes);
    method.setAccessible(true);
    return method.invoke(target, params);
  }

  // Helper method to normalize line endings for comparison
  private String normalizeLineEndings(String s) {
    return s != null ? s.replace("\r\n", "\n") : "";
  }

  @BeforeEach
  public void setUp() throws Exception {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));

    // Create test parent app
    parent = new TestCliApp();
    parent.setRepo("https://github.com/test/repo");
    parent.setBranch("main");
    parent.setCommit("");
    parent.setFilePath("pipeline.yml");
    parent.setPipeline("test-pipeline");
    parent.setLocalRun(false);

    // Reset test flags
    shouldRemoteExecutionFail = false;
    shouldThrowIOException = false;

    // Create test command using standard class
    runCommand = new RunCommand();

    // Set parent
    setPrivateField(runCommand, "parent", parent);
  }

  @AfterEach
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  public void testCallValidationError() throws Exception {
    // Mock the validator to throw an exception
    try (MockedStatic<YamlPipelineValidator> mockedValidator = mockStatic(YamlPipelineValidator.class)) {
      mockedValidator.when(() -> YamlPipelineValidator.validatePipeline(anyString()))
              .thenThrow(new RuntimeException("Invalid pipeline configuration"));

      // Execute
      int result = runCommand.call();

      // Verify
      assertEquals(1, result);
      assertTrue(normalizeLineEndings(errContent.toString())
              .contains("Invalid pipeline configuration"));
    }
  }

  @Test
  public void testCallLocalExecution() throws Exception {
    // Set to local run
    parent.setLocalRun(true);

    try (MockedStatic<YamlPipelineValidator> mockedValidator = mockStatic(YamlPipelineValidator.class);
         MockedStatic<GitUtils> mockedGitUtils = mockStatic(GitUtils.class)) {
      // Use doNothing for void methods instead of thenReturn(null)
      mockedValidator.when(() -> YamlPipelineValidator.validatePipeline(anyString()))
              .then(invocation -> null); // This is equivalent to doNothing() for static mocks

      // Mock GitUtils for latest commit
      mockedGitUtils.when(GitUtils::getLatestCommitHash).thenReturn("abc123");

      // Execute
      int result = runCommand.call();

      // Verify
      assertEquals(0, result);
      String output = normalizeLineEndings(outContent.toString());
      assertTrue(output.contains("[Local Execution] Running the pipeline locally"));
      assertTrue(output.contains("Pipeline execution completed successfully"));
    }
  }

  // Tests for runPipelineRemotely using separate test methods

  // We can't directly test the remote execution methods without modifying the static HTTP client,
  // so we'll test the overall call() method's response to exceptions instead.
  // This tests the error handling in call() when YamlPipelineValidator throws an exception

  @Test
  public void testExceptionHandling() throws Exception {
    try (MockedStatic<YamlPipelineValidator> mockedValidator = mockStatic(YamlPipelineValidator.class)) {
      // Mock the validator to throw IOException
      mockedValidator.when(() -> YamlPipelineValidator.validatePipeline(anyString()))
              .thenThrow(new RuntimeException("Test exception"));

      // Execute
      int result = runCommand.call();

      // Verify
      assertEquals(1, result);
      assertTrue(normalizeLineEndings(errContent.toString()).contains("Test exception"));
    }
  }

  @Test
  public void testCreatePostRequest() throws Exception {
    // Test the private createPostRequest method via reflection
    Object request = invokePrivateMethod(
            runCommand,
            "createPostRequest",
            new Class<?>[]{String.class, String.class},
            new Object[]{"http://test.com", "{\"test\":\"value\"}"});

    // Verify we got a non-null object back
    assertTrue(request != null);

    // Verify that it's an instance of okhttp3.Request
    assertTrue(request.getClass().getName().contains("Request"));
  }

  @Test
  public void testRunPipelineLocally() throws Exception {
    // Test the runPipelineLocally method via reflection
    Object result = invokePrivateMethod(
            runCommand,
            "runPipelineLocally",
            new Class<?>[]{String.class, String.class, String.class, String.class, String.class},
            new Object[]{"test-repo", "test-branch", "test-commit", "test-pipeline", "test-filepath"});

    // Verify it returns 0 (success)
    assertEquals(0, result);

    // Verify the output contains expected messages
    String output = normalizeLineEndings(outContent.toString());
    assertTrue(output.contains("[Local Execution] Running the pipeline locally"));
    assertTrue(output.contains("[Local Execution] Repository: test-repo"));
    assertTrue(output.contains("[Local Execution] Branch: test-branch"));
    assertTrue(output.contains("[Local Execution] Commit: test-commit"));
    assertTrue(output.contains("[Local Execution] Pipeline: test-pipeline"));
  }

  // This test will check if the pipeline name falls back to filepath when null
  @Test
  public void testRunPipelineLocallyWithNullPipeline() throws Exception {
    // Test the runPipelineLocally method via reflection with null pipeline
    Object result = invokePrivateMethod(
            runCommand,
            "runPipelineLocally",
            new Class<?>[]{String.class, String.class, String.class, String.class, String.class},
            new Object[]{"test-repo", "test-branch", "test-commit", null, "test-filepath"});

    // Verify it returns 0 (success)
    assertEquals(0, result);

    // Verify the output shows filepath as the pipeline name
    String output = normalizeLineEndings(outContent.toString());
    assertTrue(output.contains("[Local Execution] Pipeline: test-filepath"));
  }
}