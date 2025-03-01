package edu.neu.cs6510.sp25.t1.backend.repository;

import edu.neu.cs6510.sp25.t1.backend.model.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for Stage entity.
 * Provides database operations for stage-related queries.
 */
public interface StageRepository extends JpaRepository<Stage, Long> {
  List<Stage> findByPipelineName(String pipelineName);
}
