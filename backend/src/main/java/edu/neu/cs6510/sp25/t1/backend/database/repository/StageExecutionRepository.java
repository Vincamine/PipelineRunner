package edu.neu.cs6510.sp25.t1.backend.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Repository for managing StageExecution entities.
 */
@Repository
public interface StageExecutionRepository extends JpaRepository<StageExecutionEntity, UUID> {

  /**
   * Finds stage executions by pipeline execution ID.
   *
   * @param pipelineExecutionId the pipeline execution ID
   * @return a list of stage executions associated with the given pipeline execution
   */
  List<StageExecutionEntity> findByPipelineExecutionId(UUID pipelineExecutionId);

  /**
   * Finds stage executions by status.
   *
   * @param status the execution status
   * @return a list of stage executions with the specified status
   */
  List<StageExecutionEntity> findByStatus(ExecutionStatus status);

  /**
   * Finds a specific stage execution by stage ID and pipeline execution ID.
   *
   * @param stageId             the stage ID
   * @param pipelineExecutionId the pipeline execution ID
   * @return an optional stage execution matching the criteria
   */
  Optional<StageExecutionEntity> findByStageIdAndPipelineExecutionId(UUID stageId, UUID pipelineExecutionId);
}
