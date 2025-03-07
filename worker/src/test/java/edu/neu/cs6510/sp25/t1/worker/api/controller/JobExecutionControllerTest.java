package edu.neu.cs6510.sp25.t1.worker.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.worker.service.PipelineExecutionWorkerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the JobExecutionController class.
 *
 * Tests the REST API endpoints for job execution.
 */
@WebMvcTest(JobExecutionController.class)
class JobExecutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PipelineExecutionWorkerService pipelineExecutionWorkerService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Tests that a job execution request is properly queued and returns the expected response.
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    void shouldQueueJobExecution() throws Exception {
        // Prepare test data
        JobExecutionDTO job = new JobExecutionDTO();
        job.setId(UUID.randomUUID());

        // Execute the test
        mockMvc.perform(post("/api/job/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(job)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\": \"QUEUED\"}"));

        // Verify service method was called
        verify(pipelineExecutionWorkerService).executeJob(any(JobExecutionDTO.class));
    }
}