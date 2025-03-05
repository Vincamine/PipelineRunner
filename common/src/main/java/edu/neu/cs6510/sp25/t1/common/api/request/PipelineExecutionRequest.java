package edu.neu.cs6510.sp25.t1.common.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a request to execute a pipeline.
 */
public class PipelineExecutionRequest {
  private final String repo;
  private final String branch;
  private final String commit;
  private final String pipeline;

  /**
   * Constructor for PipelineExecutionRequest.
   *
   * @param repo     The repository URL.
   * @param branch   The branch to execute.
   * @param commit   The commit hash to execute.s
   * @param pipeline The pipeline to execute.
   */
  @JsonCreator
  public PipelineExecutionRequest(
          @JsonProperty("repo") String repo,
          @JsonProperty("branch") String branch,
          @JsonProperty("commit") String commit,
          @JsonProperty("pipeline") String pipeline) {
    this.repo = repo;
    this.branch = branch;
    this.commit = commit;
    this.pipeline = pipeline;
  }


  /**
   * Getter for repo.
   *
   * @return the repo
   */
  public String getRepo() {
    return repo;
  }

  /**
   * Getter for branch.
   *
   * @return the branch
   */
  public String getBranch() {
    return branch;
  }

  /**
   * Getter for commit.
   *
   * @return the commit
   */
  public String getCommit() {
    return commit;
  }

  /**
   * Getter for pipeline.
   *
   * @return the pipeline
   */
  public String getPipeline() {
    return pipeline;
  }
}
