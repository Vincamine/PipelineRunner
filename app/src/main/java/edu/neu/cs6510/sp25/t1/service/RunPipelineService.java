package edu.neu.cs6510.sp25.t1.service;

import edu.neu.cs6510.sp25.t1.util.ErrorHandler;
import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;
import edu.neu.cs6510.sp25.t1.model.PipelineStatus;
import edu.neu.cs6510.sp25.t1.model.PipelineState;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Service to execute a CI/CD pipeline while respecting dependencies and failure
 * policies.
 * <p>
 * The pipeline runs in the order defined in the YAML file:
 * <ul>
 * <li>Stages execute sequentially.</li>
 * <li>Jobs within each stage execute concurrently.</li>
 * <li>If a job (without allow_failure) fails, the pipeline halts
 * immediately.</li>
 * </ul>
 */
public class RunPipelineService {
    private final YamlPipelineValidator validator;

    /**
     * Constructs a pipeline execution service.
     *
     * @param validator YAML validator to check pipeline configuration.
     */
    public RunPipelineService(YamlPipelineValidator validator) {
        this.validator = validator;
    }

    /**
     * Executes the pipeline in order while respecting dependencies and failure
     * policies.
     *
     * @param pipelineConfig The parsed YAML pipeline configuration.
     * @return The final pipeline execution status.
     */
    public PipelineStatus executePipeline(Map<String, Object> pipelineConfig) {
        try {
            // Validate pipeline structure
            if (!validator.validatePipeline(pipelineConfig)) {
                System.err.println("Pipeline validation failed.");
                return new PipelineStatus("unknown", PipelineState.FAILED, 0, "Validation error.");
            }

            // Extract Stages, Jobs, and Dependencies safely
            final List<String> stages = extractStages(pipelineConfig);
            final Map<String, List<String>> jobs = extractJobs(pipelineConfig);
            final Map<String, List<String>> dependencies = extractDependencies(pipelineConfig);

            @SuppressWarnings("unused")
            final PipelineStatus status = new PipelineStatus("pipeline", PipelineState.RUNNING, 0,
                    "Executing pipeline.");

            // Execute pipeline stage by stage
            for (String stage : stages) {
                System.out.println("🚀 Executing stage: " + stage);
                final boolean stageFailed = executeStage(stage, jobs.getOrDefault(stage, Collections.emptyList()),
                        dependencies);

                if (stageFailed) {
                    System.err.println("Pipeline failed at stage: " + stage);
                    return new PipelineStatus("pipeline", PipelineState.FAILED, 1, "Pipeline execution failed.");
                }
            }

            System.out.println("✅ Pipeline execution complete.");
            return new PipelineStatus("pipeline", PipelineState.SUCCESS, 0, "Pipeline executed successfully.");
        } catch (Exception e) {
            ErrorHandler.reportError("Pipeline execution error: " + e.getMessage());
            return new PipelineStatus("pipeline", PipelineState.FAILED, 1, "Execution error.");
        }
    }

    /**
     * Extracts stages from the pipeline configuration safely.
     *
     * @param pipelineConfig The parsed YAML pipeline configuration.
     * @return A list of stage names.
     */
    private List<String> extractStages(Map<String, Object> pipelineConfig) {
        final Object stagesObj = pipelineConfig.get("stages");
        if (stagesObj instanceof List<?>) {
            return ((List<?>) stagesObj).stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }
        throw new IllegalArgumentException("Invalid or missing 'stages' in pipeline configuration.");
    }

    /**
     * Extracts jobs for each stage safely.
     *
     * @param pipelineConfig The parsed YAML pipeline configuration.
     * @return A map where keys are stage names, and values are lists of job names.
     */
    private Map<String, List<String>> extractJobs(Map<String, Object> pipelineConfig) {
        final Map<String, List<String>> jobs = new HashMap<>();
        final Object jobsObj = pipelineConfig.get("jobs");
        if (jobsObj instanceof Map<?, ?> jobMap) {
            for (Map.Entry<?, ?> entry : jobMap.entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof List<?>) {
                    final List<String> jobList = ((List<?>) entry.getValue()).stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .toList();
                    jobs.put((String) entry.getKey(), jobList);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid or missing 'jobs' in pipeline configuration.");
        }
        return jobs;
    }

    /**
     * Extracts job dependencies safely.
     *
     * @param pipelineConfig The parsed YAML pipeline configuration.
     * @return A map where keys are job names, and values are lists of dependent job
     *         names.
     */
    private Map<String, List<String>> extractDependencies(Map<String, Object> pipelineConfig) {
        final Map<String, List<String>> dependencies = new HashMap<>();
        final Object dependenciesObj = pipelineConfig.get("dependencies");
        if (dependenciesObj instanceof Map<?, ?> dependencyMap) {
            for (Map.Entry<?, ?> entry : dependencyMap.entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof List<?>) {
                    final List<String> depList = ((List<?>) entry.getValue()).stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .toList();
                    dependencies.put((String) entry.getKey(), depList);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid or missing 'dependencies' in pipeline configuration.");
        }
        return dependencies;
    }

    /**
     * Executes a stage by running its jobs and respecting dependencies.
     *
     * @param stage        The stage name.
     * @param jobs         The list of job names in this stage.
     * @param dependencies The job dependency mapping.
     * @return {@code true} if any job fails; otherwise, {@code false}.
     */
    private boolean executeStage(String stage, List<String> jobs, Map<String, List<String>> dependencies) {
        final Set<String> completedJobs = new HashSet<>();
        final Queue<String> jobQueue = new LinkedList<>(jobs);

        while (!jobQueue.isEmpty()) {
            final String job = jobQueue.poll();

            // Ensure dependencies are met before running job
            if (dependencies.containsKey(job)) {
                final boolean unmetDependency = dependencies.get(job).stream()
                        .anyMatch(dep -> !completedJobs.contains(dep));
                if (unmetDependency) {
                    jobQueue.offer(job); // Requeue job for later execution
                    continue;
                }
            }

            // Simulate job execution (Replace with actual execution logic)
            final boolean jobFailed = simulateJobExecution(job);

            if (jobFailed) {
                return true; // If a job fails, return failure immediately
            }

            completedJobs.add(job);
        }

        return false; // Stage completed successfully
    }

    /**
     * Simulates job execution. In a real system, this would run the job logic.
     *
     * @param job The job name.
     * @return {@code true} if the job fails, {@code false} if successful.
     */
    private boolean simulateJobExecution(String job) {
        System.out.println("🔄 Running job: " + job);
        return false; // Simulating success (Modify based on actual execution)
    }
}
