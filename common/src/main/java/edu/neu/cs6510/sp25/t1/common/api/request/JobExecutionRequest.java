package edu.neu.cs6510.sp25.t1.common.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Getter;

/**
 * Represents a request to execute a job.
 */
@Getter
public class JobExecutionRequest {
  // Getters with lombok
  private final String executionId;
  private final String stage;
  private final String job;
  private final String dockerImage;
  private final List<String> commands;

  /**
   * Constructs a new JobExecutionRequest based on its definition.
   *
   * @param executionId The unique identifier for this job execution
   * @param stage       The stage name
   * @param job         The job name
   * @param dockerImage The Docker image to use for the job
   * @param commands    The list of commands to execute
   */
  @JsonCreator
  public JobExecutionRequest(
          @JsonProperty("executionId") String executionId,
          @JsonProperty("stage") String stage,
          @JsonProperty("job") String job,
          @JsonProperty("dockerImage") String dockerImage,
          @JsonProperty("commands") List<String> commands) {
    this.executionId = executionId;
    this.stage = stage;
    this.job = job;
    this.dockerImage = dockerImage;
    this.commands = commands;
  }
}
