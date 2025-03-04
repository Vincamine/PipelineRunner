package edu.neu.cs6510.sp25.t1.worker.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.neu.cs6510.sp25.t1.common.execution.JobExecution;
import edu.neu.cs6510.sp25.t1.common.execution.StageExecution;
import edu.neu.cs6510.sp25.t1.worker.execute.JobExecutor;

/**
 * REST controller for handling job execution requests.
 */
@RestController
@RequestMapping("/api/jobs")
public class WorkerController {
  private static final Logger logger = LoggerFactory.getLogger(WorkerController.class);
  private final JobExecutor jobExecutor;

  /**
   * Constructor for WorkerController.
   *
   * @param jobExecutor JobExecutor instance to use for executing jobs.
   */
  @Autowired
  public WorkerController(JobExecutor jobExecutor) {
    this.jobExecutor = jobExecutor;
  }

  /**
   * Represents a job execution request sent to the worker.
   * This request provides execution-specific details while referencing
   * the pre-defined job configuration.
   * Sent from the backend to the worker.
   */
  public static class JobRequest {
    private final String jobId; // Unique execution ID
    private final String pipelineName; // Pipeline this job belongs to
    private final String jobName; // Reference to the job in pipeline config
    private final String commitHash; // Specific commit for execution
    private final Map<String, String> environmentVariables; // Runtime variables
    private final List<String> artifactPaths; // Paths to store collected artifacts
    private List<String> needs; // List of dependent job names

    public JobRequest(String jobId, String pipelineName, String jobName,
                      String commitHash, Map<String, String> environmentVariables,
                      List<String> artifactPaths) {
      this.jobId = jobId;
      this.pipelineName = pipelineName;
      this.jobName = jobName;
      this.commitHash = commitHash;
      this.environmentVariables = environmentVariables != null ? environmentVariables : Map.of();
      this.artifactPaths = artifactPaths != null ? artifactPaths : List.of();
      this.needs = new ArrayList<>();
    }

    public String getJobId() {
      return jobId;
    }

    public String getPipelineName() {
      return pipelineName;
    }

    public String getJobName() {
      return jobName;
    }

    public String getCommitHash() {
      return commitHash;
    }

    public Map<String, String> getEnvironmentVariables() {
      return environmentVariables;
    }

    public List<String> getArtifactPaths() {
      return artifactPaths;
    }

    public List<String> getNeeds() {
      return needs;
    }

    public void addNeeds(String need) {
      this.needs.add(need);
    }

    public boolean isRunLocal() {
      return environmentVariables.containsKey("RUN_LOCAL") &&
              Boolean.parseBoolean(environmentVariables.get("RUN_LOCAL"));
    }

    public List<String> getScript() {
      return environmentVariables.containsKey("SCRIPT")
              ? List.of(environmentVariables.get("SCRIPT").split(";"))
              : List.of();
    }

    public String getImage() {
      return environmentVariables.get("IMAGE");
    }

    @Override
    public String toString() {
      return "JobRequest{" +
              "jobId='" + jobId + '\'' +
              ", pipelineName='" + pipelineName + '\'' +
              ", jobName='" + jobName + '\'' +
              ", commitHash='" + commitHash + '\'' +
              ", environmentVariables=" + environmentVariables +
              ", artifactPaths=" + artifactPaths +
              '}';
    }
  }

  /**
   * Endpoint for executing a job.
   *
   * @param jobRequest JobRequest object containing the job name to execute.
   * @return ResponseEntity containing the result of the job execution.
   */
  @PostMapping("/execute")
  public ResponseEntity<String> executeJob(@RequestBody JobRequest jobRequest) {
    if (jobRequest == null || jobRequest.getJobName() == null || jobRequest.getJobName().isBlank()) {
      return ResponseEntity.badRequest().body("Error: Job name cannot be null or empty.");
    }

    logger.info("Received job execution request: {}", jobRequest.getJobName());

    try {
      jobExecutor.executeJob(jobRequest);
      return ResponseEntity.ok("Job execution started successfully.");
    } catch (Exception e) {
      logger.error("Error executing job: {}", jobRequest.getJobName(), e);
      return ResponseEntity.status(500).body("Job execution failed: " + e.getMessage());
    }
  }

  @PostMapping("/stages/execute")
  public ResponseEntity<String> executeStage(@RequestBody StageExecution stageExecution) {
    if (stageExecution == null || stageExecution.getJobs().isEmpty()) {
      return ResponseEntity.badRequest().body("Error: Stage must contain at least one job.");
    }

    logger.info("Received stage execution request: {}", stageExecution.getStageName());

    for (JobExecution job : stageExecution.getJobs()) {
      jobExecutor.executeJob(job);  // Execute each job in the stage
    }

    return ResponseEntity.ok("Stage execution started successfully.");
  }

}
