package edu.neu.cs6510.sp25.t1.cli.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.Mark;

import edu.neu.cs6510.sp25.t1.validation.PipelineStructureValidator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PipelineStructureValidatorTest {
  private PipelineStructureValidator validator;
  private Map<String, Object> validPipelineData;
  private Map<String, Mark> locations;
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalErr = System.err;
  private final String TEST_FILENAME = "pipeline.yaml";

  @BeforeEach
  void setUp() {
    validator = new PipelineStructureValidator();
    validPipelineData = createValidPipelineData();
    locations = new HashMap<>();
    System.setErr(new PrintStream(errContent));
  }

  @Test
  void validate_WithValidStructure() {
    assertTrue(validator.validate(validPipelineData, locations, TEST_FILENAME));
  }

  @Test
  void validate_WithMissingPipeline() {
    final Map<String, Object> data = new HashMap<>();
    assertFalse(validator.validate(data, locations, TEST_FILENAME));
  }

  @Test
  void validate_WithInvalidPipelineType() {
    validPipelineData.put("pipeline", "not-a-map");
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
  }

  @Test
  void validate_WithMissingName() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) validPipelineData.get("pipeline");
    pipeline.remove("name");
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
  }

  @Test
  void validate_WithInvalidNameType() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) validPipelineData.get("pipeline");
    pipeline.put("name", 123);
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
  }

  @Test
  void validate_WithMissingStages() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) validPipelineData.get("pipeline");
    pipeline.remove("stages");
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
  }

  @Test
  void validate_WithEmptyStages() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) validPipelineData.get("pipeline");
    pipeline.put("stages", Collections.emptyList());
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
  }

  @Test
  void validate_WithInvalidStagesType() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) validPipelineData.get("pipeline");
    pipeline.put("stages", "not-a-list");
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
  }

  @Test
  void validate_WithNonStringStages() {
    @SuppressWarnings("unchecked")
    final Map<String, Object> pipeline = (Map<String, Object>) validPipelineData.get("pipeline");
    pipeline.put("stages", Arrays.asList("build", 123, "deploy"));
    assertFalse(validator.validate(validPipelineData, locations, TEST_FILENAME));
  }

  private Map<String, Object> createValidPipelineData() {
    final Map<String, Object> pipeline = new HashMap<>();
    pipeline.put("name", "test-pipeline");
    pipeline.put("stages", Arrays.asList("build", "test", "deploy"));

    final Map<String, Object> data = new HashMap<>();
    data.put("pipeline", pipeline);
    return data;
  }
}