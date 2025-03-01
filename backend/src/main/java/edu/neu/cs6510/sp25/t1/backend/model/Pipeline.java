package edu.neu.cs6510.sp25.t1.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "pipelines")
public class Pipeline {
    @Id
    @Column(nullable = false, unique = true)
    private String name; // Pipeline is uniquely identified by name

    @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Stage> stages;

    // Constructors
    public Pipeline() {}
    public Pipeline(String name) { this.name = name; }

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Stage> getStages() { return stages; }
    public void setStages(List<Stage> stages) { this.stages = stages; }
}