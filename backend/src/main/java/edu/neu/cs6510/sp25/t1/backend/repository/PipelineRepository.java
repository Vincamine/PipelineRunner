package edu.neu.cs6510.sp25.t1.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.neu.cs6510.sp25.t1.backend.model.Pipeline;

/**
 * Repository for Pipeline entity.
 * Provides database operations for pipeline-related queries.
 */
public interface PipelineRepository extends JpaRepository<Pipeline, String> {
  Pipeline findByName(String name);
}
