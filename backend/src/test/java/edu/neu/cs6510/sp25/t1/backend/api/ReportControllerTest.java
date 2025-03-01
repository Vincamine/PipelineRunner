package edu.neu.cs6510.sp25.t1.backend.api;

import edu.neu.cs6510.sp25.t1.backend.service.ReportService;
import edu.neu.cs6510.sp25.t1.common.model.execution.PipelineExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ReportService reportService;

    @Test
    void testGetAllPipelines() throws Exception {
        List<String> pipelines = List.of("pipeline1", "pipeline2");
        when(reportService.getAllPipelines()).thenReturn(pipelines);

        mockMvc.perform(get("/api/reports/pipelines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("pipeline1"))
                .andExpect(jsonPath("$[1]").value("pipeline2"));
    }

    @Test
    void testGetPipelineExecutions() throws Exception {
        PipelineExecution execution = new PipelineExecution();
        execution.setPipelineId("pipeline1");
        List<PipelineExecution> executions = List.of(execution);

        when(reportService.getPipelineExecutions("pipeline1")).thenReturn(executions);

        mockMvc.perform(get("/api/reports/pipeline1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pipelineId").value("pipeline1"));
    }

    @Test
    void testGetPipelineRun() throws Exception {
        PipelineExecution execution = new PipelineExecution();
        execution.setPipelineId("pipeline1");

        when(reportService.getPipelineRun("pipeline1", "run1")).thenReturn(execution);

        mockMvc.perform(get("/api/reports/pipeline1/run1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pipelineId").value("pipeline1"));
    }
}
