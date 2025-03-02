package edu.neu.cs6510.sp25.t1.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.entity.Stage;

/**
 * Repository for Stage entity.
 * Provides database operations for stage-related queries.
 */
@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {
  List<Stage> findByPipelineName(String pipelineName);
}
