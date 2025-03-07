package edu.neu.cs6510.sp25.t1.backend.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.ExecutionLogEntity;

/**
 * Repository interface for ExecutionLogEntity.
 */
@Repository
public interface ExecutionLogRepository extends JpaRepository<ExecutionLogEntity, UUID> {

  /**
   * Find pipeline execution logs by pipeline execution id.
   *
   * @param pipelineExecutionId The pipeline execution id.
   * @return List of execution logs.
   */
  List<ExecutionLogEntity> findByPipelineExecutionId(UUID pipelineExecutionId);


  /**
   * Find stage execution logs by stage execution id.
   *
   * @param stageExecutionId The stage execution id.
   * @return List of execution logs.
   */
  List<ExecutionLogEntity> findByStageExecutionId(UUID stageExecutionId);

  /**
   * Find job execution logs by stage execution id
   *
   * @param jobExecutionId The job execution id
   * @return List of execution logs
   */
  List<ExecutionLogEntity> findByJobExecutionId(UUID jobExecutionId);
}
