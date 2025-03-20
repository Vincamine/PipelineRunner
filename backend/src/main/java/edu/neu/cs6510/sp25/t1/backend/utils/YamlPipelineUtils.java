package edu.neu.cs6510.sp25.t1.backend.utils;

import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;

/**
 * Utility class for reading and validating pipeline YAML files.
 * Enhanced to support the queue-based execution system by providing
 * more comprehensive validation and structure support.
 */
public class YamlPipelineUtils {

  // YAML validation constants
  private static final String STAGES_KEY = "stages";
  private static final String NAME_KEY = "name";
  private static final String JOBS_KEY = "jobs";
  private static final String SCRIPT_KEY = "script";
  private static final String IMAGE_KEY = "image";
  private static final String ALLOW_FAILURE_KEY = "allow_failure";
  private static final String DEPENDENCIES_KEY = "dependencies";
  private static final String REPOSITORY_KEY = "repository";
  private static final String BRANCH_KEY = "branch";

  /**
   * Reads and parses a pipeline YAML file.
   *
   * @param filePath The path to the pipeline YAML file.
   * @return Parsed YAML as a Map.
   * @throws IOException If reading the file fails.
   * @throws ValidationException If the file structure is invalid.
   */
  public static Map<String, Object> readPipelineYaml(String filePath) throws IOException, ValidationException {
    Path path = Paths.get(filePath).isAbsolute()
            ? Paths.get(filePath)
            : Paths.get(System.getProperty("user.dir")).resolve(filePath).normalize();
    PipelineLogger.info("Checking pipeline file: " + path);

    if (!Files.exists(path)) {
      PipelineLogger.error("Pipeline configuration file not found: " + path.toAbsolutePath());
      throw new ValidationException(filePath, 1, 1, "Pipeline configuration file not found: " + path.toAbsolutePath());
    }
    
    if (!Files.isReadable(path)) {
      PipelineLogger.error("Pipeline configuration file is not readable: " + path.toAbsolutePath());
      throw new ValidationException(filePath, 1, 1, "Pipeline configuration file is not readable: " + path.toAbsolutePath());
    }

    try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
      Yaml yaml = new Yaml();
      Map<String, Object> pipelineConfig = yaml.load(fileInputStream);

      if (pipelineConfig == null || pipelineConfig.isEmpty()) {
        PipelineLogger.error("Pipeline configuration is empty or malformed.");
        throw new IllegalArgumentException("Pipeline configuration is empty or malformed.");
      }

      PipelineLogger.info("Pipeline configuration successfully loaded from: " + filePath);
      return pipelineConfig;
    } catch (Exception e) {
      PipelineLogger.error("Error parsing pipeline YAML: " + e.getMessage());
      throw new IOException("Error parsing pipeline YAML: " + e.getMessage(), e);
    }
  }

  /**
   * Performs comprehensive validation of the pipeline YAML structure.
   * This enhanced version is designed to support the queue-based execution system.
   *
   * @param pipelineConfig The parsed pipeline configuration.
   * @throws IllegalArgumentException If validation fails.
   */
  public static void validatePipelineConfig(Map<String, Object> pipelineConfig) {
    // Check required top-level fields
    if (!pipelineConfig.containsKey(STAGES_KEY)) {
      PipelineLogger.error("Invalid pipeline.yaml: Missing 'stages' field.");
      throw new IllegalArgumentException("Invalid pipeline.yaml: Missing 'stages' field.");
    }

    // Validate stages
    validateStages(pipelineConfig);

    PipelineLogger.info("Pipeline YAML validation successful: Structure is valid.");
  }
  
  /**
   * Validates the stages section of the pipeline configuration.
   *
   * @param pipelineConfig The parsed pipeline configuration.
   * @throws IllegalArgumentException If validation fails.
   */
  @SuppressWarnings("unchecked")
  private static void validateStages(Map<String, Object> pipelineConfig) {
    Object stagesObj = pipelineConfig.get(STAGES_KEY);
    if (!(stagesObj instanceof List<?>)) {
      PipelineLogger.error("Invalid pipeline.yaml: 'stages' must be a list.");
      throw new IllegalArgumentException("Invalid pipeline.yaml: 'stages' must be a list.");
    }
    
    List<Map<String, Object>> stages = new ArrayList<>();
    for (Object stageObj : (List<?>) stagesObj) {
      if (!(stageObj instanceof Map)) {
        PipelineLogger.error("Invalid pipeline.yaml: Each stage must be a map.");
        throw new IllegalArgumentException("Invalid pipeline.yaml: Each stage must be a map.");
      }
      stages.add((Map<String, Object>) stageObj);
    }
    
    // Validate each stage
    for (int i = 0; i < stages.size(); i++) {
      validateStage(stages.get(i), i);
    }
    
    // Validate stage names are unique
    validateUniqueStageNames(stages);
  }
  
  /**
   * Validates an individual stage in the pipeline.
   *
   * @param stage The stage configuration.
   * @param index The index of the stage in the list.
   * @throws IllegalArgumentException If validation fails.
   */
  private static void validateStage(Map<String, Object> stage, int index) {
    if (!stage.containsKey(NAME_KEY)) {
      PipelineLogger.error("Invalid pipeline.yaml: Stage at index " + index + " is missing 'name' field.");
      throw new IllegalArgumentException("Invalid pipeline.yaml: Stage at index " + index + " is missing 'name' field.");
    }
    
    String stageName = (String) stage.get(NAME_KEY);
    if (stageName == null || stageName.trim().isEmpty()) {
      PipelineLogger.error("Invalid pipeline.yaml: Stage at index " + index + " has empty name.");
      throw new IllegalArgumentException("Invalid pipeline.yaml: Stage at index " + index + " has empty name.");
    }
    
    // Check if jobs exist and are well-formed
    if (!stage.containsKey(JOBS_KEY)) {
      PipelineLogger.error("Invalid pipeline.yaml: Stage '" + stageName + "' is missing 'jobs' field.");
      throw new IllegalArgumentException("Invalid pipeline.yaml: Stage '" + stageName + "' is missing 'jobs' field.");
    }
    
    validateJobs(stage, stageName);
  }
  
  /**
   * Validates that all stage names are unique.
   *
   * @param stages The list of stages.
   * @throws IllegalArgumentException If validation fails.
   */
  private static void validateUniqueStageNames(List<Map<String, Object>> stages) {
    Map<String, Integer> stageNames = new HashMap<>();
    
    for (int i = 0; i < stages.size(); i++) {
      String name = (String) stages.get(i).get(NAME_KEY);
      if (stageNames.containsKey(name)) {
        PipelineLogger.error("Invalid pipeline.yaml: Duplicate stage name '" + name + "' at indices " 
            + stageNames.get(name) + " and " + i);
        throw new IllegalArgumentException("Invalid pipeline.yaml: Duplicate stage name '" + name + "'.");
      }
      stageNames.put(name, i);
    }
  }
  
  /**
   * Validates jobs in a stage.
   *
   * @param stage The stage configuration.
   * @param stageName The name of the stage.
   * @throws IllegalArgumentException If validation fails.
   */
  @SuppressWarnings("unchecked")
  private static void validateJobs(Map<String, Object> stage, String stageName) {
    Object jobsObj = stage.get(JOBS_KEY);
    if (!(jobsObj instanceof List<?>)) {
      PipelineLogger.error("Invalid pipeline.yaml: Jobs in stage '" + stageName + "' must be a list.");
      throw new IllegalArgumentException("Invalid pipeline.yaml: Jobs in stage '" + stageName + "' must be a list.");
    }
    
    List<Map<String, Object>> jobs = new ArrayList<>();
    for (Object jobObj : (List<?>) jobsObj) {
      if (!(jobObj instanceof Map)) {
        PipelineLogger.error("Invalid pipeline.yaml: Each job in stage '" + stageName + "' must be a map.");
        throw new IllegalArgumentException("Invalid pipeline.yaml: Each job in stage '" + stageName + "' must be a map.");
      }
      jobs.add((Map<String, Object>) jobObj);
    }
    
    // Validate each job
    for (int i = 0; i < jobs.size(); i++) {
      validateJob(jobs.get(i), stageName, i);
    }
    
    // Validate job names are unique within a stage
    validateUniqueJobNames(jobs, stageName);
    
    // Validate job dependencies
    validateJobDependencies(jobs, stageName);
  }
  
  /**
   * Validates an individual job in a stage.
   *
   * @param job The job configuration.
   * @param stageName The name of the stage.
   * @param index The index of the job in the list.
   * @throws IllegalArgumentException If validation fails.
   */
  private static void validateJob(Map<String, Object> job, String stageName, int index) {
    if (!job.containsKey(NAME_KEY)) {
      PipelineLogger.error("Invalid pipeline.yaml: Job at index " + index + " in stage '" + stageName + "' is missing 'name' field.");
      throw new IllegalArgumentException("Invalid pipeline.yaml: Job at index " + index + " in stage '" + stageName + "' is missing 'name' field.");
    }
    
    String jobName = (String) job.get(NAME_KEY);
    if (jobName == null || jobName.trim().isEmpty()) {
      PipelineLogger.error("Invalid pipeline.yaml: Job at index " + index + " in stage '" + stageName + "' has empty name.");
      throw new IllegalArgumentException("Invalid pipeline.yaml: Job at index " + index + " in stage '" + stageName + "' has empty name.");
    }
    
    // Validate script if present
    if (job.containsKey(SCRIPT_KEY)) {
      validateScript(job.get(SCRIPT_KEY), jobName, stageName);
    }
    
    // Validate docker image if present
    if (job.containsKey(IMAGE_KEY)) {
      validateDockerImage((String) job.get(IMAGE_KEY), jobName, stageName);
    }
    
    // Validate allow_failure if present
    if (job.containsKey(ALLOW_FAILURE_KEY)) {
      Object allowFailure = job.get(ALLOW_FAILURE_KEY);
      if (!(allowFailure instanceof Boolean) && !(allowFailure instanceof String)) {
        PipelineLogger.error("Invalid pipeline.yaml: 'allow_failure' for job '" + jobName + "' in stage '" + stageName + "' must be a boolean or string.");
        throw new IllegalArgumentException("Invalid pipeline.yaml: 'allow_failure' for job '" + jobName + "' in stage '" + stageName + "' must be a boolean or string.");
      }
      
      if (allowFailure instanceof String) {
        String allowFailureStr = (String) allowFailure;
        if (!allowFailureStr.equalsIgnoreCase("true") && !allowFailureStr.equalsIgnoreCase("false")) {
          PipelineLogger.error("Invalid pipeline.yaml: 'allow_failure' for job '" + jobName + "' in stage '" + stageName + "' must be 'true' or 'false'.");
          throw new IllegalArgumentException("Invalid pipeline.yaml: 'allow_failure' for job '" + jobName + "' in stage '" + stageName + "' must be 'true' or 'false'.");
        }
      }
    }
  }
  
  /**
   * Validates that script is either a string or a list of strings.
   *
   * @param script The script value.
   * @param jobName The name of the job.
   * @param stageName The name of the stage.
   * @throws IllegalArgumentException If validation fails.
   */
  private static void validateScript(Object script, String jobName, String stageName) {
    if (script instanceof String) {
      // Single line script is valid
      return;
    } else if (script instanceof List<?>) {
      // List of script lines is valid if all elements are strings
      List<?> scriptLines = (List<?>) script;
      for (Object line : scriptLines) {
        if (!(line instanceof String)) {
          PipelineLogger.error("Invalid pipeline.yaml: Script for job '" + jobName + "' in stage '" + stageName + "' must be a string or list of strings.");
          throw new IllegalArgumentException("Invalid pipeline.yaml: Script for job '" + jobName + "' in stage '" + stageName + "' must be a string or list of strings.");
        }
      }
    } else {
      PipelineLogger.error("Invalid pipeline.yaml: Script for job '" + jobName + "' in stage '" + stageName + "' must be a string or list of strings.");
      throw new IllegalArgumentException("Invalid pipeline.yaml: Script for job '" + jobName + "' in stage '" + stageName + "' must be a string or list of strings.");
    }
  }
  
  /**
   * Validates that a Docker image name is valid.
   *
   * @param image The Docker image name.
   * @param jobName The name of the job.
   * @param stageName The name of the stage.
   * @throws IllegalArgumentException If validation fails.
   */
  private static void validateDockerImage(String image, String jobName, String stageName) {
    if (image == null || image.trim().isEmpty()) {
      PipelineLogger.error("Invalid pipeline.yaml: Docker image for job '" + jobName + "' in stage '" + stageName + "' cannot be empty.");
      throw new IllegalArgumentException("Invalid pipeline.yaml: Docker image for job '" + jobName + "' in stage '" + stageName + "' cannot be empty.");
    }
    
    // Basic validation - could be enhanced for stricter Docker image name validation
    Pattern dockerImagePattern = Pattern.compile("^[a-zA-Z0-9./\\-_:]+$");
    if (!dockerImagePattern.matcher(image).matches()) {
      PipelineLogger.warn("Docker image name '" + image + "' for job '" + jobName + "' in stage '" + stageName + "' may not be valid.");
    }
  }
  
  /**
   * Validates that all job names within a stage are unique.
   *
   * @param jobs The list of jobs.
   * @param stageName The name of the stage.
   * @throws IllegalArgumentException If validation fails.
   */
  private static void validateUniqueJobNames(List<Map<String, Object>> jobs, String stageName) {
    Map<String, Integer> jobNames = new HashMap<>();
    
    for (int i = 0; i < jobs.size(); i++) {
      String name = (String) jobs.get(i).get(NAME_KEY);
      if (jobNames.containsKey(name)) {
        PipelineLogger.error("Invalid pipeline.yaml: Duplicate job name '" + name + "' in stage '" + stageName + "' at indices " 
            + jobNames.get(name) + " and " + i);
        throw new IllegalArgumentException("Invalid pipeline.yaml: Duplicate job name '" + name + "' in stage '" + stageName + "'.");
      }
      jobNames.put(name, i);
    }
  }
  
  /**
   * Validates job dependencies to ensure they reference existing jobs and don't create circular dependencies.
   *
   * @param jobs The list of jobs.
   * @param stageName The name of the stage.
   * @throws IllegalArgumentException If validation fails.
   */
  @SuppressWarnings("unchecked")
  private static void validateJobDependencies(List<Map<String, Object>> jobs, String stageName) {
    // First, build a map of job names for quick lookup
    Map<String, Integer> jobNameMap = new HashMap<>();
    for (int i = 0; i < jobs.size(); i++) {
      jobNameMap.put((String) jobs.get(i).get(NAME_KEY), i);
    }
    
    // Then check dependencies
    for (Map<String, Object> job : jobs) {
      String jobName = (String) job.get(NAME_KEY);
      
      if (job.containsKey(DEPENDENCIES_KEY)) {
        Object dependencies = job.get(DEPENDENCIES_KEY);
        List<String> dependencyList = new ArrayList<>();
        
        // Convert dependencies to a list
        if (dependencies instanceof String) {
          // Single dependency
          dependencyList.add((String) dependencies);
        } else if (dependencies instanceof List) {
          // List of dependencies
          for (Object dep : (List<?>) dependencies) {
            if (dep instanceof String) {
              dependencyList.add((String) dep);
            } else {
              PipelineLogger.error("Invalid pipeline.yaml: Dependencies for job '" + jobName + "' in stage '" + stageName + "' must be a string or list of strings.");
              throw new IllegalArgumentException("Invalid pipeline.yaml: Dependencies for job '" + jobName + "' in stage '" + stageName + "' must be a string or list of strings.");
            }
          }
        } else {
          PipelineLogger.error("Invalid pipeline.yaml: Dependencies for job '" + jobName + "' in stage '" + stageName + "' must be a string or list of strings.");
          throw new IllegalArgumentException("Invalid pipeline.yaml: Dependencies for job '" + jobName + "' in stage '" + stageName + "' must be a string or list of strings.");
        }
        
        // Validate each dependency references an existing job
        for (String dependency : dependencyList) {
          if (!jobNameMap.containsKey(dependency)) {
            PipelineLogger.error("Invalid pipeline.yaml: Job '" + jobName + "' in stage '" + stageName + "' depends on non-existent job '" + dependency + "'.");
            throw new IllegalArgumentException("Invalid pipeline.yaml: Job '" + jobName + "' in stage '" + stageName + "' depends on non-existent job '" + dependency + "'.");
          }
          
          if (dependency.equals(jobName)) {
            PipelineLogger.error("Invalid pipeline.yaml: Job '" + jobName + "' in stage '" + stageName + "' cannot depend on itself.");
            throw new IllegalArgumentException("Invalid pipeline.yaml: Job '" + jobName + "' in stage '" + stageName + "' cannot depend on itself.");
          }
        }
      }
    }
    
    // Check for circular dependencies
    Map<String, List<String>> dependencyGraph = buildDependencyGraph(jobs);
    if (hasCycle(dependencyGraph)) {
      PipelineLogger.error("Invalid pipeline.yaml: Circular dependency detected in stage '" + stageName + "'.");
      throw new IllegalArgumentException("Invalid pipeline.yaml: Circular dependency detected in stage '" + stageName + "'.");
    }
  }
  
  /**
   * Builds a dependency graph for cycle detection.
   *
   * @param jobs The list of jobs.
   * @return A map representing the dependency graph.
   */
  @SuppressWarnings("unchecked")
  private static Map<String, List<String>> buildDependencyGraph(List<Map<String, Object>> jobs) {
    Map<String, List<String>> graph = new HashMap<>();
    
    for (Map<String, Object> job : jobs) {
      String jobName = (String) job.get(NAME_KEY);
      graph.put(jobName, new ArrayList<>());
      
      if (job.containsKey(DEPENDENCIES_KEY)) {
        Object dependencies = job.get(DEPENDENCIES_KEY);
        
        if (dependencies instanceof String) {
          graph.get(jobName).add((String) dependencies);
        } else if (dependencies instanceof List) {
          for (Object dep : (List<?>) dependencies) {
            if (dep instanceof String) {
              graph.get(jobName).add((String) dep);
            }
          }
        }
      }
    }
    
    return graph;
  }
  
  /**
   * Detects cycles in the dependency graph using DFS.
   *
   * @param graph The dependency graph.
   * @return true if a cycle is detected, false otherwise.
   */
  private static boolean hasCycle(Map<String, List<String>> graph) {
    Set<String> visited = new HashSet<>();
    Set<String> recStack = new HashSet<>();
    
    for (String node : graph.keySet()) {
      if (hasCycleDFS(graph, node, visited, recStack)) {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * DFS helper for cycle detection.
   *
   * @param graph The dependency graph.
   * @param node The current node.
   * @param visited Set of visited nodes.
   * @param recStack Set of nodes in the current recursion stack.
   * @return true if a cycle is detected, false otherwise.
   */
  private static boolean hasCycleDFS(Map<String, List<String>> graph, String node, Set<String> visited, Set<String> recStack) {
    if (recStack.contains(node)) {
      return true;
    }
    
    if (visited.contains(node)) {
      return false;
    }
    
    visited.add(node);
    recStack.add(node);
    
    for (String neighbor : graph.get(node)) {
      if (hasCycleDFS(graph, neighbor, visited, recStack)) {
        return true;
      }
    }
    
    recStack.remove(node);
    return false;
  }
  
  /**
   * Set of utility methods for HashSet creation.
   */
  private static class HashSet<T> extends java.util.HashSet<T> {
  }
}
