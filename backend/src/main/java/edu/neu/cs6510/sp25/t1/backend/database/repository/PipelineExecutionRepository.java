package edu.neu.cs6510.sp25.t1.backend.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;

@Repository
public interface PipelineExecutionRepository extends JpaRepository<PipelineExecutionEntity, Long> {

  /**
   * Finds all executions for a specific pipeline name.
   *
   * @param pipelineName The name of the pipeline.
   * @return List of executions.
   */
  List<PipelineExecutionEntity> findByPipelineName(String pipelineName);

  /**
   * Finds a specific execution by pipeline name and run ID.
   *
   * @param pipelineName The name of the pipeline.
   * @param runId The unique run ID.
   * @return Optional containing the execution if found.
   */
  Optional<PipelineExecutionEntity> findByPipelineNameAndRunId(String pipelineName, String runId);

  /**
   * Finds the latest execution for a pipeline, ordered by startTime timestamp.
   *
   * @param pipelineName The name of the pipeline.
   * @return Optional containing the latest execution.
   */
  Optional<PipelineExecutionEntity> findTopByPipelineNameOrderByStartTimeDesc(String pipelineName);

  /**
   * Finds a specific execution by run ID.
   *
   * @param runId The unique run ID.
   * @return Optional containing the execution if found.
   */
  Optional<PipelineExecutionEntity> findByRunId(String runId);

  /**
   * Finds all executions for a specific repository URL.
   *
   * @param repositoryUrl The repository URL.
   * @return List of pipeline executions.
   */
  List<PipelineExecutionEntity> findByRepositoryUrl(String repositoryUrl);
}
