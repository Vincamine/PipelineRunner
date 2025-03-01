package edu.neu.cs6510.sp25.t1.common.api;

import java.util.Map;

/**
 * Represents a request to run a pipeline.
 * This class is used for sending pipeline execution requests to the backend.
 */
public class RunPipelineRequest {
    private String repo; // Repository URL.
    private String branch; // Git branch name to execute.
    private String commit; // Commit SHA to execute.
    private String pipeline; // Pipeline name to execute.
    private boolean local; //  If true, runs locally.
    private Map<String, String> overrides; // Custom configuration overrides.
    private String configPath;

    /**
     * Default constructor.
     */
    public RunPipelineRequest() {
        this.overrides = Map.of(); // Initialize to prevent null pointer issues
    }

    /**
     * Constructor with only pipelineId (for basic runs).
     *
     * @param pipeline Pipeline ID to execute.
     */
    public RunPipelineRequest(String pipeline) {
        this.pipeline = pipeline;
        this.overrides = Map.of();
    }

    /**
     * Full constructor for advanced pipeline execution.
     *
     * @param repo      Repository URL.
     * @param branch    Git branch name.
     * @param commit    Commit SHA.
     * @param pipeline  Pipeline name.
     * @param local     Whether to run locally.
     */
    public RunPipelineRequest(String repo, String branch, String commit, String pipeline, boolean local) {
        this.repo = repo;
        this.branch = branch;
        this.commit = commit;
        this.pipeline = pipeline;
        this.local = local;
        this.overrides = Map.of();
    }

    // ✅ Getters and Setters

    public String getRepo() { return repo; }
    public void setRepo(String repo) { this.repo = repo; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getCommit() { return commit; }
    public void setCommit(String commit) { this.commit = commit; }

    public String getPipeline() { return pipeline; }
    public void setPipeline(String pipeline) { this.pipeline = pipeline; }

    public boolean isLocal() { return local; }
    public void setLocal(boolean local) { this.local = local; }

    public Map<String, String> getOverrides() { return overrides; }
    public void setOverrides(Map<String, String> overrides) { this.overrides = overrides; }

    public String getConfigPath() { return configPath; }
    public void setConfigPath(String configPath) { this.configPath = configPath; }
}
