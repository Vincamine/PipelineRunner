package edu.neu.cs6510.sp25.t1.validation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.Mark;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DependencyValidatorTest {
  private Map<String, List<String>> dependencies;
  private Mark testMark;
  private final ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
  private final PrintStream originalErr = System.err;
  private final String TEST_FILENAME = "pipeline.yaml";

  @BeforeEach
  void setUp() {
    dependencies = new HashMap<>();
    testMark = new Mark(TEST_FILENAME, 0, 5, 10, new int[] {}, 0);
    System.setErr(new PrintStream(errorOutput));
  }

  @Test
  void validateDependencies_WithValidDependencies() {
    dependencies.put("job1", Arrays.asList("job2", "job3"));
    dependencies.put("job2", Collections.emptyList());
    dependencies.put("job3", Collections.singletonList("job2"));

    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);
    assertTrue(validator.validateDependencies(), "Valid dependencies should return true");
    assertEquals("", errorOutput.toString().trim());
  }

  @Test
  void validateDependencies_WithNonExistentDependency() {
    dependencies.put("job1", Collections.singletonList("non-existent-job"));
    dependencies.put("job2", Collections.emptyList());

    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);
    assertFalse(validator.validateDependencies(), "Validation should fail for a missing dependency");
    assertTrue(errorOutput.toString().contains("non-existent job"), "Expected error message for missing job");
  }

  @Test
  void validateDependencies_WithSimpleCycle() {
    dependencies.put("job1", Collections.singletonList("job2"));
    dependencies.put("job2", Collections.singletonList("job1"));

    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);
    assertFalse(validator.validateDependencies(), "Simple cycle should return false");
    assertTrue(errorOutput.toString().contains("cycle detected"), "Expected cycle detection error");
  }

  @Test
  void validateDependencies_WithComplexCycle() {
    dependencies.put("job1", Arrays.asList("job2", "job3"));
    dependencies.put("job2", Collections.singletonList("job4"));
    dependencies.put("job3", Collections.singletonList("job4"));
    dependencies.put("job4", Collections.singletonList("job1"));

    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);
    assertFalse(validator.validateDependencies(), "Complex cycle should return false");
    assertTrue(errorOutput.toString().contains("cycle detected"), "Expected cycle detection error");
  }

  @Test
  void validateDependencies_WithSelfDependency() {
    dependencies.put("job1", Collections.singletonList("job1"));

    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);
    assertFalse(validator.validateDependencies(), "Self dependency should return false");
    assertTrue(errorOutput.toString().contains("cycle detected"), "Expected cycle detection error");
  }

  @Test
  void validateDependencies_WithComplexValidGraph() {
    dependencies.put("job1", Arrays.asList("job2", "job3"));
    dependencies.put("job2", Collections.singletonList("job4"));
    dependencies.put("job3", Collections.singletonList("job4"));
    dependencies.put("job4", Collections.emptyList());

    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);
    assertTrue(validator.validateDependencies(), "Valid dependency graph should return true");
    assertEquals("", errorOutput.toString().trim());
  }

  @Test
  void validateDependencies_WithMultipleJobsWithoutDependencies() {
    dependencies.put("job1", Collections.emptyList());
    dependencies.put("job2", Collections.emptyList());
    dependencies.put("job3", Collections.emptyList());

    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);
    assertTrue(validator.validateDependencies(), "Multiple independent jobs should return true");
    assertEquals("", errorOutput.toString().trim());
  }

  @Test
  void validateDependencies_WithJobHavingNoDependencies() {
    dependencies.put("job1", Collections.emptyList());

    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);
    assertTrue(validator.validateDependencies(), "Job with no dependencies should return true");
    assertEquals("", errorOutput.toString().trim());
  }

  @AfterEach
  void tearDown() {
    System.setErr(originalErr);
  }
}
