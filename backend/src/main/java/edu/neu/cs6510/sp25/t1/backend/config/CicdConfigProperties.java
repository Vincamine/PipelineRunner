package edu.neu.cs6510.sp25.t1.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration class for loading CICD-related properties from application.yml.
 */
@Component
@ConfigurationProperties(prefix = "cicd")
public class CicdConfigProperties {
  private GitConfig git;
  private ArtifactsConfig artifacts;
  private WorkerConfig worker;

  // Getters and Setters
  public GitConfig getGit() { return git; }
  public void setGit(GitConfig git) { this.git = git; }

  public ArtifactsConfig getArtifacts() { return artifacts; }
  public void setArtifacts(ArtifactsConfig artifacts) { this.artifacts = artifacts; }

  public WorkerConfig getWorker() { return worker; }
  public void setWorker(WorkerConfig worker) { this.worker = worker; }

  // Git Configuration
  public static class GitConfig {
    private String repositoryRoot;
    public String getRepositoryRoot() { return repositoryRoot; }
    public void setRepositoryRoot(String repositoryRoot) { this.repositoryRoot = repositoryRoot; }
  }

  // Artifacts Configuration
  public static class ArtifactsConfig {
    private String storagePath;
    private int retentionDays;
    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    public int getRetentionDays() { return retentionDays; }
    public void setRetentionDays(int retentionDays) { this.retentionDays = retentionDays; }
  }

  // Worker Configuration
  public static class WorkerConfig {
    private int maxRetries;
    private int retryDelayMs;
    private int timeoutSeconds;
    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    public int getRetryDelayMs() { return retryDelayMs; }
    public void setRetryDelayMs(int retryDelayMs) { this.retryDelayMs = retryDelayMs; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
  }
}

