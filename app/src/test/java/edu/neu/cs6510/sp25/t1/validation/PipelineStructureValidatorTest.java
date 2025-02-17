package edu.neu.cs6510.sp25.t1.validation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.Mark;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PipelineStructureValidator}.
 * <p>
 * This test suite ensures that the pipeline structure follows the correct YAML format,
 * checking for missing or incorrectly formatted fields.
 */
class PipelineStructureValidatorTest {
  private PipelineStructureValidator validator;
  private Map<String, Object> validPipelineData;
  private Map<String, Mark> locations;
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalErr = System.err;
  private final String TEST_FILENAME = "pipeline.yaml";

  /**
   * Sets up the test environment by initializing the validator and valid pipeline data.
   * Redirects System.err output for error validation.
   */
  @BeforeEach
  void setUp() {
    validator = new PipelineStructureValidator();
    validPipelineData = createValidPipelineData();
    locations = new HashMap<>();
    System.setErr(new PrintStream(errContent)); // Capture error messages
  }

  /**
   * Restores the original System.err output after each test.
   */
  @AfterEach
  void tearDown() {
    System.setErr(originalErr);
  }

  /**
   * Tests a valid pipeline structure.
   */
  @Test
  void validate_WithValidStructure() {
    assertTrue(validator.validate(validPipelineData, locations, TEST_FILENAME));
    assertEquals("", errContent.toString()); // No errors should be logged
  }

  /**
   * Tests when the pipeline key is entirely missing.
   */
  @Test
  void validate_WithMissingPipeline() {
    final Map<String, Object> data = new HashMap<>();
    assertFalse(validator.validate(data, locations, TEST_FILENAME));
    assertTrue(errContent.toString().contains("pipeline"));
  }

  /**
   * Tests when the pipeline key exists but is not a map.
   */
  @Test
  void validate_WithInvalidPipelineType() {
    validPipelineData.put("pipeline", "not-a-map");
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
    assertTrue(errContent.toString().contains("pipeline"));
  }

  /**
   * Tests when the pipeline name is missing.
   */
  @Test
  void validate_WithMissingName() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) validPipelineData.get("pipeline");
    pipeline.remove("name");
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
    assertTrue(errContent.toString().contains("name"));
  }

  /**
   * Tests when the pipeline name is an invalid type.
   */
  @Test
  void validate_WithInvalidNameType() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) validPipelineData.get("pipeline");
    pipeline.put("name", 123);
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
    assertTrue(errContent.toString().contains("name"));
  }

  /**
   * Tests when the "stages" key is missing from the pipeline.
   */
  @Test
  void validate_WithMissingStages() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) validPipelineData.get("pipeline");
    pipeline.remove("stages");
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
    assertTrue(errContent.toString().contains("stages"));
  }

  /**
   * Tests when the "stages" key is present but empty.
   */
  @Test
  void validate_WithEmptyStages() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) validPipelineData.get("pipeline");
    pipeline.put("stages", Collections.emptyList());
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
    assertTrue(errContent.toString().contains("at least one stage"));
  }

  /**
   * Tests when "stages" is present but is an invalid type (not a list).
   */
  @Test
  void validate_WithInvalidStagesType() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) validPipelineData.get("pipeline");
    pipeline.put("stages", "not-a-list");
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
    assertTrue(errContent.toString().contains("stages"));
  }

  /**
   * Tests when the "stages" list contains non-string values.
   */
  @Test
  void validate_WithNonStringStages() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) validPipelineData.get("pipeline");
    pipeline.put("stages", Arrays.asList("build", 123, "deploy"));
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
    assertTrue(errContent.toString().contains("stage"));
  }

  /**
   * Tests when "stages" is explicitly set to null.
   */
  @Test
  void validate_WithNullStages() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) validPipelineData.get("pipeline");
    pipeline.put("stages", null);
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
    assertTrue(errContent.toString().contains("stages"));
  }

  /**
   * Helper method to create a valid pipeline data structure.
   *
   * @return A valid pipeline configuration map.
   */
  private Map<String, Object> createValidPipelineData() {
    final Map<String, Object> pipeline = new HashMap<>();
    pipeline.put("name", "test-pipeline");
    pipeline.put("stages", Arrays.asList("build", "test", "deploy"));

    final Map<String, Object> data = new HashMap<>();
    data.put("pipeline", pipeline);
    return data;
  }
}
