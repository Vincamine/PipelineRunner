package edu.neu.cs6510.sp25.t1.common.api;

import java.util.List;
import java.util.Map;

/**
 * Represents a job request to be processed by the worker.
 * Used for submitting a job execution request.
 */
public class JobRequest {
    private String jobId; // unique identifier for the job.
    private String repositoryUrl; // URL of the repository to be processed.
    private String commitHash; // commit hash of the repository.
    private String dockerImage; // Docker image to be used for the job.
    private List<String> commands; // list of commands to be executed.
    private Map<String, String> environmentVariables; // environment variables for the job.
    private List<String> artifactPaths; // paths to the artifacts to be collected.
    private boolean allowFailure; // flag to allow failure of the job.

    /**
     * Constructor for JobRequest.
     * 
     * @param jobId
     * @param repositoryUrl
     * @param commitHash
     * @param dockerImage
     * @param commands
     * @param environmentVariables
     * @param artifactPaths
     * @param allowFailure
     */
    public JobRequest(String jobId, String repositoryUrl, String commitHash, String dockerImage,
            List<String> commands, Map<String, String> environmentVariables,
            List<String> artifactPaths, boolean allowFailure) {
        this.jobId = jobId;
        this.repositoryUrl = repositoryUrl;
        this.commitHash = commitHash;
        this.dockerImage = dockerImage;
        this.commands = commands;
        this.environmentVariables = environmentVariables;
        this.artifactPaths = artifactPaths;
        this.allowFailure = allowFailure;
    }

    /**
     * Getters for jobId.
     * 
     * @return jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Getters for repositoryUrl.
     * 
     * @return repositoryUrl
     */
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    /**
     * Getters for commitHash.
     * 
     * @return commitHash
     */
    public String getCommitHash() {
        return commitHash;
    }

    /**
     * Getters for dockerImage.
     * 
     * @return dockerImage
     */
    public String getDockerImage() {
        return dockerImage;
    }

    /**
     * Getters for commands.
     * 
     * @return commands
     */
    public List<String> getCommands() {
        return commands;
    }

    /**
     * Getters for environmentVariables.
     * 
     * @return environmentVariables
     */
    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    /**
     * Getters for artifactPaths.
     * 
     * @return artifactPaths
     */
    public List<String> getArtifactPaths() {
        return artifactPaths;
    }

    /**
     * Getters for allowFailure.
     * 
     * @return allowFailure
     */
    public boolean isAllowFailure() {
        return allowFailure;
    }

    /**
     * String representation of the JobRequest object.
     * 
     * @return String representation of the JobRequest object
     * 
     */
    @Override
    public String toString() {
        return "JobRequest{" +
                "jobId='" + jobId + '\'' +
                ", repositoryUrl='" + repositoryUrl + '\'' +
                ", commitHash='" + commitHash + '\'' +
                ", dockerImage='" + dockerImage + '\'' +
                ", commands=" + commands +
                ", environmentVariables=" + environmentVariables +
                ", artifactPaths=" + artifactPaths +
                ", allowFailure=" + allowFailure +
                '}';
    }
}
