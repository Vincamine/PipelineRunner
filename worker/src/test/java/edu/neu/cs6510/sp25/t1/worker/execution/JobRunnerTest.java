package edu.neu.cs6510.sp25.t1.worker.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.service.WorkerCommunicationService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobRunnerTest {

  @Mock
  private DockerExecutor dockerExecutor;

  @Mock
  private WorkerCommunicationService workerCommunicationService;

  @InjectMocks
  private JobRunner jobRunner;

  private JobExecutionDTO job;
  private UUID jobId;

  @BeforeEach
  void setUp() {
    jobId = UUID.randomUUID();
    job = new JobExecutionDTO();
    job.setId(jobId);
  }

  @Test
  void testRunJob_Success() {
    when(workerCommunicationService.getJobDependencies(jobId)).thenReturn(List.of());
    when(dockerExecutor.execute(job)).thenReturn(ExecutionStatus.SUCCESS);

    jobRunner.runJob(job);

    verify(workerCommunicationService).reportJobStatus(jobId, ExecutionStatus.SUCCESS, "Job execution completed.");
  }

  @Test
  void testRunJob_Failure_WithAllowFailure() {
    job.setAllowFailure(true);
    when(workerCommunicationService.getJobDependencies(jobId)).thenReturn(List.of());
    when(dockerExecutor.execute(job)).thenReturn(ExecutionStatus.FAILED);

    jobRunner.runJob(job);

    verify(workerCommunicationService).reportJobStatus(jobId, ExecutionStatus.SUCCESS, "Job failed but marked as allowFailure.");
  }
}
