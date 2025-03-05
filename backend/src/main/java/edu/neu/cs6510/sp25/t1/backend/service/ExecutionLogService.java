package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.ExecutionLogEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.ExecutionLogRepository;

/**
 * Service class for handling execution logs.
 */
@Service
public class ExecutionLogService {

  private final ExecutionLogRepository executionLogRepository;

  @Autowired
  public ExecutionLogService(ExecutionLogRepository executionLogRepository) {
    this.executionLogRepository = executionLogRepository;
  }

  /**
   * Logs an execution message with optional references to pipeline, stage, or job executions.
   *
   * @param logText             The log message text
   * @param pipelineExecutionId Pipeline execution ID (nullable)
   * @param stageExecutionId    Stage execution ID (nullable)
   * @param jobExecutionId      Job execution ID (nullable)
   */
  public void logExecution(String logText, UUID pipelineExecutionId, UUID stageExecutionId, UUID jobExecutionId) {
    ExecutionLogEntity log = new ExecutionLogEntity(logText, pipelineExecutionId, stageExecutionId, jobExecutionId);
    executionLogRepository.save(log);
  }
}
