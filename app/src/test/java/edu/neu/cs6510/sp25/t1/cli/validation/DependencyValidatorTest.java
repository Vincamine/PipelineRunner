package edu.neu.cs6510.sp25.t1.cli.validation;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DependencyValidator.
 * Ensures job dependency rules are properly enforced.
 */
class DependencyValidatorTest {
  private DependencyValidator dependencyValidator;

  /**
   * Tests a valid job dependency structure.
   */
  @Test
  void testValidDependencies() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/dependencyVal/valid_dependencies.yml");
    final Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    final List<Map<String, Object>> jobs = getJobs(yamlData);
    final Map<String, List<String>> jobDependencies = extractJobDependencies(jobs);

    dependencyValidator = new DependencyValidator(jobDependencies);
    assertTrue(dependencyValidator.validateDependencies(), "Valid dependencies should pass validation.");
  }

  /**
   * Tests a job referencing a non-existent job in its 'needs' field.
   */
  @Test
  void testMissingDependency() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/dependencyVal/missing_dependency.yml");
    final Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    final List<Map<String, Object>> jobs = getJobs(yamlData);
    final Map<String, List<String>> jobDependencies = extractJobDependencies(jobs);

    dependencyValidator = new DependencyValidator(jobDependencies);
    assertFalse(dependencyValidator.validateDependencies(), "Job referencing a non-existent dependency should fail validation.");
  }

  /**
   * Tests a cyclic dependency where jobs depend on each other.
   */
  @Test
  void testCyclicDependencies() throws IOException, URISyntaxException {
    final Path yamlPath = getResourcePath("yaml/dependencyVal/cyclic_dependencies.yml");
    final Map<String, Object> yamlData = YamlLoader.loadYaml(yamlPath.toString());

    final List<Map<String, Object>> jobs = getJobs(yamlData);
    final Map<String, List<String>> jobDependencies = extractJobDependencies(jobs);

    dependencyValidator = new DependencyValidator(jobDependencies);
    assertFalse(dependencyValidator.validateDependencies(), "Cyclic dependencies should fail validation.");
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
   * Helper method to extract the list of jobs from parsed YAML data.
   *
   * @param yamlData The parsed YAML data.
   * @return A list of job configurations.
   */
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getJobs(Map<String, Object> yamlData) {
    return (List<Map<String, Object>>) yamlData.get("job");
  }

  /**
   * Helper method to extract job dependencies.
   *
   * @param jobs List of job definitions from YAML.
   * @return A map where each job name is mapped to its dependencies.
   */
  private Map<String, List<String>> extractJobDependencies(List<Map<String, Object>> jobs) {
    final Map<String, List<String>> jobDependencies = new HashMap<>();

    for (Map<String, Object> job : jobs) {
      final Object jobNameObj = job.get("name");
      if (!(jobNameObj instanceof String jobName)) {
        System.err.println("Error: Job without a valid 'name'.");
        continue;
      }

      final Object needsObj = job.get("needs");
      if (needsObj instanceof List<?> rawNeeds) {
        final List<String> needs = rawNeeds.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .collect(Collectors.toList());
        jobDependencies.put(jobName, needs);
      } else {
        jobDependencies.put(jobName, List.of()); // No dependencies
      }
    }

    return jobDependencies;
  }
}
