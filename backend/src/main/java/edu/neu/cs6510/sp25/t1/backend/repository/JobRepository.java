package edu.neu.cs6510.sp25.t1.backend.repository;

import edu.neu.cs6510.sp25.t1.backend.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for Job entity.
 * Provides database operations for job-related queries.
 */
public interface JobRepository extends JpaRepository<Job, Long> {
  List<Job> findByStageId(Long stageId);
}
