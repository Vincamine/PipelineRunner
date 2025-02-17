package edu.neu.cs6510.sp25.t1.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for YamlPipelineValidator.
 * Ensures the pipeline validation rules are properly enforced.
 */
class YamlPipelineValidatorTest {
  private YamlPipelineValidator yamlPipelineValidator;

  /**
   * Initializes the validator before each test.
   */
  @BeforeEach
  void setUp() {
    yamlPipelineValidator = new YamlPipelineValidator();
  }

  /**
   * Tests a valid YAML pipeline configuration.
   */
  @Test
  void testValidPipeline() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/pipelineVal/valid_pipeline.yml");
    assertTrue(yamlPipelineValidator.validatePipeline(yamlPath.toString()));
  }

  /**
   * Tests a YAML pipeline where 'pipeline' is not a valid object.
   */
  @Test
  void testInvalidPipelineFormat() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/pipelineVal/invalid_pipeline_format.yml");
    assertFalse(yamlPipelineValidator.validatePipeline(yamlPath.toString()));
  }

  /**
   * Tests a YAML pipeline where 'stages' is not a list.
   */
  @Test
  void testInvalidStagesFormat() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/pipelineVal/invalid_stages_format.yml");
    assertFalse(yamlPipelineValidator.validatePipeline(yamlPath.toString()));
  }

  /**
   * Tests a YAML pipeline missing the 'pipeline' key.
   */
  @Test
  void testMissingPipelineKey() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/pipelineVal/missing_pipeline.yml");
    assertFalse(yamlPipelineValidator.validatePipeline(yamlPath.toString()));
  }

  /**
   * Tests a YAML pipeline missing the 'stages' key.
   */
  @Test
  void testMissingStagesKey() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/pipelineVal/missing_stages.yml");
    assertFalse(yamlPipelineValidator.validatePipeline(yamlPath.toString()));
  }

  /**
   * Tests a YAML pipeline missing the 'job' key.
   */
  @Test
  void testMissingJobsKey() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/pipelineVal/missing_jobs.yml");
    assertFalse(yamlPipelineValidator.validatePipeline(yamlPath.toString()));
  }

  /**
   * Tests a job configuration missing required fields like 'image' and 'script'.
   */
  @Test
  void testMissingJobFields() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/pipelineVal/missing_job_fields.yml");
    assertFalse(yamlPipelineValidator.validatePipeline(yamlPath.toString()));
  }

  /**
   * Tests a job referencing a non-existent stage.
   */
  @Test
  void testJobWithInvalidStage() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/pipelineVal/job_with_invalid_stage.yml");
    assertFalse(yamlPipelineValidator.validatePipeline(yamlPath.toString()));
  }

  /**
   * Tests a job referencing a non-existent dependency.
   */
  @Test
  void testJobWithMissingDependency() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/pipelineVal/job_with_missing_dependency.yml");
    assertFalse(yamlPipelineValidator.validatePipeline(yamlPath.toString()));
  }

  /**
   * Tests cyclic dependencies between jobs.
   */
  @Test
  void testJobWithCyclicDependencies() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/pipelineVal/job_with_cyclic_dependencies.yml");
    assertFalse(yamlPipelineValidator.validatePipeline(yamlPath.toString()));
  }

  /**
   * Tests error handling when reading a non-existent YAML file.
   */
  @Test
  void testYamlFileNotFound() {
    final String fakeFilePath = "src/test/resources/yaml/non_existent.yml";
    assertFalse(yamlPipelineValidator.validatePipeline(fakeFilePath));
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
}
