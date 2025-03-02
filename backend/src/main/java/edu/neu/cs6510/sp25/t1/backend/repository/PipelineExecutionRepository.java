package edu.neu.cs6510.sp25.t1.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import edu.neu.cs6510.sp25.t1.backend.entity.PipelineExecution;

@Repository
public interface PipelineExecutionRepository extends JpaRepository<PipelineExecution, Long> {

  /**
   * Finds all executions for a specific pipeline name.
   *
   * @param pipelineName The name of the pipeline.
   * @return List of executions.
   */
  List<PipelineExecution> findByPipelineName(String pipelineName);

  /**
   * Finds the latest execution by pipeline name.
   *
   * @param pipelineName The name of the pipeline.
   * @return Optional containing the latest execution.
   */
  Optional<PipelineExecution> findFirstByPipelineNameOrderByCreatedAtDesc(String pipelineName);

}
