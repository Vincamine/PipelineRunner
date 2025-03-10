package edu.neu.cs6510.sp25.t1.worker.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the WorkerHealthController class.
 * <p>
 * Tests the health check endpoint of the worker service.
 */
@WebMvcTest(WorkerHealthController.class)
class WorkerHealthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  /**
   * Tests that the health check endpoint returns the expected response.
   *
   * @throws Exception if any error occurs during the test
   */
  @Test
  void shouldReturnHealthStatus() throws Exception {
    mockMvc.perform(get("/api/worker/health"))
            .andExpect(status().isOk())
            .andExpect(content().string("Worker is running."));
  }
}