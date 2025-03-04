package edu.neu.cs6510.sp25.t1.backend.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
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
  List<JobExecutionEntity> findByStageExecution_PipelineExecution_RunId(String runId);


  /**
   * Finds all job executions by status.
   *
   * @param status The execution status (e.g., SUCCESS, FAILED, CANCELED).
   * @return List of JobExecution records matching the status.
   */
  List<JobExecutionEntity> findByStatus(ExecutionStatus status);

  /**
   * Finds all job executions by stage execution ID.
   *
   * @param stageExecutionId The stage execution ID.
   * @return List of JobExecution records.
   */
  List<JobExecutionEntity> findByStageExecution_Id(Long stageExecutionId);

  /**
   * Finds job execution by job name and run ID.
   *
   * @param jobName The job name.
   * @param runId The pipeline execution run ID.
   * @return Optional containing the execution if found.
   */
  Optional<JobExecutionEntity> findByJob_NameAndStageExecution_PipelineExecution_RunId(String jobName, String runId);

  @Query("SELECT j FROM JobExecutionEntity j " +
          "WHERE j.stageExecution.pipelineExecution.runId = :runId " +
          "AND j.stageExecution.stageName = :stageName " +
          "AND j.job.name = :jobName")
  Optional<JobExecutionEntity> findByPipelineExecutionRunIdAndStageNameAndJobName(
          @Param("runId") String runId,
          @Param("stageName") String stageName,
          @Param("jobName") String jobName);
}
