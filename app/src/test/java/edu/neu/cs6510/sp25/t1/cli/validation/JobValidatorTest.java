package edu.neu.cs6510.sp25.t1.cli.validation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JobValidator.
 * Ensures job validation rules are properly enforced including type checking
 * and error message formatting.
 */
class JobValidatorTest {
  private JobValidator jobValidator;
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalErr = System.err;

  /**
   * Initializes the JobValidator and sets up error stream capture before each test.
   */
  @BeforeEach
  void setUp() {
    jobValidator = null;  // Will be initialized per test with correct stages
    System.setErr(new PrintStream(errContent));
  }

  /**
   * Restores the original error stream after each test.
   */
  @AfterEach
  void tearDown() {
    System.setErr(originalErr);
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

    final Map<String, String> jobStages = jobValidator.getJobStages();
    assertEquals("build", jobStages.get("BuildJob"));
    assertEquals("test", jobStages.get("TestJob"));
    assertTrue(errContent.toString().isEmpty(), "No error messages should be generated for valid jobs");
  }

  /**
   * Tests validation with a null jobs list.
   */
  @Test
  void testNullJobsList() {
    jobValidator = new JobValidator(List.of("build", "test"));
    assertFalse(jobValidator.validateJobs(null));
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.matches("pipeline\\.yaml:\\d+:\\d+: At least one job must be defined.*"));
  }

  /**
   * Tests validation with an empty jobs list.
   */
  @Test
  void testEmptyJobsList() {
    jobValidator = new JobValidator(List.of("build", "test"));
    assertFalse(jobValidator.validateJobs(Collections.emptyList()));
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.matches("pipeline\\.yaml:\\d+:\\d+: At least one job must be defined.*"));
  }

  /**
   * Tests a job with wrong type for name field (integer instead of string).
   */
  @Test
  void testWrongTypeForName() {
    final List<String> stages = Arrays.asList("build", "test");
    jobValidator = new JobValidator(stages);

    final Map<String, Object> job = new HashMap<>();
    job.put("name", 123);  // Wrong type (Integer instead of String)
    job.put("stage", "build");
    job.put("image", "maven:3.8");
    job.put("script", Arrays.asList("mvn clean", "mvn package"));

    assertFalse(jobValidator.validateJobs(Collections.singletonList(job)));
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.matches("pipeline\\.yaml:\\d+:\\d+: Wrong type for value '123' in key 'name', expected String but got Integer.*"));
  }

  /**
   * Tests a job with wrong type for stage field (integer instead of string).
   */
  @Test
  void testWrongTypeForStage() {
    final List<String> stages = Arrays.asList("build", "test");
    jobValidator = new JobValidator(stages);

    final Map<String, Object> job = new HashMap<>();
    job.put("name", "BuildJob");
    job.put("stage", 1);  // Wrong type (Integer instead of String)
    job.put("image", "maven:3.8");
    job.put("script", Arrays.asList("mvn clean", "mvn package"));

    assertFalse(jobValidator.validateJobs(Collections.singletonList(job)));
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.matches("pipeline\\.yaml:\\d+:\\d+: Wrong type for value '1' in key 'stage', expected String but got Integer.*"));
  }

  /**
   * Tests a job with wrong type for image field (integer instead of string).
   */
  @Test
  void testWrongTypeForImage() {
    final List<String> stages = Arrays.asList("build", "test");
    jobValidator = new JobValidator(stages);

    final Map<String, Object> job = new HashMap<>();
    job.put("name", "BuildJob");
    job.put("stage", "build");
    job.put("image", 123);  // Wrong type (Integer instead of String)
    job.put("script", Arrays.asList("mvn clean", "mvn package"));

    assertFalse(jobValidator.validateJobs(Collections.singletonList(job)));
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.matches("pipeline\\.yaml:\\d+:\\d+: Wrong type for value '123' in key 'image', expected String but got Integer.*"));
  }

  /**
   * Tests a job with wrong type for script field (string instead of list).
   */
  @Test
  void testWrongTypeForScript() {
    final List<String> stages = Arrays.asList("build", "test");
    jobValidator = new JobValidator(stages);

    final Map<String, Object> job = new HashMap<>();
    job.put("name", "BuildJob");
    job.put("stage", "build");
    job.put("image", "maven:3.8");
    job.put("script", "mvn clean");  // Wrong type (String instead of List)

    assertFalse(jobValidator.validateJobs(Collections.singletonList(job)));
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.matches("pipeline\\.yaml:\\d+:\\d+: Wrong type for value 'mvn clean' in key 'script', expected List but got String.*"));
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
    assertFalse(jobValidator.validateJobs(jobs));
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.matches("pipeline\\.yaml:\\d+:\\d+: Missing required field 'name'.*"));
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
    assertFalse(jobValidator.validateJobs(jobs));
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.matches("pipeline\\.yaml:\\d+:\\d+: Missing required field 'stage'.*"));
  }

  /**
   * Tests a job configuration where a job is missing the 'image' field.
   */
  @Test
  void testMissingJobImage() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/job/missing_job_image.yml");
    final Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    final List<String> stages = getStages(yamlData);
    final List<Map<String, Object>> jobs = getJobs(yamlData);

    jobValidator = new JobValidator(stages);
    assertFalse(jobValidator.validateJobs(jobs));
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.matches("pipeline\\.yaml:\\d+:\\d+: Missing required field 'image'.*"));
  }

  /**
   * Tests a job configuration with empty script list.
   */
  @Test
  void testEmptyScript() {
    final List<String> stages = Arrays.asList("build", "test");
    jobValidator = new JobValidator(stages);

    final Map<String, Object> job = new HashMap<>();
    job.put("name", "BuildJob");
    job.put("stage", "build");
    job.put("image", "maven:3.8");
    job.put("script", Collections.emptyList());

    assertFalse(jobValidator.validateJobs(Collections.singletonList(job)));
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.matches("pipeline\\.yaml:\\d+:\\d+: Job 'BuildJob' must have at least one script command.*"));
  }

  /**
   * Tests a job with non-string script commands.
   */
  @Test
  void testNonStringScriptCommands() {
    final List<String> stages = Arrays.asList("build", "test");
    jobValidator = new JobValidator(stages);

    final Map<String, Object> job = new HashMap<>();
    job.put("name", "BuildJob");
    job.put("stage", "build");
    job.put("image", "maven:3.8");
    job.put("script", Arrays.asList("mvn clean", 123, "mvn package"));

    assertFalse(jobValidator.validateJobs(Collections.singletonList(job)));
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.matches("pipeline\\.yaml:\\d+:\\d+: Script command '123' must be a string in job 'BuildJob'.*"));
  }

  /**
   * Tests a job that references a non-existent stage.
   */
  @Test
  void testNonExistentStage() {
    final List<String> stages = Arrays.asList("build", "test");
    jobValidator = new JobValidator(stages);

    final Map<String, Object> job = new HashMap<>();
    job.put("name", "DeployJob");
    job.put("stage", "deploy");  // Stage doesn't exist
    job.put("image", "maven:3.8");
    job.put("script", Arrays.asList("mvn deploy"));

    assertFalse(jobValidator.validateJobs(Collections.singletonList(job)));
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.matches("pipeline\\.yaml:\\d+:\\d+: Job 'DeployJob' references non-existent stage 'deploy'.*"));
  }

  /**
   * Tests a job configuration with duplicate job names in the same stage.
   */
  @Test
  void testDuplicateJobNames() {
    final List<String> stages = Arrays.asList("build", "test");
    jobValidator = new JobValidator(stages);

    final Map<String, Object> job1 = new HashMap<>();
    job1.put("name", "BuildJob");
    job1.put("stage", "build");
    job1.put("image", "maven:3.8");
    job1.put("script", Arrays.asList("mvn clean"));

    final Map<String, Object> job2 = new HashMap<>();
    job2.put("name", "BuildJob");  // Duplicate name in same stage
    job2.put("stage", "build");
    job2.put("image", "maven:3.8");
    job2.put("script", Arrays.asList("mvn package"));

    assertFalse(jobValidator.validateJobs(Arrays.asList(job1, job2)));
    final String errorOutput = errContent.toString();
    assertTrue(errorOutput.matches("pipeline\\.yaml:\\d+:\\d+: Duplicate job name 'BuildJob' in stage 'build'.*"));
  }

  /**
   * Helper method to retrieve a test resource file path.
   */
  private Path getResourcePath(String resource) throws URISyntaxException {
    final Path path = Paths.get(ClassLoader.getSystemResource(resource).toURI());
    assertNotNull(path, "Resource path should not be null");
    return path;
  }

  /**
   * Helper method to extract the list of stages from parsed YAML data.
   */
  @SuppressWarnings("unchecked")
  private List<String> getStages(Map<String, Object> yamlData) {
    assertNotNull(yamlData, "YAML data should not be null");
    final Map<String, Object> pipeline = (Map<String, Object>) yamlData.get("pipeline");
    assertNotNull(pipeline, "Pipeline configuration should not be null");
    return (List<String>) pipeline.get("stages");
  }

  /**
   * Helper method to extract the list of jobs from parsed YAML data.
   */
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getJobs(Map<String, Object> yamlData) {
    assertNotNull(yamlData, "YAML data should not be null");
    return (List<Map<String, Object>>) yamlData.get("job");
  }
}