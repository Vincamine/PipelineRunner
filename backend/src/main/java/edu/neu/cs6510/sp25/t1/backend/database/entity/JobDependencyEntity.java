package edu.neu.cs6510.sp25.t1.backend.database.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "job_dependencies")
public class JobDependencyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "job_id", nullable = false)
  private JobExecutionEntity job;  // The job that has dependencies

  @ManyToOne
  @JoinColumn(name = "depends_on_job_id", nullable = false)
  private JobExecutionEntity dependency;  // The job that must run first
}
