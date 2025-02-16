//package edu.neu.cs6510.sp25.t1.cli.validation;
//
//import edu.neu.cs6510.sp25.t1.cli.util.ErrorHandler;
//import java.util.*;
//import org.yaml.snakeyaml.error.Mark;
//
///**
// * DependencyValidator checks job dependencies to ensure:
// * - All dependencies exist.
// * - No cyclic dependencies exist.
// */
//public class DependencyValidator {
//  private final Map<String, List<String>> jobDependencies;
//  private final Set<String> allJobNames;
//  private List<String> currentCycle;
//  private final ErrorHandler.Location baseLocation;
//
//  /**
//   * Initializes the validator with job dependencies and location information.
//   *
//   * @param jobDependencies Map of jobs to their dependencies
//   * @param yamlMark Location mark from the YAML parser
//   */
//  public DependencyValidator(Map<String, List<String>> jobDependencies, Mark yamlMark, String filename) {
//    this.jobDependencies = jobDependencies;
//    this.allJobNames = new HashSet<>(jobDependencies.keySet());
//    this.currentCycle = new ArrayList<>();
//    this.baseLocation = ErrorHandler.createLocation(filename, yamlMark, "dependencies");
//  }
//
//  /**
//   * Validates all dependencies and dependency relationships.
//   *
//   * @return true if all dependencies are valid, false otherwise
//   */
//  public boolean validateDependencies() {
//    // Check if all dependencies exist
//    for (Map.Entry<String, List<String>> entry : jobDependencies.entrySet()) {
//      final String jobName = entry.getKey();
//      for (String dependency : entry.getValue()) {
//        if (!allJobNames.contains(dependency)) {
//          System.err.println(ErrorHandler.formatMissingFieldError(
//              baseLocation,
//              String.format("Job '%s' has a dependency on non-existent job '%s'", jobName, dependency)
//          ));
//          return false;
//        }
//      }
//    }
//
//    // Check for cyclic dependencies
//    if (hasCycle()) {
//      System.err.println(ErrorHandler.formatCycleError(baseLocation, currentCycle));
//      return false;
//    }
//
//    return true;
//  }
//
//  /**
//   * Detects cycles using Depth-First Search (DFS).
//   *
//   * @return true if a cycle is detected, false otherwise.
//   */
//  private boolean hasCycle() {
//    final Set<String> visited = new HashSet<>();
//    final List<String> path = new ArrayList<>();
//
//    for (String job : jobDependencies.keySet()) {
//      if (detectCycle(job, visited, new HashSet<>(), path)) {
//        return true;
//      }
//    }
//    return false;
//  }
//
//  /**
//   * Recursive helper method to detect cycles in dependencies.
//   *
//   * @param job The current job being checked.
//   * @param visited A set of visited nodes.
//   * @param recursionStack A set tracking the recursion stack to detect cycles.
//   * @param path List tracking the current path for cycle reporting.
//   * @return true if a cycle is detected, false otherwise.
//   */
//  private boolean detectCycle(String job, Set<String> visited,
//      Set<String> recursionStack, List<String> path) {
//    if (recursionStack.contains(job)) {
//      final int cycleStart = path.indexOf(job);
//      currentCycle = new ArrayList<>(path.subList(cycleStart, path.size()));
//      return true;
//    }
//
//    if (visited.contains(job)) {
//      return false;
//    }
//
//    visited.add(job);
//    recursionStack.add(job);
//    path.add(job);
//
//    if (jobDependencies.containsKey(job)) {
//      for (String dep : jobDependencies.get(job)) {
//        if (detectCycle(dep, visited, recursionStack, path)) {
//          return true;
//        }
//      }
//    }
//
//    recursionStack.remove(job);
//    path.remove(path.size() - 1);
//    return false;
//  }
//}
package edu.neu.cs6510.sp25.t1.cli.validation;

import edu.neu.cs6510.sp25.t1.cli.util.ErrorHandler;
import java.util.*;
import org.yaml.snakeyaml.error.Mark;

/**
 * Enhanced DependencyValidator that can detect multiple cyclic dependency paths.
 */
