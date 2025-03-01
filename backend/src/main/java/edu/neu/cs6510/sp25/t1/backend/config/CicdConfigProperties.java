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

  /**
   * Get the Git configuration.
   *
   * @return GitConfig
   */
  public GitConfig getGit() {
    return git;
  }

  /**
   * Set the Git configuration.
   *
   * @param git GitConfig
   */
  public void setGit(GitConfig git) {
    this.git = git;
  }

  /**
   * Get the Artifacts configuration.
   *
   * @return ArtifactsConfig
   */
  public ArtifactsConfig getArtifacts() {
    return artifacts;
  }

  /**
   * Set the Artifacts configuration.
   *
   * @param artifacts ArtifactsConfig
   */
  public void setArtifacts(ArtifactsConfig artifacts) {
    this.artifacts = artifacts;
  }

  /**
   * Get the Worker configuration.
   *
   * @return WorkerConfig
   */
  public WorkerConfig getWorker() {
    return worker;
  }

  /**
   * Set the Worker configuration.
   *
   * @param worker WorkerConfig
   */
  public void setWorker(WorkerConfig worker) {
    this.worker = worker;
  }

  /**
   * Git Configuration
   */
  public static class GitConfig {
    private String repositoryRoot;

    /**
     * Get the repository root.
     *
     * @return String
     */
    public String getRepositoryRoot() {
      return repositoryRoot;
    }

    /**
     * Set the repository root.
     *
     * @param repositoryRoot String
     */
    public void setRepositoryRoot(String repositoryRoot) {
      this.repositoryRoot = repositoryRoot;
    }
  }

  /**
   * Artifacts Configuration
   */
  public static class ArtifactsConfig {
    private String storagePath;
    private int retentionDays;

    /**
     * Get the storage path.
     *
     * @return String
     */
    public String getStoragePath() {
      return storagePath;
    }

    /**
     * Set the storage path.
     *
     * @param storagePath String
     */
    public void setStoragePath(String storagePath) {
      this.storagePath = storagePath;
    }

    /**
     * Get the retention days.
     *
     * @return int
     */
    public int getRetentionDays() {
      return retentionDays;
    }

    /**
     * Set the retention days.
     *
     * @param retentionDays int
     */
    public void setRetentionDays(int retentionDays) {
      this.retentionDays = retentionDays;
    }
  }

  /**
   * Worker Configuration
   */
  public static class WorkerConfig {
    private int maxRetries;
    private int retryDelayMs;
    private int timeoutSeconds;

    /**
     * Get the maximum retries.
     *
     * @return int
     */
    public int getMaxRetries() {
      return maxRetries;
    }

    /**
     * Set the maximum retries.
     *
     * @param maxRetries int
     */
    public void setMaxRetries(int maxRetries) {
      this.maxRetries = maxRetries;
    }

    /**
     * Get the retry delay in milliseconds.
     *
     * @return int
     */
    public int getRetryDelayMs() {
      return retryDelayMs;
    }

    /**
     * Set the retry delay in milliseconds.
     *
     * @param retryDelayMs int
     */
    public void setRetryDelayMs(int retryDelayMs) {
      this.retryDelayMs = retryDelayMs;
    }

    /**
     * Get the timeout in seconds.
     *
     * @return int
     */
    public int getTimeoutSeconds() {
      return timeoutSeconds;
    }

    /**
     * Set the timeout in seconds.
     *
     * @param timeoutSeconds int
     */
    public void setTimeoutSeconds(int timeoutSeconds) {
      this.timeoutSeconds = timeoutSeconds;
    }
  }
}

