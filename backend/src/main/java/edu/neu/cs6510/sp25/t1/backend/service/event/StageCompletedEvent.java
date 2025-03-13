package edu.neu.cs6510.sp25.t1.backend.service.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

import lombok.Getter;

@Getter
public class StageCompletedEvent extends ApplicationEvent {
  private final UUID stageExecutionId;
  private final UUID pipelineExecutionId;

  public StageCompletedEvent(Object source, UUID stageExecutionId, UUID pipelineExecutionId) {
    super(source);
    this.stageExecutionId = stageExecutionId;
    this.pipelineExecutionId = pipelineExecutionId;
  }

}
//