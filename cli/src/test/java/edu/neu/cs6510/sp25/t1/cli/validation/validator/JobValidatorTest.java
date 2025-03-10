package edu.neu.cs6510.sp25.t1.cli.validation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.neu.cs6510.sp25.t1.cli.validation.error.ValidationException;
import edu.neu.cs6510.sp25.t1.cli.validation.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Stage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JobValidatorTest {

  private static final String TEST_FILENAME = "pipeline.yml";

  @BeforeEach
  public void setUp() {
    // Mock YamlParser.getFieldLineNumber to return a consistent value for testing
    try (MockedStatic<YamlParser> yamlParserMockedStatic = Mockito.mockStatic(YamlParser.class)) {
      yamlParserMockedStatic.when(() -> YamlParser.getFieldLineNumber(anyString(), anyString()))
              .thenReturn(10);
    }
  }

  @Test
  public void testValidateJobs_ValidJobs_NoExceptionThrown() {
    // Arrange
    Job validJob = Mockito.mock(Job.class);
    when(validJob.getName()).thenReturn("test-job");
    when(validJob.getDockerImage()).thenReturn("alpine:latest");
    when(validJob.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Stage validStage = Mockito.mock(Stage.class);
    when(validStage.getName()).thenReturn("test-stage");
    when(validStage.getJobs()).thenReturn(Collections.singletonList(validJob));

    List<Stage> stages = new ArrayList<>(Collections.singletonList(validStage));

    // Act & Assert
    assertDoesNotThrow(() -> JobValidator.validateJobs(stages, TEST_FILENAME));
  }

  @Test
  public void testValidateJobs_MissingJobName_ThrowsValidationException() {
    // Arrange
    Job validJob = Mockito.mock(Job.class);
    when(validJob.getName()).thenReturn("test-job");
    when(validJob.getDockerImage()).thenReturn("alpine:latest");
    when(validJob.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Job jobWithoutName = Mockito.mock(Job.class);
    when(jobWithoutName.getName()).thenReturn(null);
    when(jobWithoutName.getDockerImage()).thenReturn("alpine:latest");
    when(jobWithoutName.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Stage stage = Mockito.mock(Stage.class);
    when(stage.getName()).thenReturn("test-stage");
    when(stage.getJobs()).thenReturn(Arrays.asList(validJob, jobWithoutName));

    List<Stage> stages = Collections.singletonList(stage);

    // Act & Assert
    ValidationException exception = assertThrows(ValidationException.class,
            () -> JobValidator.validateJobs(stages, TEST_FILENAME));

    assertTrue(exception.getMessage().contains("Job must have a name"));
  }

  @Test
  public void testValidateJobs_EmptyJobName_ThrowsValidationException() {
    // Arrange
    Job validJob = Mockito.mock(Job.class);
    when(validJob.getName()).thenReturn("test-job");
    when(validJob.getDockerImage()).thenReturn("alpine:latest");
    when(validJob.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Job jobWithEmptyName = Mockito.mock(Job.class);
    when(jobWithEmptyName.getName()).thenReturn("");
    when(jobWithEmptyName.getDockerImage()).thenReturn("alpine:latest");
    when(jobWithEmptyName.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Stage stage = Mockito.mock(Stage.class);
    when(stage.getName()).thenReturn("test-stage");
    when(stage.getJobs()).thenReturn(Arrays.asList(validJob, jobWithEmptyName));

    List<Stage> stages = Collections.singletonList(stage);

    // Act & Assert
    ValidationException exception = assertThrows(ValidationException.class,
            () -> JobValidator.validateJobs(stages, TEST_FILENAME));

    assertTrue(exception.getMessage().contains("Job must have a name"));
  }

  @Test
  public void testValidateJobs_MissingDockerImage_ThrowsValidationException() {
    // Arrange
    Job validJob = Mockito.mock(Job.class);
    when(validJob.getName()).thenReturn("test-job");
    when(validJob.getDockerImage()).thenReturn("alpine:latest");
    when(validJob.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Job jobWithoutImage = Mockito.mock(Job.class);
    when(jobWithoutImage.getName()).thenReturn("no-image-job");
    when(jobWithoutImage.getDockerImage()).thenReturn(null);
    when(jobWithoutImage.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Stage stage = Mockito.mock(Stage.class);
    when(stage.getName()).thenReturn("test-stage");
    when(stage.getJobs()).thenReturn(Arrays.asList(validJob, jobWithoutImage));

    List<Stage> stages = Collections.singletonList(stage);

    // Act & Assert
    ValidationException exception = assertThrows(ValidationException.class,
            () -> JobValidator.validateJobs(stages, TEST_FILENAME));

    assertTrue(exception.getMessage().contains("must specify an image"));
  }

  @Test
  public void testValidateJobs_EmptyDockerImage_ThrowsValidationException() {
    // Arrange
    Job validJob = Mockito.mock(Job.class);
    when(validJob.getName()).thenReturn("test-job");
    when(validJob.getDockerImage()).thenReturn("alpine:latest");
    when(validJob.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Job jobWithEmptyImage = Mockito.mock(Job.class);
    when(jobWithEmptyImage.getName()).thenReturn("empty-image-job");
    when(jobWithEmptyImage.getDockerImage()).thenReturn("");
    when(jobWithEmptyImage.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Stage stage = Mockito.mock(Stage.class);
    when(stage.getName()).thenReturn("test-stage");
    when(stage.getJobs()).thenReturn(Arrays.asList(validJob, jobWithEmptyImage));

    List<Stage> stages = Collections.singletonList(stage);

    // Act & Assert
    ValidationException exception = assertThrows(ValidationException.class,
            () -> JobValidator.validateJobs(stages, TEST_FILENAME));

    assertTrue(exception.getMessage().contains("must specify an image"));
  }

  @Test
  public void testValidateJobs_MissingScript_ThrowsValidationException() {
    // Arrange
    Job validJob = Mockito.mock(Job.class);
    when(validJob.getName()).thenReturn("test-job");
    when(validJob.getDockerImage()).thenReturn("alpine:latest");
    when(validJob.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Job jobWithoutScript = Mockito.mock(Job.class);
    when(jobWithoutScript.getName()).thenReturn("no-script-job");
    when(jobWithoutScript.getDockerImage()).thenReturn("alpine:latest");
    when(jobWithoutScript.getScript()).thenReturn(null);

    Stage stage = Mockito.mock(Stage.class);
    when(stage.getName()).thenReturn("test-stage");
    when(stage.getJobs()).thenReturn(Arrays.asList(validJob, jobWithoutScript));

    List<Stage> stages = Collections.singletonList(stage);

    // Act & Assert
    ValidationException exception = assertThrows(ValidationException.class,
            () -> JobValidator.validateJobs(stages, TEST_FILENAME));

    assertTrue(exception.getMessage().contains("must have at least one script command"));
  }

  @Test
  public void testValidateJobs_EmptyScript_ThrowsValidationException() {
    // Arrange
    Job validJob = Mockito.mock(Job.class);
    when(validJob.getName()).thenReturn("test-job");
    when(validJob.getDockerImage()).thenReturn("alpine:latest");
    when(validJob.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Job jobWithEmptyScript = Mockito.mock(Job.class);
    when(jobWithEmptyScript.getName()).thenReturn("empty-script-job");
    when(jobWithEmptyScript.getDockerImage()).thenReturn("alpine:latest");
    when(jobWithEmptyScript.getScript()).thenReturn(Collections.emptyList());

    Stage stage = Mockito.mock(Stage.class);
    when(stage.getName()).thenReturn("test-stage");
    when(stage.getJobs()).thenReturn(Arrays.asList(validJob, jobWithEmptyScript));

    List<Stage> stages = Collections.singletonList(stage);

    // Act & Assert
    ValidationException exception = assertThrows(ValidationException.class,
            () -> JobValidator.validateJobs(stages, TEST_FILENAME));

    assertTrue(exception.getMessage().contains("must have at least one script command"));
  }

  @Test
  public void testValidateJobs_DuplicateJobNames_ThrowsValidationException() {
    // Arrange
    Job firstJob = Mockito.mock(Job.class);
    when(firstJob.getName()).thenReturn("test-job");
    when(firstJob.getDockerImage()).thenReturn("alpine:latest");
    when(firstJob.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Job duplicateNameJob = Mockito.mock(Job.class);
    when(duplicateNameJob.getName()).thenReturn("test-job"); // Same name as firstJob
    when(duplicateNameJob.getDockerImage()).thenReturn("node:14");
    when(duplicateNameJob.getScript()).thenReturn(Arrays.asList("npm test"));

    Stage stage = Mockito.mock(Stage.class);
    when(stage.getName()).thenReturn("test-stage");
    when(stage.getJobs()).thenReturn(Arrays.asList(firstJob, duplicateNameJob));

    List<Stage> stages = Collections.singletonList(stage);

    // Act & Assert
    ValidationException exception = assertThrows(ValidationException.class,
            () -> JobValidator.validateJobs(stages, TEST_FILENAME));

    assertTrue(exception.getMessage().contains("Duplicate job name found"));
  }

  @Test
  public void testValidateJobs_MultipleErrors_ThrowsValidationExceptionWithAllErrors() {
    // Arrange
    Job validJob = Mockito.mock(Job.class);
    when(validJob.getName()).thenReturn("test-job");
    when(validJob.getDockerImage()).thenReturn("alpine:latest");
    when(validJob.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Job jobWithMultipleErrors = Mockito.mock(Job.class);
    when(jobWithMultipleErrors.getName()).thenReturn(null);
    when(jobWithMultipleErrors.getDockerImage()).thenReturn(null);
    when(jobWithMultipleErrors.getScript()).thenReturn(Arrays.asList("echo 'Hello World'"));

    Stage stage = Mockito.mock(Stage.class);
    when(stage.getName()).thenReturn("test-stage");
    when(stage.getJobs()).thenReturn(Arrays.asList(validJob, jobWithMultipleErrors));

    List<Stage> stages = Collections.singletonList(stage);

    // Act
    ValidationException exception = assertThrows(ValidationException.class,
            () -> JobValidator.validateJobs(stages, TEST_FILENAME));

    // Assert
    String errorMessage = exception.getMessage();
    assertTrue(errorMessage.contains("Job must have a name"));
    assertTrue(errorMessage.contains("must specify an image"));
  }

  @Test
  public void testValidateJobs_NoStages_NoExceptionThrown() {
    // Arrange
    List<Stage> emptyStages = new ArrayList<>();

    // Act & Assert
    assertDoesNotThrow(() -> JobValidator.validateJobs(emptyStages, TEST_FILENAME));
  }

  @Test
  public void testValidateJobs_StageWithNoJobs_NoExceptionThrown() {
    // Arrange
    Stage emptyStage = Mockito.mock(Stage.class);
    when(emptyStage.getName()).thenReturn("empty-stage");
    when(emptyStage.getJobs()).thenReturn(new ArrayList<>());

    List<Stage> stages = new ArrayList<>(Collections.singletonList(emptyStage));

    // Act & Assert
    assertDoesNotThrow(() -> JobValidator.validateJobs(stages, TEST_FILENAME));
  }
}