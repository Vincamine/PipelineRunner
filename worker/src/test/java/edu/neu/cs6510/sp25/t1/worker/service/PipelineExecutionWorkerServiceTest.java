package edu.neu.cs6510.sp25.t1.worker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.execution.JobRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PipelineExecutionWorkerServiceTest {

  @Mock
  private JobRunner jobRunner;

  @Mock
  private WorkerCommunicationService workerCommunicationService;

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private PipelineExecutionWorkerService pipelineExecutionWorkerService;

  private UUID pipelineExecutionId;
  private JobExecutionDTO job1;
  private JobExecutionDTO job2;

  @BeforeEach
  void setUp() {
    pipelineExecutionId = UUID.randomUUID();
    job1 = new JobExecutionDTO();
    job1.setId(UUID.randomUUID());
    job2 = new JobExecutionDTO();
    job2.setId(UUID.randomUUID());
  }

  @Test
  void testExecutePipeline_NoJobsFound() {
    when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(null);
    pipelineExecutionWorkerService.executePipeline(pipelineExecutionId);
    verify(jobRunner, never()).runJob(any());
  }

  @Test
  void testExecutePipeline_IndependentJobsRunImmediately() {
    when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(List.of(job1, job2));
    when(workerCommunicationService.getJobDependencies(any())).thenReturn(List.of());

    pipelineExecutionWorkerService.executePipeline(pipelineExecutionId);

    verify(jobRunner, times(2)).runJob(any());
  }

  @Test
  void testExecutePipeline_DependentJobsWaitForDependencies() {
    UUID depId = UUID.randomUUID();
    when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(List.of(job1, job2));
    when(workerCommunicationService.getJobDependencies(job2.getId())).thenReturn(List.of(depId));
    when(workerCommunicationService.getJobStatus(depId)).thenReturn(ExecutionStatus.SUCCESS);
    when(workerCommunicationService.getJobDependencies(job1.getId())).thenReturn(List.of());

    pipelineExecutionWorkerService.executePipeline(pipelineExecutionId);

    verify(jobRunner).runJob(job1);
    verify(jobRunner).runJob(job2);
  }

  @Test
  void testExecuteJob_SingleJobExecution() {
    pipelineExecutionWorkerService.executeJob(job1);
    verify(jobRunner).runJob(job1);
  }

  @Test
  void testWaitAndRunDependentJob_Success() {
    UUID depId = UUID.randomUUID();
    when(workerCommunicationService.getJobStatus(depId)).thenReturn(ExecutionStatus.SUCCESS);
    when(workerCommunicationService.getJobDependencies(job1.getId())).thenReturn(List.of(depId));

    pipelineExecutionWorkerService.executeJob(job1);
    verify(jobRunner).runJob(job1);
  }

  @Test
  void testWaitAndRunDependentJob_Failure() {
    UUID depId = UUID.randomUUID();
    when(workerCommunicationService.getJobStatus(depId)).thenReturn(ExecutionStatus.RUNNING);
    when(workerCommunicationService.getJobDependencies(job1.getId())).thenReturn(List.of(depId));

    pipelineExecutionWorkerService.executeJob(job1);

    verify(jobRunner, never()).runJob(job1);
  }
}