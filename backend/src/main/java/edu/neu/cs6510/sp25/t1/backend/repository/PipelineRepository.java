package edu.neu.cs6510.sp25.t1.backend.repository;

import edu.neu.cs6510.sp25.t1.backend.model.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Pipeline entity.
 * Provides database operations for pipeline-related queries.
 */
public interface PipelineRepository extends JpaRepository<Pipeline, String> {
    Pipeline findByName(String name);
}
