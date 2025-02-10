package edu.neu.cs6510.sp25.t1.cli.validation;

import edu.neu.cs6510.sp25.t1.cli.util.ErrorHandler;
import java.util.*;
import org.yaml.snakeyaml.error.Mark;

/**
 * DependencyValidator checks job dependencies to ensure:
 * - All dependencies exist.
 * - No cyclic dependencies exist.
 */
public class DependencyValidator {
  private final Map<String, List<String>> jobDependencies;
  private final Set<String> allJobNames;
  private List<String> currentCycle;
  private final ErrorHandler.Location baseLocation;

  /**
   * Initializes the validator with job dependencies and location information.
   *
   * @param jobDependencies Map of jobs to their dependencies
   * @param yamlMark Location mark from the YAML parser
   */
  public DependencyValidator(Map<String, List<String>> jobDependencies, Mark yamlMark) {
    this.jobDependencies = jobDependencies;
    this.allJobNames = new HashSet<>(jobDependencies.keySet());
    this.currentCycle = new ArrayList<>();
    this.baseLocation = ErrorHandler.createLocation(yamlMark, "dependencies");
  }

  /**
   * Validates all dependencies and dependency relationships.
   *
   * @return true if all dependencies are valid, false otherwise
   */
  public boolean validateDependencies() {
    // Check if all dependencies exist
    for (Map.Entry<String, List<String>> entry : jobDependencies.entrySet()) {
      final String jobName = entry.getKey();
      for (String dependency : entry.getValue()) {
        if (!allJobNames.contains(dependency)) {
          System.err.println(ErrorHandler.formatMissingFieldError(
              baseLocation,
              String.format("Job '%s' has a dependency on non-existent job '%s'", jobName, dependency)
          ));
          return false;
        }
      }
    }

    // Check for cyclic dependencies
    if (hasCycle()) {
      System.err.println(ErrorHandler.formatCycleError(baseLocation, currentCycle));
      return false;
    }

    return true;
  }

  /**
   * Detects cycles using Depth-First Search (DFS).
   *
   * @return true if a cycle is detected, false otherwise.
   */
  private boolean hasCycle() {
    final Set<String> visited = new HashSet<>();
    final List<String> path = new ArrayList<>();

    for (String job : jobDependencies.keySet()) {
      if (detectCycle(job, visited, new HashSet<>(), path)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Recursive helper method to detect cycles in dependencies.
   *
   * @param job The current job being checked.
   * @param visited A set of visited nodes.
   * @param recursionStack A set tracking the recursion stack to detect cycles.
   * @param path List tracking the current path for cycle reporting.
   * @return true if a cycle is detected, false otherwise.
   */
  private boolean detectCycle(String job, Set<String> visited,
      Set<String> recursionStack, List<String> path) {
    if (recursionStack.contains(job)) {
      final int cycleStart = path.indexOf(job);
      currentCycle = new ArrayList<>(path.subList(cycleStart, path.size()));
      return true;
    }

    if (visited.contains(job)) {
      return false;
    }

    visited.add(job);
    recursionStack.add(job);
    path.add(job);

    if (jobDependencies.containsKey(job)) {
      for (String dep : jobDependencies.get(job)) {
        if (detectCycle(dep, visited, recursionStack, path)) {
          return true;
        }
      }
    }

    recursionStack.remove(job);
    path.remove(path.size() - 1);
    return false;
  }
}