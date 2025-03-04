package edu.neu.cs6510.sp25.t1.backend.api.request;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a request to run a pipeline.
 * This class is used for sending pipeline execution requests to the backend.
 */
public class RunPipelineRequest {
  private final String repo;
  private final String branch;
  private final String commit;
  private final String pipeline;
  private final boolean local;
  private final Map<String, String> overrides;
  private final String configPath;

  public RunPipelineRequest() {
    this.repo = "";
    this.branch = "";
    this.commit = "";
    this.pipeline = "";
    this.local = false;
    this.overrides = Map.of();
    this.configPath = "";
  }

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

  public String getRepo() {
    return repo;
  }

  public String getBranch() {
    return branch;
  }

  public String getCommit() {
    return commit;
  }

  public String getPipeline() {
    return pipeline;
  }

  public boolean isLocal() {
    return local;
  }

  public Map<String, String> getOverrides() {
    return overrides;
  }

  public String getConfigPath() {
    return configPath;
  }

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