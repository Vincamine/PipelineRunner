package edu.neu.cs6510.sp25.t1.common.validation;
// package edu.neu.cs6510.sp25.t1.validation;

// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;

// import org.yaml.snakeyaml.error.Mark;
// import edu.neu.cs6510.sp25.t1.util.ErrorHandler;

// /**
//  * DependencyValidator: Validates job dependencies in a CI/CD pipeline YAML
//  * configuration.
//  * This class ensures that:
//  * 
//  * - All referenced job dependencies exist.
//  * - No cyclic dependencies exist within the job structure.
//  * 
//  * Validation Flow:
//  * First, checks that all job dependencies exist in the job definitions.
//  * Then, performs a depth-first search (DFS) to detect cycles in job
//  * dependencies.
//  */
// public class DependencyValidator {
//   private final Map<String, List<String>> jobDependencies;
//   private final Set<String> allJobNames;
//   private final List<List<String>> detectedCycles;
//   private final ErrorHandler.Location baseLocation;

//   /**
//    * Constructor for DependencyValidator.
//    *
//    * @param jobDependencies A map where the key is the job name, and the value is
//    *                        a list of dependent job names.
//    * @param yamlMark        The location information from the YAML parser for
//    *                        error reporting.
//    * @param filename        The filename of the YAML configuration.
//    */
//   public DependencyValidator(Map<String, List<String>> jobDependencies, Mark yamlMark, String filename) {
//     this.jobDependencies = jobDependencies;
//     this.allJobNames = new HashSet<>(jobDependencies.keySet());
//     this.detectedCycles = new ArrayList<>();
//     this.baseLocation = ErrorHandler.createLocation(filename, yamlMark, "dependencies");
//   }

//   /**
//    * Validates dependencies by ensuring:
//    * - All referenced dependencies exist.
//    * - No circular dependencies are present.
//    *
//    * @return {@code true} if dependencies are valid, {@code false} otherwise.
//    */
//   public boolean validateDependencies() {
//     // Check if all dependencies exist
//     if (!validateExistence()) {
//       return false;
//     }

//     // Find all cycles in job dependencies
//     findAllCycles();

//     // Report all detected cycles
//     if (!detectedCycles.isEmpty()) {
//       for (List<String> cycle : detectedCycles) {
//         System.err.println(ErrorHandler.formatCycleError(baseLocation, cycle));
//       }
//       return false;
//     }

//     return true;
//   }

//   /**
//    * Ensures all dependencies reference existing jobs.
//    *
//    * @return {@code true} if all dependencies exist, {@code false} if any
//    *         dependency is missing.
//    */
//   private boolean validateExistence() {
//     for (Map.Entry<String, List<String>> entry : jobDependencies.entrySet()) {
//       final String jobName = entry.getKey();
//       for (String dependency : entry.getValue()) {
//         if (!allJobNames.contains(dependency)) {
//           System.err.println(ErrorHandler.formatMissingFieldError(
//               baseLocation,
//               String.format("Job '%s' has a dependency on non-existent job '%s'", jobName, dependency)));
//           return false;
//         }
//       }
//     }
//     return true;
//   }

//   /**
//    * Detects all cycles in the job dependencies using depth-first search (DFS).
//    */
//   private void findAllCycles() {
//     final Set<String> processedCycles = new HashSet<>();

//     for (String startJob : jobDependencies.keySet()) {
//       final Set<String> recursionStack = new HashSet<>();
//       final List<String> currentPath = new ArrayList<>();
//       final Set<String> visited = new HashSet<>();

//       detectCycles(startJob, visited, recursionStack, currentPath, processedCycles);
//     }
//   }

//   /**
//    * Performs a depth-first search (DFS) to detect cycles in job dependencies.
//    *
//    * @param job             The current job being processed.
//    * @param visited         A set of already visited jobs.
//    * @param recursionStack  Tracks the current path in the DFS for cycle
//    *                        detection.
//    * @param currentPath     The current job path being explored.
//    * @param processedCycles Stores detected cycles to prevent duplicate reports.
//    */
//   private void detectCycles(
//       String job,
//       Set<String> visited,
//       Set<String> recursionStack,
//       List<String> currentPath,
//       Set<String> processedCycles) {
//     // If job is in recursion stack, we found a cycle
//     if (recursionStack.contains(job)) {
//       final int cycleStart = currentPath.indexOf(job);
//       final List<String> cycle = new ArrayList<>(currentPath.subList(cycleStart, currentPath.size()));

//       // Create a unique signature for this cycle to avoid duplicate reporting
//       final String signature = createCycleSignature(cycle);
//       if (!processedCycles.contains(signature)) {
//         detectedCycles.add(cycle);
//         processedCycles.add(signature);
//       }
//       return;
//     }

//     // If job was visited before, no need to check again
//     if (visited.contains(job)) {
//       return;
//     }

//     // Add job to tracking sets
//     visited.add(job);
//     recursionStack.add(job);
//     currentPath.add(job);

//     // Recursively check all dependencies
//     final List<String> dependencies = jobDependencies.getOrDefault(job, Collections.emptyList());
//     for (String dep : dependencies) {
//       detectCycles(dep, visited, recursionStack, currentPath, processedCycles);
//     }

//     // Backtrack: remove job from recursion stack and path
//     recursionStack.remove(job);
//     currentPath.remove(currentPath.size() - 1);
//   }

//   /**
//    * Generates a unique signature for a detected cycle to avoid duplicate reports.
//    *
//    * @param cycle The detected cyclic dependency path.
//    * @return A string representing the unique cycle signature.
//    */
//   private String createCycleSignature(List<String> cycle) {
//     // Find the lexicographically smallest job name as the start point
//     String minJob = cycle.get(0);
//     int minIndex = 0;
//     for (int i = 1; i < cycle.size(); i++) {
//       if (cycle.get(i).compareTo(minJob) < 0) {
//         minJob = cycle.get(i);
//         minIndex = i;
//       }
//     }

//     // Rotate cycle to start from the smallest job name
//     final StringBuilder signature = new StringBuilder();
//     for (int i = 0; i < cycle.size(); i++) {
//       final int index = (minIndex + i) % cycle.size();
//       signature.append(cycle.get(index)).append("->");
//     }
//     signature.append(minJob);

//     return signature.toString();
//   }
// }
