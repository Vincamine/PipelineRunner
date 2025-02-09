package edu.neu.cs6510.sp25.t1.cli.validation;

import java.util.*;

/**
 * DependencyValidator checks job dependencies to ensure:
 * - All dependencies exist.
 * - No circular dependencies exist.
 */
public class DependencyValidator {
  private final Map<String, List<String>> jobDependencies;

  /**
   * Initializes the validator with job dependencies.
   *
   * @param jobDependencies A map where the key is a job name and the value is a list of dependent job names.
   */
  public DependencyValidator(Map<String, List<String>> jobDependencies) {
    this.jobDependencies = jobDependencies;
  }

  /**
   * Validates dependencies, ensuring no cycles exist.
   *
   * @return true if no cycles are found, false otherwise.
   */
  public boolean validateDependencies() {
    Set<String> visited = new HashSet<>();
    Set<String> recursionStack = new HashSet<>();

    for (String job : jobDependencies.keySet()) {
      if (detectCycle(job, visited, recursionStack)) {
        System.err.println("Error: Cycle detected in job dependencies.");
        return false;
      }
    }
    return true;
  }

  /**
   * Detects cycles using Depth-First Search (DFS).
   *
   * @param job The current job being checked.
   * @param visited A set of visited nodes.
   * @param recursionStack A set tracking the recursion stack to detect cycles.
   * @return true if a cycle is detected, false otherwise.
   */
  private boolean detectCycle(String job, Set<String> visited, Set<String> recursionStack) {
    if (recursionStack.contains(job)) {
      return true;
    }
    if (visited.contains(job)) {
      return false;
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

