package edu.neu.cs6510.sp25.t1.backend.api;

import edu.neu.cs6510.sp25.t1.common.api.request.JobStatusUpdate;
import edu.neu.cs6510.sp25.t1.common.api.request.PipelineExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.validation.error.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequestValidator {

    /**
     * Validates the pipeline execution request.
     *
     * @param request the pipeline execution request object
     * @throws ValidationException if validation fails
     */
    public void validatePipelineExecutionRequest(PipelineExecutionRequest request) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (request.getPipelineId() == null) {
            errors.add("Pipeline ID is required");
        }

        if (request.getFilePath() == null || request.getFilePath().isBlank()) {
            errors.add("Pipeline file path is required");
        }

        if (request.getCommitHash() == null || request.getCommitHash().isBlank()) {
            errors.add("Commit hash is required");
        }

        if (!errors.isEmpty()) {
            PipelineLogger.error("Pipeline execution request validation failed with " + errors.size() + " errors");
            throw new ValidationException(errors);
        }

        PipelineLogger.info("Pipeline execution request validated successfully");
    }

    /**
     * Validates the job status update request.
     *
     * @param request the job status update request object
     * @throws ValidationException if validation fails
     */
    public void validateJobStatusUpdateRequest(JobStatusUpdate request) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (request.getJobExecutionId() == null) {
            errors.add("Job execution ID is required");
        }

        if (request.getStatus() == null) {
            errors.add("Job status is required");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
