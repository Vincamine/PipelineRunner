package edu.neu.cs6510.sp25.t1.validation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobValidatorTest {

  private JobValidator validator;
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalErr = System.err;

  @BeforeEach
  void setUp() {
    System.setErr(new PrintStream(errContent)); // Redirect error stream
  }

  @AfterEach
  void tearDown() {
    System.setErr(originalErr); // Restore original error stream
  }

  @Test
  void validateJobs_WithValidJobs() throws URISyntaxException, IOException {
    final Path yamlPath = getResourcePath("yaml/job/valid_jobs.yml");
    final YamlLoadResult result = YamlLoader.loadYamlWithLocations(yamlPath.toString());

    assertNotNull(result.getData(), "YAML data should not be null");
    assertNotNull(result.getLocations(), "YAML locations should not be null");

    @SuppressWarnings("unchecked")
    final List<String> stages = (List<String>) ((Map<String, Object>) result.getData().get("pipeline")).get("stages");
    validator = new JobValidator(stages);

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> jobs = (List<Map<String, Object>>) result.getData().get("job");

    assertTrue(validator.validateJobs(jobs, result.getLocations(), yamlPath.toString()));
    assertEquals("", errContent.toString().trim());
  }

  @Test
  void validateJobs_WithDuplicateNames() throws URISyntaxException, IOException {
    final Path yamlPath = getResourcePath("yaml/job/duplicate_job_names.yml");
    final YamlLoadResult result = YamlLoader.loadYamlWithLocations(yamlPath.toString());

    assertNotNull(result.getData(), "YAML data should not be null");

    @SuppressWarnings("unchecked")
    final List<String> stages = (List<String>) ((Map<String, Object>) result.getData().get("pipeline")).get("stages");
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

    assertNotNull(result.getData(), "YAML data should not be null");

    @SuppressWarnings("unchecked")
    final List<String> stages = (List<String>) ((Map<String, Object>) result.getData().get("pipeline")).get("stages");
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

    assertNotNull(result.getData(), "YAML data should not be null");

    @SuppressWarnings("unchecked")
    final List<String> stages = (List<String>) ((Map<String, Object>) result.getData().get("pipeline")).get("stages");
    validator = new JobValidator(stages);

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> jobs = (List<Map<String, Object>>) result.getData().get("job");

    assertFalse(validator.validateJobs(jobs, result.getLocations(), yamlPath.toString()));

    // Verify correct error message
    assertTrue(errContent.toString().contains("Missing Field Error"));
  }

  @Test
  void validateJobs_WithEmptyScript() throws URISyntaxException, IOException {
    final Path yamlPath = getResourcePath("yaml/job/job_with_empty_script.yml");
    final YamlLoadResult result = YamlLoader.loadYamlWithLocations(yamlPath.toString());

    assertNotNull(result.getData(), "YAML data should not be null");

    @SuppressWarnings("unchecked")
    final List<String> stages = (List<String>) ((Map<String, Object>) result.getData().get("pipeline")).get("stages");
    validator = new JobValidator(stages);

    @SuppressWarnings("unchecked")
    final List<Map<String, Object>> jobs = (List<Map<String, Object>>) result.getData().get("job");

    assertFalse(validator.validateJobs(jobs, result.getLocations(), yamlPath.toString()));
    // Check if the error message mentions the script being empty
    assertTrue(errContent.toString().contains("must have at least one script command"));
  }

  /**
   * Retrieves the path to a resource file inside the test classpath.
   *
   * @param resource The resource file name (relative to `src/test/resources`).
   * @return The absolute path to the requested resource.
   * @throws URISyntaxException If the resource URI is malformed.
   */
  private Path getResourcePath(String resource) throws URISyntaxException {
    final var url = ClassLoader.getSystemResource(resource);
    assertNotNull(url, "Test resource not found: " + resource);
    return Paths.get(url.toURI());
  }
}
