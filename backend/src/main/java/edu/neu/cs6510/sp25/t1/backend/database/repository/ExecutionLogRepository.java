package edu.neu.cs6510.sp25.t1.backend.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.model.ExecutionLog;

public interface ExecutionLogRepository extends JpaRepository<ExecutionLog, UUID> {
  List<ExecutionLog> findByPipelineExecutionId(UUID pipelineExecutionId);
  List<ExecutionLog> findByStageExecutionId(UUID stageExecutionId);
  List<ExecutionLog> findByJobExecutionId(UUID jobExecutionId);
}
