package edu.neu.cs6510.sp25.t1.backend.repository;

import edu.neu.cs6510.sp25.t1.backend.model.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineRepository extends JpaRepository<Pipeline, Long> {
    Pipeline findByName(String name);
}
