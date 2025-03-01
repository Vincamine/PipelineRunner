package edu.neu.cs6510.sp25.t1.backend.api;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExecutionController.class)
class ExecutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetExecutionLogs() throws Exception {
        List<String> mockLogs = List.of("Job started", "Job completed");

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readAllLines(any())).thenReturn(mockLogs);

            mockMvc.perform(get("/api/executions/logs"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[\"Job started\",\"Job completed\"]"));
        }
    }
}
