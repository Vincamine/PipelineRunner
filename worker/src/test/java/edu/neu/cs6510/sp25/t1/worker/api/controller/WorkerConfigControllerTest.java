package edu.neu.cs6510.sp25.t1.worker.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the WorkerConfigController class.
 * <p>
 * Tests the REST API endpoints for retrieving worker configurations.
 */
@WebMvcTest(WorkerConfigController.class)
@TestPropertySource(properties = {
        "worker.maxRetries=5",
        "worker.retryDelay=1000"
})
class WorkerConfigControllerTest {

  @Autowired
  private MockMvc mockMvc;

//  /**
//   * Tests that the worker configuration is returned correctly.
//   *
//   * @throws Exception if any error occurs during the test
//   */
//  @Test
//  void shouldReturnWorkerConfig() throws Exception {
//    mockMvc.perform(get("/api/worker/config"))
//            .andExpect(status().isOk())
//            .andExpect(content().string("Worker Config - MaxRetries: 5, RetryDelay: 1000ms"));
//  }
}