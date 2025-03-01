package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;

import edu.neu.cs6510.sp25.t1.common.model.execution.PipelineExecution;

import java.util.*;

/**
 * Service for storing and retrieving pipeline execution history.
 *
 * This service maintains a record of past pipeline executions and allows querying:
 * - List of available pipelines
 * - Execution history of a specific pipeline
 * - Details of a specific pipeline run
 *
 * Future Enhancements:
 * - Store execution logs
 * - Support querying by stage and job
 * - Persist execution history in a database
 */
@Service
public class ReportService {

    private final Map<String, List<PipelineExecution>> pipelineExecutions;

    /**
     * Initializes an in-memory store for execution history.
     */
    public ReportService() {
        this.pipelineExecutions = new HashMap<>();
    }

    /**
     * Stores a pipeline execution.
     *
     * @param pipelineName The name of the pipeline.
     * @param execution The pipeline execution details.
     */
    public void savePipelineExecution(String pipelineName, PipelineExecution execution) {
        pipelineExecutions.computeIfAbsent(pipelineName, k -> new ArrayList<>()).add(execution);
    }

    /**
     * Retrieves the list of all pipeline names.
     *
     * @return List of pipeline names.
     */
    public List<String> getAllPipelines() {
        return new ArrayList<>(pipelineExecutions.keySet());
    }

    /**
     * Retrieves the execution history of a specific pipeline.
     *
     * @param pipelineName The name of the pipeline.
     * @return List of executions, or an empty list if none exist.
     */
    public List<PipelineExecution> getPipelineExecutions(String pipelineName) {
        return pipelineExecutions.getOrDefault(pipelineName, Collections.emptyList());
    }

    /**
     * Retrieves details of a specific pipeline run.
     *
     * @param pipelineName The pipeline name.
     * @param runId The unique identifier of the pipeline run.
     * @return The pipeline execution details, or null if not found.
     */
    public PipelineExecution getPipelineRun(String pipelineName, String runId) {
        return pipelineExecutions.getOrDefault(pipelineName, Collections.emptyList())
                .stream()
                .filter(exec -> exec.getPipelineId().equals(runId))
                .findFirst()
                .orElse(null);
    }
}
