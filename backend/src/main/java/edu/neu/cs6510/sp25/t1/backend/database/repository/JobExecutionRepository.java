package edu.neu.cs6510.sp25.t1.backend.database.repository;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing JobExecution entities.
 */
@Repository
public interface JobExecutionRepository extends JpaRepository<JobExecutionEntity, UUID> {

  /**
   * Finds job executions by stage execution ID.
   *
   * @param stageExecutionId the ID of the stage execution
   * @return a list of job executions in the given stage
   */
  List<JobExecutionEntity> findByStageExecutionId(UUID stageExecutionId);

  /**
   * Finds job executions by job ID.
   *
   * @param jobId the ID of the job
   * @return a list of executions of the specified job
   */
  List<JobExecutionEntity> findByJobId(UUID jobId);

  /**
   * Finds job executions by status.
   *
   * @param status the execution status
   * @return a list of job executions with the specified status
   */
  List<JobExecutionEntity> findByStatus(ExecutionStatus status);

  /**
   * Finds job execution by commit hash.
   *
   * @param commitHash the commit hash used for execution
   * @return an optional job execution associated with the given commit
   */
  Optional<JobExecutionEntity> findByCommitHash(String commitHash);

  /**
   * Dynamically fetches the job name associated with a job execution.
   * and return the entity
   * @param stageExecutionId the ID of the stage execution
   * @return a list of job executions with the job name
   */
  @Query("SELECT je FROM JobExecutionEntity je JOIN JobEntity j ON je.jobId = j.id WHERE je.stageExecutionId = :stageExecutionId ORDER BY je.startTime DESC")
  List<JobExecutionEntity> findByStageExecutionIdAndFetchJobName(@Param("stageExecutionId") UUID stageExecutionId);

  /**
   * Join jobExecution and job tables to fetch the job name by stageExecutionId
   * @param stageExecutionId the stage execution ID
   * @return a list of job names
   */
  @Query("SELECT j.name FROM JobExecutionEntity je JOIN JobEntity j ON je.jobId = j.id WHERE je.stageExecutionId = :stageExecutionId")
  List<String> findJobNamesByStageExecutionId(@Param("stageExecutionId") UUID stageExecutionId);

  /**
   * Join jobExecution and job tables to fetch the job name by jobId
   * @param jobId the job ID
   * @return an optional job name
   */
  @Query("SELECT j.name FROM JobExecutionEntity je JOIN JobEntity j ON je.jobId = j.id WHERE je.jobId = :jobId")
  Optional<String> findJobNameByJobId(@Param("jobId") UUID jobId);

  /**
   * Join jobExecution and job tables to fetch the job name by jobName
   * @param stageExecutionId the stage execution ID
   * @param jobName the name of the job
   * @return a list of job executions with the job name
   */
  @Query("SELECT je FROM JobExecutionEntity je JOIN JobEntity j ON je.jobId = j.id WHERE je.stageExecutionId = :stageExecutionId AND j.name = :jobName ORDER BY je.startTime DESC")
  List<JobExecutionEntity> findByStageExecutionIdAndJobNameOrderByStartTimeDesc(@Param("stageExecutionId") UUID stageExecutionId, @Param("jobName") String jobName);

  /**
   * Find job dependencies by job ID.
   * @param jobId the job ID
   * @return a list of job dependencies
   */
  @Query("SELECT jd.dependency.id FROM JobDependencyEntity jd WHERE jd.job.id = :jobId")
  List<UUID> findDependenciesByJobId(@Param("jobId") UUID jobId);

}
