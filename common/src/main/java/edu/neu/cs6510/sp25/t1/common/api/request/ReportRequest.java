package edu.neu.cs6510.sp25.t1.common.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a request to report the status of a job execution.
 */
public class ReportRequest {
  private final String pipelineName;
  private final String stageName;
  private final String jobName;
  private final String runNumber;

  /**
   * Constructor for ReportRequest.
   *
   * @param pipelineName The pipeline name
   * @param stageName    The stage name
   * @param jobName      The job name
   * @param runNumber    The run number
   */
  @JsonCreator
  public ReportRequest(
          @JsonProperty("pipelineName") String pipelineName,
          @JsonProperty("stageName") String stageName,
          @JsonProperty("jobName") String jobName,
          @JsonProperty("runNumber") String runNumber) {
    this.pipelineName = pipelineName;
    this.stageName = stageName;
    this.jobName = jobName;
    this.runNumber = runNumber;
  }

  /**
   * Getter for pipelineName.
   *
   * @return the pipelineName
   */
  public String getPipelineName() {
    return pipelineName;
  }

  /**
   * Getter for stageName.
   *
   * @return the stageName
   */
  public String getStageName() {
    return stageName;
  }

  /**
   * Getter for jobName.
   *
   * @return the jobName
   */
  public String getJobName() {
    return jobName;
  }

  /**
   * Getter for runNumber.
   *
   * @return the runNumber
   */
  public String getRunNumber() {
    return runNumber;
  }
}
