package edu.neu.cs6510.sp25.t1.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JobValidatorTest {

  private JobValidator validator;
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

  @BeforeEach
  void setUp() {
    System.setErr(new PrintStream(errContent));
  }

  @Test
  void validateJobs_WithValidJobs() throws URISyntaxException, IOException {
    final Path yamlPath = getResourcePath("yaml/job/valid_jobs.yml");
    final YamlLoadResult result = YamlLoader.loadYamlWithLocations(yamlPath.toString());

    @SuppressWarnings("unchecked")
    final List<String> stages = (List<String>) ((Map<String, Object>) result.getData()
        .get("pipeline")).get("stages");
    validator = new JobValidator(stages);

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> jobs = (List<Map<String, Object>>) result.getData().get("job");

    assertTrue(validator.validateJobs(jobs, result.getLocations(), yamlPath.toString()));
    assertEquals("", errContent.toString());
  }

  @Test
  void validateJobs_WithDuplicateNames() throws URISyntaxException, IOException {
    final Path yamlPath = getResourcePath("yaml/job/duplicate_job_names.yml");
    final YamlLoadResult result = YamlLoader.loadYamlWithLocations(yamlPath.toString());

    @SuppressWarnings("unchecked")
    final List<String> stages = (List<String>) ((Map<String, Object>) result.getData()
        .get("pipeline")).get("stages");
    validator = new JobValidator(stages);

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> jobs = (List<Map<String, Object>>) result.getData().get("job");

    assertFalse(validator.validateJobs(jobs, result.getLocations(), yamlPath.toString()));
    assertTrue(errContent.toString().contains("Duplicate job name"));
  }

  @Test
  void validateJobs_WithInvalidStage() throws URISyntaxException, IOException {
    final Path yamlPath = getResourcePath("yaml/job/job_with_invalid_stage.yml");
    final YamlLoadResult result = YamlLoader.loadYamlWithLocations(yamlPath.toString());

    @SuppressWarnings("unchecked")
    final List<String> stages = (List<String>) ((Map<String, Object>) result.getData()
        .get("pipeline")).get("stages");
    validator = new JobValidator(stages);

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> jobs = (List<Map<String, Object>>) result.getData().get("job");

    assertFalse(validator.validateJobs(jobs, result.getLocations(), yamlPath.toString()));
    assertTrue(errContent.toString().contains("non-existent stage"));
  }

  @Test
  void validateJobs_WithMissingRequiredFields() throws URISyntaxException, IOException {
    final Path yamlPath = getResourcePath("yaml/job/job_missing_required_fields.yml");
    final YamlLoadResult result = YamlLoader.loadYamlWithLocations(yamlPath.toString());

    @SuppressWarnings("unchecked")
    final List<String> stages = (List<String>) ((Map<String, Object>) result.getData()
        .get("pipeline")).get("stages");
    validator = new JobValidator(stages);

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> jobs = (List<Map<String, Object>>) result.getData().get("job");

    assertFalse(validator.validateJobs(jobs, result.getLocations(), yamlPath.toString()));
    System.out.println(errContent.toString());
    assertTrue(errContent.toString().contains("missing field error"));
  }

  private Path getResourcePath(String resource) throws URISyntaxException {
    return Paths.get(ClassLoader.getSystemResource(resource).toURI());
  }
}