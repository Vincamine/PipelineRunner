package edu.neu.cs6510.sp25.t1.common.api;

import java.util.List;
import java.util.Map;

/**
 * Represents a job execution request sent to the worker.
 * This request provides execution-specific details while referencing
 * the pre-defined job configuration.
 * sent from the backend to the worker.
 */
public class JobRequest {
  private final String jobId; // Unique execution ID
  private final String pipelineName; // Pipeline this job belongs to
  private final String jobName; // Reference to the job in pipeline config
  private final String commitHash; // Specific commit for execution
  private final Map<String, String> environmentVariables; // Runtime variables
  private final List<String> artifactPaths; // Paths to store collected artifacts
  private List<String> needs; // List of dependent job names

  /**
   * Constructor for JobRequest.
   *
   * @param jobId                Unique execution ID
   * @param pipelineName         Name of the pipeline
   * @param jobName              Name of the job in pipeline configuration
   * @param commitHash           Commit hash for execution
   * @param environmentVariables Environment variables for job execution
   * @param artifactPaths        Paths to artifacts produced by job execution
   */
  public JobRequest(String jobId, String pipelineName, String jobName,
                    String commitHash, Map<String, String> environmentVariables,
                    List<String> artifactPaths) {
    this.jobId = jobId;
    this.pipelineName = pipelineName;
    this.jobName = jobName;
    this.commitHash = commitHash;
    this.environmentVariables = environmentVariables != null ? environmentVariables : Map.of();
    this.artifactPaths = artifactPaths != null ? artifactPaths : List.of();
    this.needs = List.of();
  }

  /**
   * Get the job ID.
   *
   * @return the job ID
   */
  public String getJobId() {
    return jobId;
  }

  /**
   * Get the pipeline name.
   *
   * @return the pipeline name
   */
  public String getPipelineName() {
    return pipelineName;
  }

  /**
   * Get the job name.
   *
   * @return the job name
   */
  public String getJobName() {
    return jobName;
  }

  /**
   * Get the commit hash.
   *
   * @return the commit hash
   */
  public String getCommitHash() {
    return commitHash;
  }

  /**
   * Get the environment variables.
   *
   * @return the environment variables
   */
  public Map<String, String> getEnvironmentVariables() {
    return environmentVariables;
  }

  /**
   * Get the artifact paths.
   *
   * @return the artifact paths
   */
  public List<String> getArtifactPaths() {
    return artifactPaths;
  }

  /**
   * String representation of the JobRequest.
   *
   * @return String representation
   */
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

  /**
   * Get the dependencies of this job.
   *
   * @return List of job names that must complete before this job starts.
   */
  public List<String> getNeeds() {
    return needs;
  }

  /**
   * Add a dependency to this job.
   */
  public void addNeeds(String need) {
    this.needs.add(need);
  }
}
