package edu.neu.cs6510.sp25.t1.backend.api;

import edu.neu.cs6510.sp25.t1.backend.service.PipelineExecutionService;
import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;
import edu.neu.cs6510.sp25.t1.common.model.execution.PipelineExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PipelineController.class)
@ExtendWith(MockitoExtension.class)
class PipelineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PipelineExecutionService pipelineService;

    @Test
    void testRunPipeline() throws Exception {
        RunPipelineRequest request = new RunPipelineRequest();
        request.setPipeline("test-pipeline");
        PipelineExecution execution = new PipelineExecution();
        execution.setPipelineId("123");
        
        when(pipelineService.startPipeline(any(), any())).thenReturn(execution);

        mockMvc.perform(post("/api/v1/pipelines/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pipeline\":\"test-pipeline\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pipelineId").value("123"));
    }

    @Test
    void testGetStatus_Found() throws Exception {
        PipelineExecution execution = new PipelineExecution();
        execution.setPipelineId("123");
        execution.setState(PipelineExecution.State.RUNNING);

        when(pipelineService.getPipelineExecution("123")).thenReturn(execution);

        mockMvc.perform(get("/api/v1/pipelines/123/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.status").value("RUNNING"));
    }

    @Test
    void testGetStatus_NotFound() throws Exception {
        when(pipelineService.getPipelineExecution("123")).thenReturn(null);

        mockMvc.perform(get("/api/v1/pipelines/123/status"))
                .andExpect(status().isNotFound());
    }
}
