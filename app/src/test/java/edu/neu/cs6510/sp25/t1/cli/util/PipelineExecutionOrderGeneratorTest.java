package edu.neu.cs6510.sp25.t1.cli.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PipelineExecutionOrderGeneratorTest {
  @TempDir
  Path tempDir; // Temporary directory for test files

  private PipelineExecutionOrderGenerator generator;
  private Path yamlFilePath;

  @BeforeEach
  void setUp() throws IOException {
    generator = Mockito.spy(new PipelineExecutionOrderGenerator());

    // Create .pipelines directory inside tempDir
    Path pipelinesDir = tempDir.resolve(".pipelines");
    Files.createDirectory(pipelinesDir);

    // Copy YAML file from test resources to tempDir/.pipelines/
    yamlFilePath = pipelinesDir.resolve("valid_pipeline.yml");
    copyTestResource("yaml/commands/execution_pipelines/valid_pipeline.yml", yamlFilePath);
  }

  @Test
  void testGenerateExecutionOrder_ValidYaml() throws IOException {
    // Mock the execution order processing to ensure it's called
    PipelineExecutionOrderGenerator mockGenerator = mock(PipelineExecutionOrderGenerator.class);
    when(mockGenerator.generateExecutionOrder(yamlFilePath.toString()))
        .thenCallRealMethod();

    Map<String, Map<String, Object>> executionOrder = mockGenerator.generateExecutionOrder(yamlFilePath.toString());

    assertNotNull(executionOrder);
    assertTrue(executionOrder.containsKey("build"));
    assertTrue(executionOrder.containsKey("test"));
    assertTrue(executionOrder.containsKey("deploy"));

    // Ensure job execution follows dependencies
    assertExecutionOrder(executionOrder, "jobA", "jobB");
    assertExecutionOrder(executionOrder, "jobB", "jobC");
    assertExecutionOrder(executionOrder, "jobD", "jobE");
    assertExecutionOrder(executionOrder, "jobC", "jobE");

    verify(mockGenerator, times(1)).generateExecutionOrder(anyString());
  }

  /**
   * Copies a test resource file from `src/test/resources` to the temp directory.
   *
   * @return
   */
  private Path copyTestResource(String resourcePath, Path destination) throws IOException {
    try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
      if (resourceStream == null) {
        throw new IOException("Test resource not found: " + resourcePath);
      }
      Files.copy(resourceStream, destination, StandardCopyOption.REPLACE_EXISTING);
    }
    return destination;
  }

  @Test
  void testGenerateExecutionOrder_InvalidYaml_NoStages() throws IOException {
    Path invalidYamlFile = copyTestResource("yaml/commands/execution_pipelines/missing_stages.yml", tempDir.resolve("missing_stages.yml"));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        generator.generateExecutionOrder(invalidYamlFile.toString())
    );

    assertEquals("Invalid YAML structure: 'stages' key is missing.", exception.getMessage());
  }

  @Test
  void testGenerateExecutionOrder_CircularDependency() throws IOException {
    Path circularYamlFile = copyTestResource("yaml/commands/execution_pipelines/circular_dependency.yml", tempDir.resolve("circular_dependency.yml"));

    Map<String, Map<String, Object>> executionOrder = generator.generateExecutionOrder(circularYamlFile.toString());

    boolean allStagesEmpty = executionOrder.values().stream().allMatch(Map::isEmpty);
    assertTrue(allStagesEmpty, "Execution order should be empty due to circular dependency.");
  }

  @Test
  void testGenerateExecutionOrder_JobDependsOnUnexecutedJob() throws IOException {
    Path dependencyYamlFile = copyTestResource("yaml/commands/execution_pipelines/missing_dependencies.yml", tempDir.resolve("missing_dependencies.yml"));

    Map<String, Map<String, Object>> executionOrder = generator.generateExecutionOrder(dependencyYamlFile.toString());

    boolean allStagesEmpty = executionOrder.values().stream().allMatch(Map::isEmpty);
    assertTrue(allStagesEmpty, "Execution order should be empty because dependencies are missing.");
  }

  @Test
  void testGenerateExecutionOrder_EmptyYaml() throws IOException {
    Path emptyYamlFile = copyTestResource("yaml/commands/execution_pipelines/empty_pipeline.yml", tempDir.resolve("empty_pipeline.yml"));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        generator.generateExecutionOrder(emptyYamlFile.toString())
    );

    assertEquals("Invalid YAML structure: 'stages' key is missing.", exception.getMessage());
  }


  /**
   * Utility method to check if job1 is executed before job2.
   */
  private void assertExecutionOrder(Map<String, Map<String, Object>> executionOrder, String job1, String job2) {
    boolean foundJob1 = false;
    boolean foundJob2 = false;

    for (Map.Entry<String, Map<String, Object>> stage : executionOrder.entrySet()) {
      if (stage.getValue().containsKey(job1)) {
        foundJob1 = true;
      }
      if (stage.getValue().containsKey(job2)) {
        foundJob2 = true;
        assertTrue(foundJob1, "Job " + job2 + " should not be executed before " + job1);
        return;
      }
    }
    fail("One or both jobs not found in execution order: " + job1 + ", " + job2);
  }
}
