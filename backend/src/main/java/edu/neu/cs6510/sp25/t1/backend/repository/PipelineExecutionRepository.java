package edu.neu.cs6510.sp25.t1.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.runtime.PipelineRunState;

/**
 * Repository for managing pipeline execution records.
 * This is different from PipelineRepository, which handles pipeline definitions.
 */
@Repository
public interface PipelineExecutionRepository extends JpaRepository<PipelineExecutionEntity, UUID> {

  /**
   * Finds all executions for a specific pipeline name.
   *
   * @param pipelineName The name of the pipeline.
   * @return List of executions.
   */
  List<PipelineRunState> findByPipelineName(String pipelineName);

  /**
   * Finds the latest execution by pipeline name.
   *
   * @param pipelineName The name of the pipeline.
   * @return Optional pipeline execution.
   */
  Optional<PipelineRunState> findFirstByPipelineNameOrderByStartTimeDesc(String pipelineName);
}
