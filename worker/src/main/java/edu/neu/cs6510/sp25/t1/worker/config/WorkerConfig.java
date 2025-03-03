package edu.neu.cs6510.sp25.t1.worker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "worker")
public class WorkerConfig {
  private int maxRetries;
  private int retryDelayMs;
  private int timeoutSeconds;
  private int maxConcurrentJobs;
  private String artifactStoragePath;

  // Getters and Setters
  public int getMaxRetries() {
    return maxRetries;
  }

  public void setMaxRetries(int maxRetries) {
    this.maxRetries = maxRetries;
  }

  public int getRetryDelayMs() {
    return retryDelayMs;
  }

  public void setRetryDelayMs(int retryDelayMs) {
    this.retryDelayMs = retryDelayMs;
  }

  public int getTimeoutSeconds() {
    return timeoutSeconds;
  }

  public void setTimeoutSeconds(int timeoutSeconds) {
    this.timeoutSeconds = timeoutSeconds;
  }

  // Getter and Setter for maxConcurrentJobs
  public int getMaxConcurrentJobs() {
    return maxConcurrentJobs;
  }
  public void setMaxConcurrentJobs(int maxConcurrentJobs) {
    this.maxConcurrentJobs = maxConcurrentJobs;
  }

  // Getter and Setter for artifactStoragePath
  public String getArtifactStoragePath() {
    return artifactStoragePath;
  }
  public void setArtifactStoragePath(String artifactStoragePath) {
    this.artifactStoragePath = artifactStoragePath;
  }
}
