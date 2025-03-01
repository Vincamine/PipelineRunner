package edu.neu.cs6510.sp25.t1.backend.dto;

public class PipelineExecutionSummary {
  private String pipelineName;
  private String status;

  public PipelineExecutionSummary(String pipelineName, String status) {
    this.pipelineName = pipelineName;
    this.status = status;
  }

  public String getPipelineName() { return pipelineName; }
  public String getStatus() { return status; }
}
