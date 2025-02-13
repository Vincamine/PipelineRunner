package edu.neu.cs6510.sp25.t1.cli.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.yaml.snakeyaml.error.Mark;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JobValidatorTest {
  private JobValidator validator;
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalErr = System.err;
  private List<String> validStages;
  private Map<String, Mark> locations;
  private final String TEST_FILENAME = "pipeline.yaml";

  @BeforeEach
  void setUp() {
    validStages = Arrays.asList("build", "test", "deploy");
    validator = new JobValidator(validStages);
    System.setErr(new PrintStream(errContent));
    locations = new HashMap<>();
  }

  @Test
  void validateJobs_WithValidJob() {
    final Map<String, Object> job = createValidJob("build-job", "build");
    assertTrue(validator.validateJobs(Collections.singletonList(job), locations, TEST_FILENAME));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidJobFields")
  void validateJobs_WithInvalidFields(String fieldName, Object invalidValue) {
    final Map<String, Object> job = createValidJob("test-job", "build");
    job.put(fieldName, invalidValue);
    assertFalse(validator.validateJobs(Collections.singletonList(job), locations, TEST_FILENAME));
    assertTrue(errContent.toString().contains("Wrong type"));
  }

  private static Stream<Arguments> provideInvalidJobFields() {
    return Stream.of(
        Arguments.of("name", 123),
        Arguments.of("stage", 456),
        Arguments.of("image", true),
        Arguments.of("script", "not-a-list")
    );
  }

  @Test
  void validateJobs_WithMissingFields() {
    final Map<String, Object> job = createValidJob("test-job", "build");
    job.remove("image");
    assertFalse(validator.validateJobs(Collections.singletonList(job), locations, TEST_FILENAME));
    assertTrue(errContent.toString().contains("Missing required field"));
  }

  @Test
  void validateJobs_WithNonExistentStage() {
    final Map<String, Object> job = createValidJob("test-job", "non-existent-stage");
    assertFalse(validator.validateJobs(Collections.singletonList(job), locations, TEST_FILENAME));
    assertTrue(errContent.toString().contains("non-existent stage"));
  }

  @Test
  void validateJobs_WithDuplicateNames() {
    final List<Map<String, Object>> jobs = Arrays.asList(
        createValidJob("same-name", "build"),
        createValidJob("same-name", "build")
    );
    assertFalse(validator.validateJobs(jobs, locations, TEST_FILENAME));
    assertTrue(errContent.toString().contains("Duplicate job name"));
  }

  private Map<String, Object> createValidJob(String name, String stage) {
    final Map<String, Object> job = new HashMap<>();
    job.put("name", name);
    job.put("stage", stage);
    job.put("image", "ubuntu:latest");
    job.put("script", Arrays.asList("echo 'test'"));
    return job;
  }
}