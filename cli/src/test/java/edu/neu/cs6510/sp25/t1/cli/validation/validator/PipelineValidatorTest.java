package edu.neu.cs6510.sp25.t1.cli.validation.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.cli.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.cli.validation.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.model.Stage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PipelineValidatorTest {

  private static final String TEST_FILENAME = "pipeline.yml";


  @Test
  public void testValidate_MissingPipelineName_ThrowsValidationException() {
    try (MockedStatic<YamlParser> yamlParserMock = Mockito.mockStatic(YamlParser.class)) {
      yamlParserMock.when(() -> YamlParser.getFieldLineNumber(eq(TEST_FILENAME), eq("name"))).thenReturn(5);

      Pipeline pipeline = Mockito.mock(Pipeline.class);
      when(pipeline.getName()).thenReturn(null);
      // Return empty list for getStages to avoid NullPointerException
      when(pipeline.getStages()).thenReturn(Collections.emptyList());

      // Act & Assert
      ValidationException exception = assertThrows(ValidationException.class,
              () -> PipelineValidator.validate(pipeline, TEST_FILENAME));

      assertTrue(exception.getMessage().contains("Pipeline name is required"));
    }
  }

  @Test
  public void testValidate_EmptyPipelineName_ThrowsValidationException() {
    try (MockedStatic<YamlParser> yamlParserMock = Mockito.mockStatic(YamlParser.class)) {
      yamlParserMock.when(() -> YamlParser.getFieldLineNumber(eq(TEST_FILENAME), eq("name"))).thenReturn(5);

      Pipeline pipeline = Mockito.mock(Pipeline.class);
      when(pipeline.getName()).thenReturn("");
      // Return empty list for getStages to avoid NullPointerException
      when(pipeline.getStages()).thenReturn(Collections.emptyList());

      // Act & Assert
      ValidationException exception = assertThrows(ValidationException.class,
              () -> PipelineValidator.validate(pipeline, TEST_FILENAME));

      assertTrue(exception.getMessage().contains("Pipeline name is required"));
    }
  }

  @Test
  public void testDetectCycles_NoCycles_ReturnsEmptyList() {
    // Arrange - Create a linear dependency chain: A -> B -> C
    UUID jobAId = UUID.randomUUID();
    UUID jobBId = UUID.randomUUID();
    UUID jobCId = UUID.randomUUID();

    // Create job mocks with appropriate names and dependencies
    Job jobA = Mockito.mock(Job.class);
    when(jobA.getName()).thenReturn("job-a");
    when(jobA.getDependencies()).thenReturn(Collections.singletonList(jobBId));

    Job jobB = Mockito.mock(Job.class);
    when(jobB.getName()).thenReturn("job-b");
    when(jobB.getDependencies()).thenReturn(Collections.singletonList(jobCId));

    Job jobC = Mockito.mock(Job.class);
    when(jobC.getName()).thenReturn("job-c");
    when(jobC.getDependencies()).thenReturn(Collections.emptyList());

    // Add jobs to a list for the stage
    List<Job> jobs = new ArrayList<>();
    jobs.add(jobA);
    jobs.add(jobB);
    jobs.add(jobC);

    // Create stage with jobs
    Stage stage = Mockito.mock(Stage.class);
    when(stage.getJobs()).thenReturn(jobs);

    // Add stage to a list for the pipeline
    List<Stage> stages = new ArrayList<>();
    stages.add(stage);

    // Create pipeline
    Pipeline pipeline = Mockito.mock(Pipeline.class);
    when(pipeline.getStages()).thenReturn(stages);

    // Act - Use the real detectCycles method
    List<List<String>> cycles = PipelineValidator.detectCycles(pipeline);

    // Assert
    assertTrue(cycles.isEmpty(), "No cycles should be detected in a linear dependency chain");
  }

  @Test
  public void testValidate_NullPipeline_ThrowsNullPointerException() {
    // We expect a NullPointerException, not a ValidationException for null pipeline
    assertThrows(NullPointerException.class,
            () -> PipelineValidator.validate(null, TEST_FILENAME));
  }
}