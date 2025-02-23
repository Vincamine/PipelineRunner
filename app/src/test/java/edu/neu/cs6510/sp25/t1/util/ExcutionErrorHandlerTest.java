package edu.neu.cs6510.sp25.t1.util;

import edu.neu.cs6510.sp25.t1.model.ApiResponse;
import edu.neu.cs6510.sp25.t1.model.PipelineState;
import edu.neu.cs6510.sp25.t1.model.PipelineStatus;
import edu.neu.cs6510.sp25.t1.model.StageInfo;
import edu.neu.cs6510.sp25.t1.model.JobInfo;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ExecutionErrorHandlerTest {

    @Test
    void testHandlePipelineStatusFailed() {
        final ExecutionErrorHandler errorHandler = new ExecutionErrorHandler();

        // Creating a failed pipeline status with relevant information
        List<StageInfo> stages = List.of(new StageInfo("Build", "FAILED", Instant.now().minusSeconds(300).toEpochMilli(), Instant.now().toEpochMilli()));
        List<JobInfo> jobs = List.of(new JobInfo("Compile", "FAILED", false));

        final PipelineStatus status = new PipelineStatus("123", PipelineState.FAILED, 50, "Build failed due to missing dependencies", stages, jobs);

        assertFalse(errorHandler.handlePipelineStatus(status));
    }

    @Test
    void testHandleApiErrorNotFound() {
        final ExecutionErrorHandler errorHandler = new ExecutionErrorHandler();
        final ApiResponse response = new ApiResponse(404, "Resource not found");

        assertFalse(errorHandler.handleApiError(response));
    }

    @Test
    void testHandleApiErrorServerError() {
        final ExecutionErrorHandler errorHandler = new ExecutionErrorHandler();
        final ApiResponse response = new ApiResponse(500, "Internal server error");

        assertFalse(errorHandler.handleApiError(response));
    }
}
