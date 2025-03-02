package edu.neu.cs6510.sp25.t1.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration class for loading CICD-related properties from application.yml.
 */
@Component
@ConfigurationProperties(prefix = "cicd")
public class CicdConfig {
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
}

