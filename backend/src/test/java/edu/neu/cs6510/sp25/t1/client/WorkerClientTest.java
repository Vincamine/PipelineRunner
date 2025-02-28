package edu.neu.cs6510.sp25.t1.client;


import edu.neu.cs6510.sp25.t1.grpc.Worker.PipelineRequest;
import edu.neu.cs6510.sp25.t1.grpc.Worker.PipelineResponse;
import edu.neu.cs6510.sp25.t1.grpc.WorkerServiceGrpc;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WorkerClientTest {

    private WorkerClient workerClient;
    private WorkerServiceGrpc.WorkerServiceBlockingStub workerStub;

    @BeforeEach
    void setUp() {
        workerStub = mock(WorkerServiceGrpc.WorkerServiceBlockingStub.class);
        workerClient = new WorkerClient();
    }

    @Test
    void testSendJobToWorker_Success() {
        PipelineResponse mockResponse = PipelineResponse.newBuilder()
                .setStatus("Success")
                .setLogs("Job executed successfully")
                .build();

        when(workerStub.runPipeline(any(PipelineRequest.class))).thenReturn(mockResponse);

        assertDoesNotThrow(() -> workerClient.sendJobToWorker("test-repo", "test-pipeline", "config.yaml"));
    }

    @Test
    void testSendJobToWorker_Failure() {
        when(workerStub.runPipeline(any(PipelineRequest.class)))
                .thenThrow(new StatusRuntimeException(io.grpc.Status.UNAVAILABLE));

        assertThrows(RuntimeException.class, () ->
                workerClient.sendJobToWorker("test-repo", "test-pipeline", "config.yaml"));
    }
}
