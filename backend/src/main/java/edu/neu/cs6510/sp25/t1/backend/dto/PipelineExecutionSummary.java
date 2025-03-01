package edu.neu.cs6510.sp25.t1.backend.dto;

/**
 * Data Transfer Object (DTO) for Pipeline Execution Summary.
 * Used for API communication to prevent direct exposure of JPA entities.
 */
public class PipelineExecutionSummary {
  private final String pipelineName;
  private final String status;

  /**
   * Constructs a new PipelineExecutionSummary.
   * @param pipelineName The name of the pipeline.
   * @param status The status of the pipeline execution
   */
  public PipelineExecutionSummary(String pipelineName, String status) {
    this.pipelineName = pipelineName;
    this.status = status;
  }

  /**
   * Retrieves the name of the pipeline.
   * @return The name of the pipeline.
   */
  public String getPipelineName() {
    return pipelineName;
  }

  /**
   * Retrieves the status of the pipeline execution.
   * @return The status of the pipeline execution.
   */
  public String getStatus() {
    return status;
  }
}
