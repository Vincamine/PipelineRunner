package edu.neu.cs6510.sp25.t1.backend.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Repository for managing PipelineExecution entities.
 */
@Repository
public interface PipelineExecutionRepository extends JpaRepository<PipelineExecutionEntity, UUID> {

  /**
   * Finds pipeline executions by pipeline ID.
   *
   * @param pipelineId the pipeline ID
   * @return a list of pipeline executions associated with the given pipeline
   */
  List<PipelineExecutionEntity> findByPipelineId(UUID pipelineId);

  /**
   * Finds pipeline executions by status.
   *
   * @param status the execution status
   * @return a list of pipeline executions with the specified status
   */
  List<PipelineExecutionEntity> findByStatus(ExecutionStatus status);

  /**
   * Finds a specific pipeline execution by pipeline ID and run number.
   *
   * @param pipelineId the pipeline ID
   * @param runNumber  the run number of the execution
   * @return an optional pipeline execution matching the criteria
   */
  Optional<PipelineExecutionEntity> findByPipelineIdAndRunNumber(UUID pipelineId, int runNumber);

  /**
   * Finds pipeline executions by pipeline name.
   *
   * @param pipelineName the pipeline name
   * @return a list of pipeline executions associated with the given pipeline name
   */
  List<PipelineExecutionEntity> findByPipelineNameOrderByStartTimeDesc(String pipelineName);
}
