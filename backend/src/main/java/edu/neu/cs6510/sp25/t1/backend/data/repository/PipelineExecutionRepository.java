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
   * Finds the latest execution by pipeline name.
   *
   * @param pipelineName The name of the pipeline.
   * @return Optional containing the latest execution.
   */
  Optional<PipelineExecutionEntity> findFirstByPipelineNameOrderByCreatedAtDesc(String pipelineName);

}
