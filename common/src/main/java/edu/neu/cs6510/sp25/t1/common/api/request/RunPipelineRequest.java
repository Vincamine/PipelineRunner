package edu.neu.cs6510.sp25.t1.common.api.request;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a request to run a pipeline.
 * This class is used for sending pipeline execution requests to the backend.
 */
public class RunPipelineRequest {
  private final String repo; // Repository URL.
  private final String branch; // Git branch name to execute.
  private final String commit; // Commit SHA to execute.
  private final String pipeline; // Pipeline name to execute.
  private final boolean local; // If true, runs locally.
  private final Map<String, String> overrides; // Custom configuration overrides.
  private final String configPath; // Optional path to pipeline configuration file.

  /**
   * Default constructor.
   * Initializes default values to prevent null references.
   */
  public RunPipelineRequest() {
    this.repo = "";
    this.branch = "";
    this.commit = "";
    this.pipeline = "";
    this.local = false;
    this.overrides = Map.of(); // Prevents NullPointerException
    this.configPath = "";
  }

  /**
   * Constructor with only pipeline name (for basic runs).
   *
   * @param pipeline Pipeline name to execute.
   * @throws IllegalArgumentException if pipeline name is null or empty.
   */
  public RunPipelineRequest(String pipeline) {
    if (pipeline == null || pipeline.isBlank()) {
      throw new IllegalArgumentException("Pipeline name cannot be null or empty.");
    }
    this.repo = "";
    this.branch = "";
    this.commit = "";
    this.pipeline = pipeline;
    this.local = false;
    this.overrides = Map.of();
    this.configPath = "";
  }

  /**
   * Full constructor for advanced pipeline execution.
   *
   * @param repo       Repository URL.
   * @param branch     Git branch name.
   * @param commit     Commit SHA.
   * @param pipeline   Pipeline name.
   * @param local      Whether to run locally.
   * @param overrides  Custom configuration overrides.
   * @param configPath Optional path to pipeline configuration file.
   * @throws IllegalArgumentException if pipeline name is null or empty.
   */
  public RunPipelineRequest(String repo, String branch, String commit, String pipeline, boolean local, Map<String, String> overrides, String configPath) {
    if (pipeline == null || pipeline.isBlank()) {
      throw new IllegalArgumentException("Pipeline name cannot be null or empty.");
    }
    this.repo = Objects.requireNonNullElse(repo, "");
    this.branch = Objects.requireNonNullElse(branch, "");
    this.commit = Objects.requireNonNullElse(commit, "");
    this.pipeline = pipeline;
    this.local = local;
    this.overrides = Objects.requireNonNullElse(overrides, Map.of());
    this.configPath = Objects.requireNonNullElse(configPath, "");
  }


  /**
   * Get the repository URL.
   *
   * @return Repository URL.
   */
  public String getRepo() {
    return repo;
  }

  /**
   * Get the branch name.
   *
   * @return Branch name.
   */
  public String getBranch() {
    return branch;
  }

  /**
   * Get the commit SHA.
   *
   * @return Commit SHA.
   */
  public String getCommit() {
    return commit;
  }

  /**
   * Get the pipeline name.
   *
   * @return Pipeline name.
   */
  public String getPipeline() {
    return pipeline;
  }

  /**
   * Check if the pipeline should run locally.
   *
   * @return True if the pipeline should run locally, false otherwise.
   */
  public boolean isLocal() {
    return local;
  }

  /**
   * Get the custom configuration overrides.
   *
   * @return Custom configuration overrides.
   */
  public Map<String, String> getOverrides() {
    return overrides;
  }

  /**
   * Get the path to the pipeline configuration file.
   *
   * @return Path to the pipeline configuration file.
   */
  public String getConfigPath() {
    return configPath;
  }

  /**
   * String representation of the RunPipelineRequest.
   *
   * @return String representation.
   */
  @Override
  public String toString() {
    return "RunPipelineRequest{" +
            "repo='" + repo + '\'' +
            ", branch='" + branch + '\'' +
            ", commit='" + commit + '\'' +
            ", pipeline='" + pipeline + '\'' +
            ", local=" + local +
            ", overrides=" + overrides +
            ", configPath='" + configPath + '\'' +
            '}';
  }
}
