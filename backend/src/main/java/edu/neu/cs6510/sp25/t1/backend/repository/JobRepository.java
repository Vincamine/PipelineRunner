package edu.neu.cs6510.sp25.t1.backend.repository;

import edu.neu.cs6510.sp25.t1.backend.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {}
