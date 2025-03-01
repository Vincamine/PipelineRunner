package edu.neu.cs6510.sp25.t1.backend.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String image;

    @ElementCollection
    @CollectionTable(name = "job_scripts", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "script")
    private List<String> script;

    @ManyToOne
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;

    @Column(nullable = false)
    private boolean allowFailure;

    @Column
    private Instant startTime;

    @Column
    private Instant completionTime;

    public Job() {}

    public Job(String name, String image, List<String> script, Stage stage, boolean allowFailure) {
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
    public Stage getStage() { return stage; }
    public boolean isAllowFailure() { return allowFailure; }
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public Instant getCompletionTime() { return completionTime; }
    public void setCompletionTime(Instant completionTime) { this.completionTime = completionTime; }
}
