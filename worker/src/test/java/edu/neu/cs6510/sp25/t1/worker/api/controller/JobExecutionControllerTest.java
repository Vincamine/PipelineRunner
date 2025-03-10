package edu.neu.cs6510.sp25.t1.worker.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.worker.service.PipelineExecutionWorkerService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JobExecutionControllerTest {

  @Mock
  private PipelineExecutionWorkerService pipelineExecutionWorkerService;

  @InjectMocks
  private JobExecutionController jobExecutionController;

  private JobExecutionDTO job;

  @BeforeEach
  void setUp() {
    job = new JobExecutionDTO();
    job.setId(UUID.randomUUID());
  }

  @Test
  void testExecuteJob_Success() {
    doNothing().when(pipelineExecutionWorkerService).executeJob(job);

    ResponseEntity<?> response = jobExecutionController.executeJob(job);

    assertEquals(202, response.getStatusCodeValue());
    assertEquals("{\"status\": \"QUEUED\"}", response.getBody());
    verify(pipelineExecutionWorkerService).executeJob(job);
  }

  @Test
  void testExecuteJob_Failure() {
    doThrow(new RuntimeException("Execution error")).when(pipelineExecutionWorkerService).executeJob(job);

    ResponseEntity<?> response = jobExecutionController.executeJob(job);

    assertEquals(400, response.getStatusCodeValue());
    assertEquals("{\"error\": \"Job execution failed.\"}", response.getBody());
    verify(pipelineExecutionWorkerService).executeJob(job);
  }
}
