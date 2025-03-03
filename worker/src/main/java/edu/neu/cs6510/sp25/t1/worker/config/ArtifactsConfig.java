package edu.neu.cs6510.sp25.t1.worker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "artifacts")
public class ArtifactsConfig {
  private String storagePath;
  private int retentionDays;
  private String artifactStoragePath;
  private int maxConcurrentJobs;

  // Getters and Setters
  public String getStoragePath() {
    return storagePath;
  }

  public void setStoragePath(String storagePath) {
    this.storagePath = storagePath;
  }

  public int getRetentionDays() {
    return retentionDays;
  }

  public void setRetentionDays(int retentionDays) {
    this.retentionDays = retentionDays;
  }

  public String getArtifactStoragePath() {
    return artifactStoragePath;
  }

  public void setArtifactStoragePath(String artifactStoragePath) {
    this.artifactStoragePath = artifactStoragePath;
  }

  public int getMaxConcurrentJobs() {
    return maxConcurrentJobs;
  }

  public void setMaxConcurrentJobs(int maxConcurrentJobs) {
    this.maxConcurrentJobs = maxConcurrentJobs;
  }
}
