package edu.neu.cs6510.sp25.t1.backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.data.entity.StageEntity;

/**
 * Repository for Stage entity.
 * Provides database operations for stage-related queries.
 */
@Repository
public interface StageRepository extends JpaRepository<StageEntity, Long> {
  List<StageEntity> findByPipelineName(String pipelineName);
}
