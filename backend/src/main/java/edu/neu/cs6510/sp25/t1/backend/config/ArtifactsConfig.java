package edu.neu.cs6510.sp25.t1.backend.config;

/**
 * Artifacts Configuration
 */
public class ArtifactsConfig {
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