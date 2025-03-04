package edu.neu.cs6510.sp25.t1.backend.database.entity;

import java.util.List;
import jakarta.persistence.*;

/**
 * Represents a job in a CI/CD pipeline.
 */
@Entity
@Table(name = "jobs")
public class JobEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String image;

  @ElementCollection  // ✅ Ensures `script` is stored as a separate table
  @CollectionTable(name = "job_scripts", joinColumns = @JoinColumn(name = "job_id"))
  @Column(name = "script", nullable = false)
  private List<String> script;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stage_id", nullable = false)
  private StageEntity stage;

  @Column(nullable = false)
  private boolean allowFailure;

  @ManyToMany
  @JoinTable(
          name = "job_dependencies",
          joinColumns = @JoinColumn(name = "job_id"),
          inverseJoinColumns = @JoinColumn(name = "depends_on_job_id")
  )
  private List<JobEntity> dependencies;

  public JobEntity() {}

  public JobEntity(String name, String image, List<String> script, StageEntity stage, boolean allowFailure) {
    this.name = name;
    this.image = image;
    this.script = script;
    this.stage = stage;
    this.allowFailure = allowFailure;
  }

  // Getters & Setters
  public Long getId() { return id; }
  public String getName() { return name; }
  public String getImage() { return image; }
  public List<String> getScript() { return script; }
  public StageEntity getStage() { return stage; }
  public boolean isAllowFailure() { return allowFailure; }
  public List<JobEntity> getDependencies() { return dependencies; }

  public void setScript(List<String> script) { this.script = script; }
  public void setDependencies(List<JobEntity> dependencies) { this.dependencies = dependencies; }

  public void setName(String name) { this.name = name; }
  public void setImage(String image) { this.image = image; }
  public void setAllowFailure(boolean allowFailure) { this.allowFailure = allowFailure; }
  public void setStage(StageEntity stage) { this.stage = stage; }
}
