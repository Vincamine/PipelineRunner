package edu.neu.cs6510.sp25.t1.backend.config;

/**
 * Git Configuration
 */
public class GitConfig {
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