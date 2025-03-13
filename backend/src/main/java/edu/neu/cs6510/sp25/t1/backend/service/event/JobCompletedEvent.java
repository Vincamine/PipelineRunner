package edu.neu.cs6510.sp25.t1.backend.service.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

import lombok.Getter;

/**
 * Event fired when a job completes.
 */
@Getter
public class JobCompletedEvent extends ApplicationEvent {
  private final UUID jobExecutionId;
  private final UUID stageExecutionId;

  /**
   * Constructor
   *
   * @param source           The source of the event
   * @param jobExecutionId   The job execution ID
   * @param stageExecutionId The stage execution ID
   */
  public JobCompletedEvent(Object source, UUID jobExecutionId, UUID stageExecutionId) {
    super(source);
    this.jobExecutionId = jobExecutionId;
    this.stageExecutionId = stageExecutionId;
  }

}
