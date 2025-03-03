package edu.neu.cs6510.sp25.t1.backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import edu.neu.cs6510.sp25.t1.backend.data.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

/**
 * Repository for managing job execution records.
 */
@Repository
public interface JobExecutionRepository extends JpaRepository<JobExecutionEntity, Long> {

  /**
   * Finds all job executions for a given pipeline execution (linked via runId).
   *
   * @param runId The unique run ID.
   * @return List of JobExecution records.
   */
  List<JobExecutionEntity> findByPipelineExecutionRunId(String runId);

  /**
   * Finds all job executions by status.
   *
   * @param status The execution status (e.g., SUCCESS, FAILED, CANCELED).
   * @return List of JobExecution records matching the status.
   */
  List<JobExecutionEntity> findByStatus(ExecutionStatus status);

  /**
   * Finds job execution by job name and run ID.
   *
   * @param jobName The job name.
   * @param runId The pipeline execution run ID.
   * @return Optional containing the execution if found.
   */
  Optional<JobExecutionEntity> findByJobJobNameAndPipelineExecutionRunId(String jobName, String runId);
}
