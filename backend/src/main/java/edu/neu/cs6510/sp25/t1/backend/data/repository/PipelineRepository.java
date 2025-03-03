package edu.neu.cs6510.sp25.t1.backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineEntity;

/**
 * Repository for Pipeline entity.
 * Provides database operations for pipeline-related queries.
 */
@Repository
public interface PipelineRepository extends JpaRepository<PipelineEntity, String> {
  Optional<PipelineEntity> findByName(String name);

  /**
   * Finds all pipelines by repository URL.
   *
   * @param repositoryUrl The repository URL.
   * @return List of pipelines.
   */
  List<PipelineEntity> findByRepositoryUrl(String repositoryUrl);
}

