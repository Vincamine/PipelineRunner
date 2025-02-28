package edu.neu.cs6510.sp25.t1.service;

import edu.neu.cs6510.sp25.t1.client.WorkerClient;
import edu.neu.cs6510.sp25.t1.model.PipelineStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RunPipelineServiceTest {

    private RunPipelineService runPipelineService;
    private WorkerClient workerClient;

    @BeforeEach
    void setUp() {
        workerClient = mock(WorkerClient.class);
        runPipelineService = new RunPipelineService(workerClient);
    }

    @Test
    void testStartPipelineExecution() {
        String repoId = "test-repo";
        String pipelineId = "test-pipeline";

        UUID pipelineRunId = runPipelineService.startPipelineExecution(repoId, pipelineId);
        assertNotNull(pipelineRunId);

        ArgumentCaptor<String> repoCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> pipelineCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> configPathCaptor = ArgumentCaptor.forClass(String.class);

        verify(workerClient, times(1))
                .sendJobToWorker(repoCaptor.capture(), pipelineCaptor.capture(), configPathCaptor.capture());

        assertEquals(repoId, repoCaptor.getValue());
        assertEquals(pipelineId, pipelineCaptor.getValue());
        assertEquals(".pipelines/" + pipelineId + ".yaml", configPathCaptor.getValue());
    }

    @Test
    void testGetPipelineStatus_NotFound() {
        PipelineStatusResponse status = runPipelineService.getPipelineStatus("test-repo", "non-existing-pipeline");
        assertEquals("Not Found", status.getStatus());
    }
}
