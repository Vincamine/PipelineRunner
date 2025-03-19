package edu.neu.cs6510.sp25.t1.backend.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobEntity;

/**
 * Repository for managing jobs.
 */
@Repository
public interface JobRepository extends JpaRepository<JobEntity, UUID> {

  /**
   * Retrieves a job by its name.
   *
   * @param name The job name.
   * @return The job entity.
   */
  Optional<JobEntity> findByName(String name);

  /**
   * Retrieves all jobs in a stage.
   *
   * @param stageId The stage ID.
   * @return List of job entities.
   */
  List<JobEntity> findByStageId(UUID stageId);

}
