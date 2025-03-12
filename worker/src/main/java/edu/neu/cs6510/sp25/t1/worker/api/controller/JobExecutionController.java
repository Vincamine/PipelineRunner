package edu.neu.cs6510.sp25.t1.worker.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.worker.service.WorkerExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles job execution requests from the backend.
 */
@RestController
@RequestMapping("/api/worker")
@RequiredArgsConstructor
@Slf4j
public class JobExecutionController {
  private final WorkerExecutionService workerExecutionService;

  /**
   * Executes a job.
   *
   * @param job The job to execute.
   * @return The response entity.
   */
  @PostMapping("/execute")
  public ResponseEntity<?> executeJob(@RequestBody JobExecutionDTO job) {
    log.info("Received job execution request: {}", job.getId());

    try {
      workerExecutionService.executeJob(job);
      return ResponseEntity.accepted().body("{\"status\": \"QUEUED\"}");
    } catch (Exception e) {
      log.error("Failed to queue job {}: {}", job.getId(), e.getMessage());
      return ResponseEntity.badRequest().body("{\"error\": \"Job execution failed.\"}");
    }
  }
}
