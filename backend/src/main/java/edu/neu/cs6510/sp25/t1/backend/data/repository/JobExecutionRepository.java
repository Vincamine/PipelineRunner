package edu.neu.cs6510.sp25.t1.backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import edu.neu.cs6510.sp25.t1.common.execution.JobExecution;


/**
 * Repository for managing job execution records.
 */
@Repository
public interface JobExecutionRepository extends JpaRepository<JobExecution, String> {

  /**
   * Finds all job executions for a given pipeline.
   *
   * @param pipelineName The name of the pipeline.
   * @return List of JobExecution records.
   */
  List<JobExecution> findByPipelineName(String pipelineName);

  /**
   * Finds all job executions by status.
   *
   * @param status The execution status (e.g., SUCCESS, FAILED, RUNNING).
   * @return List of JobExecution records matching the status.
   */
  List<JobExecution> findByStatus(String status);

  Optional<JobExecution> findByJobName(String jobName);
}
