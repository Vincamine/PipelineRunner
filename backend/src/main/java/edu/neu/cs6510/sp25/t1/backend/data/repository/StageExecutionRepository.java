package edu.neu.cs6510.sp25.t1.backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import edu.neu.cs6510.sp25.t1.backend.data.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Repository for managing stage execution records.
 */
@Repository
public interface StageExecutionRepository extends JpaRepository<StageExecutionEntity, Long> {

  /**
   * Finds all stage executions for a given pipeline execution.
   *
   * @param runId The unique pipeline execution run ID.
   * @return List of StageExecution records.
   */
  List<StageExecutionEntity> findByPipelineExecutionRunId(String runId);

  /**
   * Finds all stage executions by status.
   *
   * @param status The execution status (e.g., SUCCESS, FAILED, CANCELED).
   * @return List of StageExecution records matching the status.
   */
  List<StageExecutionEntity> findByStatus(ExecutionStatus status);

  /**
   * Finds a specific stage execution by pipeline run ID and stage name.
   *
   * @param runId The unique pipeline execution run ID.
   * @param stageName The stage name.
   * @return Optional containing the execution if found.
   */
  Optional<StageExecutionEntity> findByPipelineExecutionRunIdAndStageName(String runId, String stageName);

  /**
   * Finds the latest execution of a stage within a pipeline run.
   *
   * @param runId The unique pipeline execution run ID.
   * @param stageName The stage name.
   * @return Optional containing the latest execution of the stage.
   */
  Optional<StageExecutionEntity> findTopByPipelineExecutionRunIdAndStageNameOrderByStartTimeDesc(String runId, String stageName);
}
