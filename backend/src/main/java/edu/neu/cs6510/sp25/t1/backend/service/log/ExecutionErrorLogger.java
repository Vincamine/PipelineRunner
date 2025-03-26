package edu.neu.cs6510.sp25.t1.backend.service.log;

import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;

import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * Component responsible for logging execution errors at different levels of the pipeline.
 * This class logs errors to both the system logger and the execution log service.
 */
@Component
public class ExecutionErrorLogger {

    private final ExecutionLogService executionLogService;

    /**
     * Constructs an ExecutionErrorLogger with the required execution log service.
     *
     * @param executionLogService the service to persist execution logs
     */
    public ExecutionErrorLogger(ExecutionLogService executionLogService) {
        this.executionLogService = executionLogService;
    }

    /**
     * Logs an error at the pipeline execution level.
     *
     * @param pipelineExecutionId the ID of the pipeline execution
     * @param errorMessage the error message to log
     */
    public void logPipelineError(UUID pipelineExecutionId, String errorMessage) {
        PipelineLogger.error("Pipeline execution error [" + pipelineExecutionId + "]: " + errorMessage);
        executionLogService.logExecution("ERROR: " + errorMessage, pipelineExecutionId, null, null);
    }

    /**
     * Logs an error at the stage execution level.
     *
     * @param stageExecutionId the ID of the stage execution
     * @param pipelineExecutionId the ID of the parent pipeline execution
     * @param errorMessage the error message to log
     */
    public void logStageError(UUID stageExecutionId, UUID pipelineExecutionId, String errorMessage) {
        PipelineLogger.error("Stage execution error [" + stageExecutionId + "]: " + errorMessage);
        executionLogService.logExecution("ERROR: " + errorMessage, pipelineExecutionId, stageExecutionId, null);
    }

    /**
     * Logs an error at the job execution level.
     *
     * @param jobExecutionId the ID of the job execution
     * @param stageExecutionId the ID of the parent stage execution
     * @param errorMessage the error message to log
     */
    public void logJobError(UUID jobExecutionId, UUID stageExecutionId, String errorMessage) {
        PipelineLogger.error("Job execution error [" + jobExecutionId + "]: " + errorMessage);
        UUID pipelineExecutionId = findPipelineId(stageExecutionId);
        executionLogService.logExecution("ERROR: " + errorMessage, pipelineExecutionId, stageExecutionId, jobExecutionId);
    }

    /**
     * Helper method to find the pipeline execution ID from a stage execution ID.
     *
     * @param stageExecutionId the ID of the stage execution
     * @return the ID of the associated pipeline execution, or null if not found
     */
    private UUID findPipelineId(UUID stageExecutionId) {
        // This would require a database query, possibly requiring injection of StageExecutionRepository
        return null; // THIS WILL BE Changed
    }
}