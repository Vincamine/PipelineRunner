package edu.neu.cs6510.sp25.t1.api;

import java.util.Map;

/**
 * Represents a request to run a pipeline.
 */
public class RunPipelineRequest {
    private String repo;
    private String branch;
    private String commit;
    private String pipeline;
    private boolean local;
    private Map<String, String> overrides;
    private String configPath;


    /**
     * Default constructor.
     */
    public RunPipelineRequest() {
    }

    /**
     * Constructor with parameters.
     * @param repo
     * @param branch
     * @param commit
     * @param pipeline
     * @param local
     */
    public RunPipelineRequest(String repo, String branch, String commit, String pipeline, boolean local) {
        this.repo = repo;
        this.branch = branch;
        this.commit = commit;
        this.pipeline = pipeline;
        this.local = local;
    }

    /**
     * Get repo.
     * @return repo
     */
    public String getRepo() {
        return repo;
    }

    /**
     * Set repo.
     * @param repo
     */
    public void setRepo(String repo) {
        this.repo = repo;
    }

    /**
     * Get branch.
     * @return branch
     */
    public String getBranch() {
        return branch;
    }

    /**
     * Set branch.
     * @param branch
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * Get commit.
     * @return commit
     */
    public String getCommit() {
        return commit;
    }

    /**
     * Set commit.
     * @param commit
     */
    public void setCommit(String commit) {
        this.commit = commit;
    }

    /**
     * Get pipeline.
     * @return pipeline
     */
    public String getPipeline() {
        return pipeline;
    }

    /**
     * Set pipeline.
     * @param pipeline
     */
    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    /**
     * Check if the pipeline is local.
     * @return true if local, false otherwise
     */
    public boolean isLocal() {
        return local;
    }

    /**
     * Set if the pipeline is local.
     * @param local
     */
    public void setLocal(boolean local) {
        this.local = local;
    }

    /**
     * Get overrides.
     * @return overrides
     */
    public Map<String, String> getOverrides() {
        return overrides;
    }

    /**
     * Set overrides.
     * @param overrides
     */
    public void setOverrides(Map<String, String> overrides) {
        this.overrides = overrides;
    }

    /**
     * Get config path.
     * @return configPath
     */
    public String getConfigPath() {
        return configPath;
    }
    
    /**
     * Set config path.
     * @param configPath
     */
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
    
}
