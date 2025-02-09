package edu.neu.cs6510.sp25.t1.cli.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JobValidator.
 * Ensures job validation rules are properly enforced.
 */
class JobValidatorTest {
  private JobValidator jobValidator;

  /**
   * Initializes the JobValidator with valid stages before each test.
   */
  @BeforeEach
  void setUp() {
    jobValidator = null;  // Will be initialized per test with correct stages
  }

  /**
   * Tests a valid job configuration.
   */
  @Test
  void testValidJobConfiguration() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/job/valid_jobs.yml");
    final Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    final List<String> stages = getStages(yamlData);
    final List<Map<String, Object>> jobs = getJobs(yamlData);

    jobValidator = new JobValidator(stages);
    assertTrue(jobValidator.validateJobs(jobs), "Valid jobs should pass validation.");

    // Validate getJobStages()
    final Map<String, String> jobStages = jobValidator.getJobStages();
    assertEquals("build", jobStages.get("BuildJob"));
    assertEquals("test", jobStages.get("TestJob"));
  }

  /**
   * Tests a job configuration where a job is missing a 'name' field.
   */
  @Test
  void testMissingJobName() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/job/missing_job_name.yml");
    final Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    final List<String> stages = getStages(yamlData);
    final List<Map<String, Object>> jobs = getJobs(yamlData);

    jobValidator = new JobValidator(stages);
    assertFalse(jobValidator.validateJobs(jobs), "Missing job name should fail validation.");
  }

  /**
   * Tests a job configuration where a job is missing the 'stage' field.
   */
  @Test
  void testMissingJobStage() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/job/missing_job_stage.yml");
    final Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    final List<String> stages = getStages(yamlData);
    final List<Map<String, Object>> jobs = getJobs(yamlData);

    jobValidator = new JobValidator(stages);
    assertFalse(jobValidator.validateJobs(jobs), "Job missing 'stage' should fail validation.");
  }

  /**
   * Tests a job configuration where a job is missing the 'script' field.
   */
  @Test
  void testMissingJobScript() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/job/missing_job_script.yml");
    final Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    final List<String> stages = getStages(yamlData);
    final List<Map<String, Object>> jobs = getJobs(yamlData);

    jobValidator = new JobValidator(stages);
    assertFalse(jobValidator.validateJobs(jobs), "Job missing 'script' should fail validation.");
  }

  /**
   * Tests a job configuration with duplicate job names within a stage.
   */
  @Test
  void testDuplicateJobNames() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/job/duplicate_job_names.yml");
    final Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    final List<String> stages = getStages(yamlData);
    final List<Map<String, Object>> jobs = getJobs(yamlData);

    jobValidator = new JobValidator(stages);
    assertFalse(jobValidator.validateJobs(jobs), "Duplicate job names in the same stage should fail validation.");
  }

  /**
   * Tests a job that references a stage that does not exist.
   */
  @Test
  void testJobWithInvalidStage() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/job/job_with_invalid_stage.yml");
    final Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    final List<String> stages = getStages(yamlData);
    final List<Map<String, Object>> jobs = getJobs(yamlData);

    jobValidator = new JobValidator(stages);
    assertFalse(jobValidator.validateJobs(jobs), "Job with an invalid stage should fail validation.");
  }

  /**
   * Tests a job configuration missing required fields like 'image' and 'script'.
   */
  @Test
  void testJobMissingRequiredFields() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/job/job_missing_required_fields.yml");
    final Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    final List<String> stages = getStages(yamlData);
    final List<Map<String, Object>> jobs = getJobs(yamlData);

    jobValidator = new JobValidator(stages);
    assertFalse(jobValidator.validateJobs(jobs), "Job missing required fields should fail validation.");
  }

  /**
   * Helper method to retrieve a test resource file path.
   *
   * @param resource The relative path of the test resource.
   * @return The absolute file path.
   * @throws URISyntaxException If resource path conversion fails.
   */
  private Path getResourcePath(String resource) throws URISyntaxException {
    return Paths.get(ClassLoader.getSystemResource(resource).toURI());
  }

  /**
   * Helper method to extract the list of stages from parsed YAML data.
   *
   * @param yamlData The parsed YAML data.
   * @return A list of stage names.
   */
  @SuppressWarnings("unchecked")
  private List<String> getStages(Map<String, Object> yamlData) {
    final Map<String, Object> pipeline = (Map<String, Object>) yamlData.get("pipeline");
    return (List<String>) pipeline.get("stages");
  }

  /**
   * Helper method to extract the list of jobs from parsed YAML data.
   *
   * @param yamlData The parsed YAML data.
   * @return A list of job configurations.
   */
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getJobs(Map<String, Object> yamlData) {
    return (List<Map<String, Object>>) yamlData.get("job");
  }
}
