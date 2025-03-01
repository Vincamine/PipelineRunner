package edu.neu.cs6510.sp25.t1.backend.dto;

import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.model.Job;

/**
 * Data Transfer Object (DTO) for Job.
 * Used for API communication to prevent direct exposure of JPA entities.
 */
public class JobDTO {
  private String jobName;
  private String image;
  private List<String> script;
  private boolean allowFailure;

  /**
   * Constructs a new JobDTO.
   *
   * @param jobName      The name of the job.
   * @param image        The Docker image to use for the job.
   * @param script       The script to run in the job.
   * @param allowFailure Whether the pipeline should continue if this job fails.
   */
  public JobDTO(String jobName, String image, List<String> script, boolean allowFailure) {
    this.jobName = jobName;
    this.image = image;
    this.script = script;
    this.allowFailure = allowFailure;
  }

  /**
   * Convert Job entity to DTO
   *
   * @param job The Job entity to convert
   * @return The JobDTO
   */
  public static JobDTO fromEntity(Job job) {
    return new JobDTO(job.getName(), job.getImage(), job.getScript(), job.isAllowFailure());
  }

  /**
   * get the name of the job
   *
   * @return jobName
   */
  public String getJobName() {
    return jobName;
  }

  /**
   * get the image of the job
   *
   * @return image
   */
  public String getImage() {
    return image;
  }

  /**
   * get the script of the job
   *
   * @return script
   */
  public List<String> getScript() {
    return script;
  }

  /**
   * get the allowFailure of the job
   *
   * @return allowFailure
   */
  public boolean isAllowFailure() {
    return allowFailure;
  }

  /**
   * set the name of the job
   *
   * @param jobName The name of the job
   */
  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  /**
   * set the image of the job
   *
   * @param image The Docker image to use for the job
   */
  public void setImage(String image) {
    this.image = image;
  }

  /**
   * set the script of the job
   *
   * @param script The script to run in the job
   */
  public void setScript(List<String> script) {
    this.script = script;
  }

  /**
   * set the allowFailure of the job
   *
   * @param allowFailure Whether the pipeline should continue if this job fails
   */
  public void setAllowFailure(boolean allowFailure) {
    this.allowFailure = allowFailure;
  }
}
