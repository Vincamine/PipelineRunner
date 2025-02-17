package edu.neu.cs6510.sp25.t1.util;

import edu.neu.cs6510.sp25.t1.model.ApiResponse;
import edu.neu.cs6510.sp25.t1.model.PipelineState;
import edu.neu.cs6510.sp25.t1.model.PipelineStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionErrorHandlerTest {

    @Test
    void testHandlePipelineStatusFailed() {
        ExecutionErrorHandler errorHandler = new ExecutionErrorHandler();
        PipelineStatus status = new PipelineStatus("123", PipelineState.FAILED, 50, "Build Stage");
        status.setMessage("Build failed due to missing dependencies");

        assertFalse(errorHandler.handlePipelineStatus(status));
    }

    @Test
    void testHandleApiErrorNotFound() {
        ExecutionErrorHandler errorHandler = new ExecutionErrorHandler();
        ApiResponse response = new ApiResponse(404, "Resource not found");

        assertFalse(errorHandler.handleApiError(response));
    }

    @Test
    void testHandleStageFailure() {
        ExecutionErrorHandler errorHandler = new ExecutionErrorHandler();
        PipelineStatus status = new PipelineStatus("123", PipelineState.FAILED, 40, "Deploy");
        status.setMessage("Deploy failed due to network issues");
        status.setLastUpdated(Instant.now());

        assertFalse(errorHandler.handleStageFailure(status));
    }
}
