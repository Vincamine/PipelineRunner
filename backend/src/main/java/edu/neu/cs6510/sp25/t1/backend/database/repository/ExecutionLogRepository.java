package edu.neu.cs6510.sp25.t1.backend.database.repository;

import edu.neu.cs6510.sp25.t1.backend.database.entity.ExecutionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for ExecutionLogEntity.
 */
@Repository
public interface ExecutionLogRepository extends JpaRepository<ExecutionLogEntity, UUID> {

  List<ExecutionLogEntity> findByPipelineExecutionId(UUID pipelineExecutionId);

  List<ExecutionLogEntity> findByStageExecutionId(UUID stageExecutionId);

  List<ExecutionLogEntity> findByJobExecutionId(UUID jobExecutionId);
}
