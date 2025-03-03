package edu.neu.cs6510.sp25.t1.backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineExecutionEntity;

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
   * @param id The run ID (execution ID).
   * @return Optional containing the execution if found.
   */
  Optional<PipelineExecutionEntity> findByPipelineNameAndId(String pipelineName, Long id); // ✅ FIXED!

  /**
   * Finds the latest execution for a pipeline, ordered by createdAt timestamp.
   *
   * @param pipelineName The name of the pipeline.
   * @return Optional containing the latest execution.
   */
  Optional<PipelineExecutionEntity> findTopByPipelineNameOrderByCreatedAtDesc(String pipelineName); // ✅ FIXED!
}
