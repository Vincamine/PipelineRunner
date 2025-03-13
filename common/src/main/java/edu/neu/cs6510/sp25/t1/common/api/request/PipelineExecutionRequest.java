package edu.neu.cs6510.sp25.t1.common.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;

/**
 * Represents a request to execute a pipeline.
 */
@Getter
public class PipelineExecutionRequest {
  private final UUID pipelineId;
  private final String repo;
  private final String branch;
  private final String commitHash;
  private final boolean isLocal;
  private final int runNumber;
  private final String filePath;

  /**
   * Constructs a new PipelineExecutionRequest based on its definition.
   */
  @JsonCreator
  public PipelineExecutionRequest(
          @JsonProperty("pipelineId") UUID pipelineId,
          @JsonProperty("repo") String repo,
          @JsonProperty("branch") String branch,
          @JsonProperty("commitHash") String commitHash,
          @JsonProperty("isLocal") boolean isLocal,
          @JsonProperty("runNumber") int runNumber,
          @JsonProperty("filePath") String filePath) {
    this.pipelineId = pipelineId;
    this.repo = repo;
    this.branch = branch;
    this.commitHash = commitHash;
    this.isLocal = isLocal;
    this.runNumber = runNumber;
    this.filePath = filePath;
  }
}
