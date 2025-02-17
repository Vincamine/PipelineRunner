package edu.neu.cs6510.sp25.t1.validation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.Mark;

import edu.neu.cs6510.sp25.t1.validation.DependencyValidator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DependencyValidatorTest {
  private Map<String, List<String>> dependencies;
  private Mark testMark;
  private final ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
  private final PrintStream originalErr = System.err;
  private final String TEST_FILENAME = "pipeline.yaml";

  @BeforeEach
  void setUp() {
    dependencies = new HashMap<>();
    testMark = new Mark("pipeline.yaml", 0, 5, 10, new int[]{}, 0);
    System.setErr(new PrintStream(errorOutput));
  }

  @Test
  void validateDependencies_WithValidDependencies() {
    dependencies.put("job1", Arrays.asList("job2", "job3"));
    dependencies.put("job2", Collections.emptyList());
    dependencies.put("job3", Collections.singletonList("job2"));
    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);

    assertTrue(validator.validateDependencies());
    assertEquals("", errorOutput.toString());
  }

  @Test
  void validateDependencies_WithNonExistentDependency() {
    dependencies.put("job1", Collections.singletonList("non-existent-job"));
    dependencies.put("job2", Collections.emptyList());
    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);

    assertFalse(validator.validateDependencies());
    assertTrue(errorOutput.toString().contains("non-existent job"));
  }

  @Test
  void validateDependencies_WithSimpleCycle() {
    dependencies.put("job1", Collections.singletonList("job2"));
    dependencies.put("job2", Collections.singletonList("job1"));
    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);

    assertFalse(validator.validateDependencies());
    assertTrue(errorOutput.toString().contains("cycle detected"));
  }


  @Test
  void validateDependencies_WithComplexCycle() {
    dependencies.put("job1", Arrays.asList("job2", "job3"));
    dependencies.put("job2", Collections.singletonList("job4"));
    dependencies.put("job3", Collections.singletonList("job4"));
    dependencies.put("job4", Collections.singletonList("job1"));
    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);

    assertFalse(validator.validateDependencies());
    assertTrue(errorOutput.toString().contains("cycle detected"));
  }

  @Test
  void validateDependencies_WithSelfDependency() {
    dependencies.put("job1", Collections.singletonList("job1"));
    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);

    assertFalse(validator.validateDependencies());
    assertTrue(errorOutput.toString().contains("cycle detected"));
  }

  @Test
  void validateDependencies_WithComplexValidGraph() {
    dependencies.put("job1", Arrays.asList("job2", "job3"));
    dependencies.put("job2", Collections.singletonList("job4"));
    dependencies.put("job3", Collections.singletonList("job4"));
    dependencies.put("job4", Collections.emptyList());
    final DependencyValidator validator = new DependencyValidator(dependencies, testMark, TEST_FILENAME);

    assertTrue(validator.validateDependencies());
    assertEquals("", errorOutput.toString());
  }

  @AfterEach
  void tearDown() {
    System.setErr(originalErr);
  }
}