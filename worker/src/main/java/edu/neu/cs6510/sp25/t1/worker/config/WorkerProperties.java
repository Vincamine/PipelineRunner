package edu.neu.cs6510.sp25.t1.worker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "worker")
public class WorkerProperties {
  private int maxConcurrentJobs;
  private String artifactStoragePath;

  public int getMaxConcurrentJobs() {
    return maxConcurrentJobs;
  }

  public void setMaxConcurrentJobs(int maxConcurrentJobs) {
    this.maxConcurrentJobs = maxConcurrentJobs;
  }

  public String getArtifactStoragePath() {
    return artifactStoragePath;
  }

  public void setArtifactStoragePath(String artifactStoragePath) {
    this.artifactStoragePath = artifactStoragePath;
  }
}