public class DependencyValidator {
  private final Map<String, List<String>> jobDependencies;
  private final Set<String> allJobNames;
  private final List<List<String>> detectedCycles;
  private final ErrorHandler.Location baseLocation;

  public DependencyValidator(Map<String, List<String>> jobDependencies, Mark yamlMark, String filename) {
    this.jobDependencies = jobDependencies;
    this.allJobNames = new HashSet<>(jobDependencies.keySet());
    this.detectedCycles = new ArrayList<>();
    this.baseLocation = ErrorHandler.createLocation(filename, yamlMark, "dependencies");
  }

  public boolean validateDependencies() {
    // Check if all dependencies exist
    if (!validateExistence()) {
      return false;
    }

    // Find all cycles
    findAllCycles();

    // Report all detected cycles
    if (!detectedCycles.isEmpty()) {
      for (List<String> cycle : detectedCycles) {
        System.err.println(ErrorHandler.formatCycleError(baseLocation, cycle));
      }
      return false;
    }

    return true;
  }

  private boolean validateExistence() {
    for (Map.Entry<String, List<String>> entry : jobDependencies.entrySet()) {
      final String jobName = entry.getKey();
      for (String dependency : entry.getValue()) {
        if (!allJobNames.contains(dependency)) {
          System.err.println(ErrorHandler.formatMissingFieldError(
              baseLocation,
              String.format("Job '%s' has a dependency on non-existent job '%s'",
                  jobName, dependency)
          ));
          return false;
        }
      }
    }
    return true;
  }

  private void findAllCycles() {
    final Set<String> processedCycles = new HashSet<>();

    // Start DFS from each job to find all possible cycles
    // Need to start from each node to ensure we find all unique cycles
    for (String startJob : jobDependencies.keySet()) {
      final Set<String> recursionStack = new HashSet<>();
      final List<String> currentPath = new ArrayList<>();
      final Set<String> visited = new HashSet<>();

      detectCycles(startJob, visited, recursionStack, currentPath, processedCycles);
    }
  }

  private void detectCycles(
      String job,
      Set<String> visited,
      Set<String> recursionStack,
      List<String> currentPath,
      Set<String> processedCycles
  ) {
    // If job is in recursion stack, we found a cycle
    if (recursionStack.contains(job)) {
      final int cycleStart = currentPath.indexOf(job);
      final List<String> cycle = new ArrayList<>(currentPath.subList(cycleStart, currentPath.size()));

      // Create a unique signature for this cycle to avoid duplicates
      final String signature = createCycleSignature(cycle);
      if (!processedCycles.contains(signature)) {
        detectedCycles.add(cycle);
        processedCycles.add(signature);
      }
      return;
    }

    // If we've visited this node in current DFS path, no need to explore again
    if (visited.contains(job)) {
      return;
    }

    // Add current job to tracking sets and current path
    visited.add(job);
    recursionStack.add(job);
    currentPath.add(job);

    // Recursively check all dependencies
    final List<String> dependencies = jobDependencies.getOrDefault(job, Collections.emptyList());
    for (String dep : dependencies) {
      detectCycles(dep, visited, recursionStack, currentPath, processedCycles);
    }

    // Backtrack: remove job from recursion stack and current path
    recursionStack.remove(job);
    currentPath.remove(currentPath.size() - 1);
  }

  /**
   * Creates a unique signature for a cycle to avoid duplicate reporting
   */
  private String createCycleSignature(List<String> cycle) {
    // Find the "smallest" job name to use as the starting point
    String minJob = cycle.get(0);
    int minIndex = 0;
    for (int i = 1; i < cycle.size(); i++) {
      if (cycle.get(i).compareTo(minJob) < 0) {
        minJob = cycle.get(i);
        minIndex = i;
      }
    }

    // Rotate the cycle to start with the smallest job name
    final StringBuilder signature = new StringBuilder();
    for (int i = 0; i < cycle.size(); i++) {
      final int index = (minIndex + i) % cycle.size();
      signature.append(cycle.get(index)).append("->");
    }
    signature.append(minJob);

    return signature.toString();
  }
}