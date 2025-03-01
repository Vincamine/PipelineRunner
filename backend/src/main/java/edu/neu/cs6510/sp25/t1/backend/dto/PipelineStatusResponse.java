package edu.neu.cs6510.sp25.t1.backend.dto;

/**
 * Represents the status of a pipeline.
 */
public class PipelineStatusResponse {
  private final String name;
  private final String status;

  /**
   * Creates a new PipelineStatusResponse.
   *
   * @param name   The name of the pipeline.
   * @param status The status of the pipeline.
   */
  public PipelineStatusResponse(String name, String status) {
    this.name = name;
    this.status = status;
  }

  /**
   * Gets the name of the pipeline.
   *
   * @return The name of the pipeline.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the status of the pipeline.
   *
   * @return The status of the pipeline.
   */
  public String getStatus() {
    return status;
  }
}
