package edu.neu.cs6510.sp25.t1.cli.validation;

import java.util.*;

/**
 * DependencyValidator checks job dependencies to ensure:
 * - All dependencies exist.
 * - No cyclic dependencies exist.
 */
public class DependencyValidator {
  private final Map<String, List<String>> jobDependencies;
  private final Set<String> allJobNames;

  /**
   * Initializes the validator with job dependencies.
   *
   * @param jobDependencies A map where the key is a job name and the value is a list of dependent job names.
   */
  public DependencyValidator(Map<String, List<String>> jobDependencies) {
    this.jobDependencies = jobDependencies;
    this.allJobNames = new HashSet<>(jobDependencies.keySet());
  }

  /**
   * Validates dependencies, ensuring:
   * - All dependencies reference existing jobs.
   * - No cycles exist.
   *
   * @return true if no issues are found, false otherwise.
   */
  public boolean validateDependencies() {
    // Check if all dependencies exist
    for (Map.Entry<String, List<String>> entry : jobDependencies.entrySet()) {
      final String jobName = entry.getKey();
      for (String dependency : entry.getValue()) {
        if (!allJobNames.contains(dependency)) {
          System.err.println("Error: Job '" + jobName + "' has a dependency on non-existent job '" + dependency + "'.");
          return false;
        }
      }
    }

    // Check for cyclic dependencies
    return !hasCycle();
  }

  /**
   * Detects cycles using Depth-First Search (DFS).
   *
   * @return true if a cycle is detected, false otherwise.
   */
  private boolean hasCycle() {
    final Set<String> visited = new HashSet<>();
    final Set<String> recursionStack = new HashSet<>();

    for (String job : jobDependencies.keySet()) {
      if (detectCycle(job, visited, recursionStack)) {
        System.err.println("Error: Cycle detected in job dependencies.");
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
   * @return true if a cycle is detected, false otherwise.
   */
  private boolean detectCycle(String job, Set<String> visited, Set<String> recursionStack) {
    if (recursionStack.contains(job)) {
      return true; // Cycle detected
    }
    if (visited.contains(job)) {
      return false; // Already processed
    }

    visited.add(job);
    recursionStack.add(job);

    if (jobDependencies.containsKey(job)) {
      for (String dep : jobDependencies.get(job)) {
        if (detectCycle(dep, visited, recursionStack)) {
          return true;
        }
      }
    }

    recursionStack.remove(job);
    return false;
  }
}
