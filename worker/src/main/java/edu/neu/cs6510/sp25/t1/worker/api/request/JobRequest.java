package edu.neu.cs6510.sp25.t1.worker.api.request;

import java.util.List;
import java.util.Map;

/**
 * Data model representing job execution request.
 */
public class JobRequest {
  private final String jobId; // Unique execution ID
  private final String pipelineName;
  private final String jobName;
  private final String commitHash;
  private final Map<String, String> environmentVariables;
  private final List<String> artifactPaths;
  private final List<String> dependencies;
  private final boolean allowFailure;

  public JobRequest(String jobId, String pipelineName, String jobName,
                    String commitHash, Map<String, String> environmentVariables,
                    List<String> artifactPaths, List<String> dependencies, boolean allowFailure) {
    this.jobId = jobId;
    this.pipelineName = pipelineName;
    this.jobName = jobName;
    this.commitHash = commitHash;
    this.environmentVariables = environmentVariables != null ? environmentVariables : Map.of();
    this.artifactPaths = artifactPaths != null ? artifactPaths : List.of();
    this.dependencies = dependencies != null ? dependencies : List.of();
    this.allowFailure = allowFailure;
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

  public List<String> getDependencies() {
    return dependencies;
  }

  public boolean isAllowFailure() {
    return allowFailure;
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
            ", dependencies=" + dependencies +
            ", allowFailure=" + allowFailure +
            '}';
  }

  public List<String> getNeeds() {
    return dependencies;
  }

}