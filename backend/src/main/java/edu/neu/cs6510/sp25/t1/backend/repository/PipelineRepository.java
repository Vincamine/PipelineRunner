package edu.neu.cs6510.sp25.t1.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.neu.cs6510.sp25.t1.backend.model.Pipeline;

/**
 * Repository for Pipeline entity.
 * Provides database operations for pipeline-related queries.
 */
@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, String> {
  Pipeline findByName(String name);
}
