package edu.neu.cs6510.sp25.t1.cli.validation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.Mark;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DependencyValidatorTest {
  private Map<String, List<String>> dependencies;
  private Mark testMark;
  private final ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
  private final PrintStream originalErr = System.err;

  @BeforeEach
  void setUp() {
    dependencies = new HashMap<>();
    testMark = new Mark("pipeline.yaml", 0, 5, 10, new int[]{}, 0);
    System.setErr(new PrintStream(errorOutput));
  }

  @Test
  void validateDependencies_WithValidDependencies_ShouldReturnTrue() {
    dependencies.put("job1", Arrays.asList("job2", "job3"));
    dependencies.put("job2", Collections.emptyList());
    dependencies.put("job3", Collections.singletonList("job2"));
    final DependencyValidator validator = new DependencyValidator(dependencies, testMark);

    final boolean result = validator.validateDependencies();

    assertTrue(result);
    assertEquals("", errorOutput.toString());
  }

  @Test
  void validateDependencies_WithNonExistentDependency_ShouldReturnFalse() {
    dependencies.put("job1", Collections.singletonList("non-existent-job"));
    dependencies.put("job2", Collections.emptyList());
    final DependencyValidator validator = new DependencyValidator(dependencies, testMark);

    final boolean result = validator.validateDependencies();

    assertFalse(result);
    final String expectedError = "pipeline.yaml:6:11: Missing required field 'Job 'job1' has a dependency on non-existent job 'non-existent-job''";
    assertTrue(errorOutput.toString().trim().contains(expectedError));
  }

  @Test
  void validateDependencies_WithCyclicDependency_ShouldReturnFalse() {
    dependencies.put("job1", Collections.singletonList("job2"));
    dependencies.put("job2", Collections.singletonList("job3"));
    dependencies.put("job3", Collections.singletonList("job1"));
    final DependencyValidator validator = new DependencyValidator(dependencies, testMark);

    final boolean result = validator.validateDependencies();

    assertFalse(result);
    final String expectedError = "pipeline.yaml:6:11: Dependency cycle detected: job1 -> job2 -> job3 -> job1";
    assertTrue(errorOutput.toString().trim().contains(expectedError));
  }

  @Test
  void validateDependencies_WithSelfDependency_ShouldReturnFalse() {
    dependencies.put("job1", Collections.singletonList("job1"));
    final DependencyValidator validator = new DependencyValidator(dependencies, testMark);

    final boolean result = validator.validateDependencies();

    assertFalse(result);
    final String expectedError = "pipeline.yaml:6:11: Dependency cycle detected: job1 -> job1";
    assertTrue(errorOutput.toString().trim().contains(expectedError));
  }

  @Test
  void validateDependencies_WithEmptyDependencies_ShouldReturnTrue() {
    dependencies.put("job1", Collections.emptyList());
    dependencies.put("job2", Collections.emptyList());
    final DependencyValidator validator = new DependencyValidator(dependencies, testMark);

    final boolean result = validator.validateDependencies();

    assertTrue(result);
    assertEquals("", errorOutput.toString());
  }

  @Test
  void validateDependencies_WithComplexDependencyGraph_ShouldReturnTrue() {
    dependencies.put("job1", Arrays.asList("job2", "job3"));
    dependencies.put("job2", Collections.singletonList("job4"));
    dependencies.put("job3", Collections.singletonList("job4"));
    dependencies.put("job4", Collections.emptyList());
    final DependencyValidator validator = new DependencyValidator(dependencies, testMark);

    final boolean result = validator.validateDependencies();

    assertTrue(result);
    assertEquals("", errorOutput.toString());
  }

  @Test
  void validateDependencies_WithComplexCyclicDependency_ShouldReturnFalse() {
    dependencies.put("job1", Arrays.asList("job2", "job3"));
    dependencies.put("job2", Collections.singletonList("job4"));
    dependencies.put("job3", Collections.singletonList("job4"));
    dependencies.put("job4", Collections.singletonList("job1"));
    final DependencyValidator validator = new DependencyValidator(dependencies, testMark);

    final boolean result = validator.validateDependencies();

    assertFalse(result);
    assertTrue(errorOutput.toString().trim().contains("Dependency cycle detected:"));
  }

  @AfterEach
  void tearDown() {
    System.setErr(originalErr);
  }
}