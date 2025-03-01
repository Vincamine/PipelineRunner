package edu.neu.cs6510.sp25.t1.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.model.Job;

/**
 * Repository for Job entity.
 * Provides database operations for job-related queries.
 */
public interface JobRepository extends JpaRepository<Job, Long> {
  List<Job> findByStageId(Long stageId);
}
