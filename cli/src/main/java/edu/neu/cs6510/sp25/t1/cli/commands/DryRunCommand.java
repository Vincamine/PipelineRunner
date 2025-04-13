package edu.neu.cs6510.sp25.t1.cli.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.model.Job;
import edu.neu.cs6510.sp25.t1.common.model.Pipeline;
import edu.neu.cs6510.sp25.t1.common.model.Stage;
import edu.neu.cs6510.sp25.t1.common.validation.parser.YamlParser;
import edu.neu.cs6510.sp25.t1.common.validation.validator.YamlPipelineValidator;
import picocli.CommandLine;

/**
 * Implements the `dry-run` command to validate a pipeline file
 * and print the execution order in structured YAML format.
 */
@CommandLine.Command(
        name = "dry-run",
        description = "Validates a pipeline file and prints execution order in YAML format."
)
public class DryRunCommand implements Callable<Integer> {

  @CommandLine.Option(
          names = {"--file", "-f"},
          description = "Path to the pipeline YAML configuration file.",
          required = true
  )
  private String filePath;
  
  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  @Override
  public Integer call() {
    File pipelineFile = new File(filePath);
    if (!pipelineFile.exists() || !pipelineFile.isFile()) {
      System.err.println("Error: Specified pipeline file does not exist: " + filePath);
      return 1;
    }

    try {
      System.out.println("Validating pipeline configuration: " + filePath);
      YamlPipelineValidator.validatePipeline(filePath);

      Pipeline pipeline = YamlParser.parseYaml(pipelineFile);
      Map<String, List<Job>> orderedStagesWithJobs = orderJobsByExecution(pipeline);

      System.out.println("\nExecution Plan:");
      printExecutionPlan(orderedStagesWithJobs);

      return 0;

    } catch (Exception e) {
      System.err.println("Validation failed: " + e.getMessage());
      return 1;
    }
  }

  /**
   * Creates a topologically-sorted execution plan of stages and jobs.
   * Each stage contains a list of jobs sorted by dependencies.
   */
  private Map<String, List<Job>> orderJobsByExecution(Pipeline pipeline) {
    // This will maintain the stage execution order
    List<Stage> orderedStages = getOrderedStages(pipeline);
    
    // Map from job name to the stage it belongs to
    Map<String, String> jobToStageMap = createJobToStageMap(pipeline);
    
    // Map from job name to Job object
    Map<String, Job> jobMap = createJobMap(pipeline);
    
    // Create ordered execution plan (stage -> ordered jobs)
    Map<String, List<Job>> executionPlan = new LinkedHashMap<>();
    
    // Prepare the execution plan with empty job lists for each stage
    for (Stage stage : orderedStages) {
      executionPlan.put(stage.getName(), new ArrayList<>());
    }
    
    // Get the list of jobs for each stage from the pipeline
    Map<String, List<Job>> stageJobsMap = new HashMap<>();
    for (Job job : pipeline.getJobs()) {
      String stageName = job.getStage();
      if (!stageJobsMap.containsKey(stageName)) {
        stageJobsMap.put(stageName, new ArrayList<>());
      }
      stageJobsMap.get(stageName).add(job);
    }
    
    // Sort jobs within each stage based on dependencies
    for (Stage stage : orderedStages) {
      List<Job> stageJobs = stageJobsMap.getOrDefault(stage.getName(), new ArrayList<>());
      List<Job> topSortedJobs = new ArrayList<>();
      Set<String> visitedJobs = new HashSet<>();
      Set<String> processingJobs = new HashSet<>();
      
      for (Job job : stageJobs) {
        if (!visitedJobs.contains(job.getName())) {
          topologicalSortJobs(job, jobMap, jobToStageMap, visitedJobs, processingJobs, topSortedJobs);
        }
      }
      
      executionPlan.put(stage.getName(), topSortedJobs);
    }
    
    return executionPlan;
  }
  
  /**
   * Performs topological sort on jobs inside a stage, respecting dependencies.
   */
  private void topologicalSortJobs(
      Job job, 
      Map<String, Job> jobMap, 
      Map<String, String> jobToStageMap,
      Set<String> visitedJobs, 
      Set<String> processingJobs, 
      List<Job> result) {
      
    if (processingJobs.contains(job.getName())) {
      throw new RuntimeException("Cyclic dependency detected for job: " + job.getName());
    }
    
    if (visitedJobs.contains(job.getName())) {
      return;
    }
    
    processingJobs.add(job.getName());
    
    // Process dependencies
    for (String dependencyName : job.getDependencies()) {
      Job dependency = jobMap.get(dependencyName);
      String dependencyStage = jobToStageMap.get(dependencyName);
      
      // Only process dependencies within the same stage here
      // Cross-stage dependencies are handled by stage ordering
      if (dependency != null && jobToStageMap.get(job.getName()).equals(dependencyStage)) {
        topologicalSortJobs(dependency, jobMap, jobToStageMap, visitedJobs, processingJobs, result);
      }
    }
    
    processingJobs.remove(job.getName());
    visitedJobs.add(job.getName());
    result.add(job);
  }
  
  /**
   * Creates a mapping from job name to stage name.
   */
  private Map<String, String> createJobToStageMap(Pipeline pipeline) {
    Map<String, String> jobToStageMap = new HashMap<>();
    
    for (Job job : pipeline.getJobs()) {
      jobToStageMap.put(job.getName(), job.getStage());
    }
    
    return jobToStageMap;
  }
  
  /**
   * Creates a mapping from job name to Job object.
   */
  private Map<String, Job> createJobMap(Pipeline pipeline) {
    Map<String, Job> jobMap = new HashMap<>();
    
    for (Job job : pipeline.getJobs()) {
      jobMap.put(job.getName(), job);
    }
    
    return jobMap;
  }

  /**
   * Orders stages based on their dependencies.
   */
  private List<Stage> getOrderedStages(Pipeline pipeline) {
    List<Stage> orderedStages = new ArrayList<>();
    Map<String, Stage> stageMap = new HashMap<>();
    Set<String> visited = new HashSet<>();
    Set<String> recursionStack = new HashSet<>();

    // Create a stage map for quick lookup
    for (Stage stage : pipeline.getStages()) {
      stageMap.put(stage.getName(), stage);
    }

    // Topologically sort the stages
    for (Stage stage : pipeline.getStages()) {
      if (!visited.contains(stage.getName())) {
        if (!visitStage(stage, stageMap, visited, recursionStack, orderedStages, pipeline)) {
          throw new RuntimeException("Error: Cyclic dependency detected between pipeline stages.");
        }
      }
    }
    
    return orderedStages;
  }

  private boolean visitStage(
      Stage stage, 
      Map<String, Stage> stageMap, 
      Set<String> visited, 
      Set<String> recursionStack, 
      List<Stage> orderedStages,
      Pipeline pipeline) {
      
    if (recursionStack.contains(stage.getName())) {
      return false;
    }
    
    if (visited.contains(stage.getName())) {
      return true;
    }

    visited.add(stage.getName());
    recursionStack.add(stage.getName());

    // Find all stages that this stage depends on through its jobs
    for (Job job : stage.getJobs()) {
      for (String dependency : job.getDependencies()) {
        Stage dependentStage = findStageContainingJob(dependency, stageMap);
        
        // If the dependency is in another stage (cross-stage dependency)
        if (dependentStage != null && !dependentStage.getName().equals(stage.getName())) {
          if (!visitStage(dependentStage, stageMap, visited, recursionStack, orderedStages, pipeline)) {
            return false;
          }
        }
      }
    }

    recursionStack.remove(stage.getName());
    orderedStages.add(stage);
    return true;
  }

