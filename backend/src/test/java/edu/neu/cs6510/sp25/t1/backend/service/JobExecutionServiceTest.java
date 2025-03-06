package edu.neu.cs6510.sp25.t1.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.JobExecutionMapper;
import edu.neu.cs6510.sp25.t1.common.api.request.JobExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.response.JobExecutionResponse;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

@ExtendWith(MockitoExtension.class)
public class JobExecutionServiceTest {

  @Mock
  private JobExecutionRepository jobExecutionRepository;

  @Mock
  private JobExecutionMapper jobExecutionMapper;

  @InjectMocks
  private JobExecutionService jobExecutionService;

  private UUID jobExecutionId;
  private UUID stageExecutionId;
  private UUID jobId;
  private JobExecutionEntity jobExecutionEntity;
  private JobExecutionDTO jobExecutionDTO;

  @BeforeEach
  public void setup() {
    jobExecutionId = UUID.randomUUID();
    stageExecutionId = UUID.randomUUID();
    jobId = UUID.randomUUID();

    // Setup job execution entity
    jobExecutionEntity = JobExecutionEntity.builder()
            .id(jobExecutionId)
            .stageExecutionId(stageExecutionId)
            .jobId(jobId)
            .commitHash("abc123")
            .isLocal(true)
            .status(ExecutionStatus.PENDING)
            .startTime(Instant.now())
            .build();

    // Setup job execution DTO
    jobExecutionDTO = JobExecutionDTO.builder()
            .id(jobExecutionId)
            .stageExecutionId(stageExecutionId)
            .jobId(jobId)
            .commitHash("abc123")
            .isLocal(true)
            .status(ExecutionStatus.PENDING)
            .build();
  }

  @Test
  public void testGetJobExecution_Success() {
    // Arrange
    when(jobExecutionRepository.findById(jobExecutionId)).thenReturn(Optional.of(jobExecutionEntity));
    when(jobExecutionMapper.toDTO(jobExecutionEntity)).thenReturn(jobExecutionDTO);

    // Act
    JobExecutionDTO result = jobExecutionService.getJobExecution(jobExecutionId);

    // Assert
    assertNotNull(result);
    assertEquals(jobExecutionDTO, result);
    verify(jobExecutionRepository).findById(jobExecutionId);
    verify(jobExecutionMapper).toDTO(jobExecutionEntity);
  }

  @Test
  public void testGetJobExecution_NotFound() {
    // Arrange
    when(jobExecutionRepository.findById(jobExecutionId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      jobExecutionService.getJobExecution(jobExecutionId);
    });
    assertEquals("Job Execution not found", exception.getMessage());
    verify(jobExecutionRepository).findById(jobExecutionId);
    verify(jobExecutionMapper, never()).toDTO(any());
  }

  @Test
  public void testStartJobExecution() {
    // Arrange
    JobExecutionRequest mockRequest = mock(JobExecutionRequest.class);
    when(mockRequest.getJobId()).thenReturn(jobId);
    when(mockRequest.getStageExecutionId()).thenReturn(stageExecutionId);
    when(mockRequest.getCommitHash()).thenReturn("abc123");
    when(mockRequest.isLocal()).thenReturn(true);

    when(jobExecutionRepository.save(any(JobExecutionEntity.class))).thenAnswer(invocation -> {
      JobExecutionEntity entity = invocation.getArgument(0);
      // Simulate ID generation by the database
      if (entity.getId() == null) {
        entity = JobExecutionEntity.builder()
                .id(jobExecutionId)
                .stageExecutionId(entity.getStageExecutionId())
                .jobId(entity.getJobId())
                .commitHash(entity.getCommitHash())
                .isLocal(entity.isLocal())
                .status(entity.getStatus())
                .build();
      }
      return entity;
    });

    // Act
    JobExecutionResponse response = jobExecutionService.startJobExecution(mockRequest);

    // Assert
    assertNotNull(response);
    assertEquals(jobExecutionId.toString(), response.getJobExecutionId());
    assertEquals("QUEUED", response.getStatus());
    verify(jobExecutionRepository).save(any(JobExecutionEntity.class));
  }

  @Test
  public void testUpdateJobExecutionStatus_Success() {
    // Arrange
    when(jobExecutionRepository.findById(jobExecutionId)).thenReturn(Optional.of(jobExecutionEntity));
    when(jobExecutionRepository.save(jobExecutionEntity)).thenReturn(jobExecutionEntity);

    // Act
    jobExecutionService.updateJobExecutionStatus(jobExecutionId, ExecutionStatus.RUNNING);

    // Assert
    verify(jobExecutionRepository).findById(jobExecutionId);
    verify(jobExecutionRepository).save(jobExecutionEntity);
    assertEquals(ExecutionStatus.RUNNING, jobExecutionEntity.getStatus());
  }

  @Test
  public void testUpdateJobExecutionStatus_NotFound() {
    // Arrange
    when(jobExecutionRepository.findById(jobExecutionId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      jobExecutionService.updateJobExecutionStatus(jobExecutionId, ExecutionStatus.RUNNING);
    });
    assertEquals("Job Execution not found", exception.getMessage());
    verify(jobExecutionRepository).findById(jobExecutionId);
    verify(jobExecutionRepository, never()).save(any());
  }

  @Test
  public void testCancelJobExecution_PendingJob() {
    // Arrange
    JobExecutionEntity pendingJobEntity = JobExecutionEntity.builder()
            .id(jobExecutionId)
            .stageExecutionId(stageExecutionId)
            .jobId(jobId)
            .status(ExecutionStatus.PENDING)
            .build();

    when(jobExecutionRepository.findById(jobExecutionId)).thenReturn(Optional.of(pendingJobEntity));
    when(jobExecutionRepository.save(pendingJobEntity)).thenReturn(pendingJobEntity);

    // Act
    jobExecutionService.cancelJobExecution(jobExecutionId);

    // Assert
    verify(jobExecutionRepository).findById(jobExecutionId);
    verify(jobExecutionRepository).save(pendingJobEntity);
    assertEquals(ExecutionStatus.CANCELED, pendingJobEntity.getStatus());
  }

  @Test
  public void testCancelJobExecution_RunningJob() {
    // Arrange
    JobExecutionEntity runningJobEntity = JobExecutionEntity.builder()
            .id(jobExecutionId)
            .stageExecutionId(stageExecutionId)
            .jobId(jobId)
            .status(ExecutionStatus.RUNNING)
            .build();

    when(jobExecutionRepository.findById(jobExecutionId)).thenReturn(Optional.of(runningJobEntity));
    when(jobExecutionRepository.save(runningJobEntity)).thenReturn(runningJobEntity);

    // Act
    jobExecutionService.cancelJobExecution(jobExecutionId);

    // Assert
    verify(jobExecutionRepository).findById(jobExecutionId);
    verify(jobExecutionRepository).save(runningJobEntity);
    assertEquals(ExecutionStatus.CANCELED, runningJobEntity.getStatus());
  }

  @Test
  public void testCancelJobExecution_CompletedJob() {
    // Arrange
    JobExecutionEntity completedJobEntity = JobExecutionEntity.builder()
            .id(jobExecutionId)
            .stageExecutionId(stageExecutionId)
            .jobId(jobId)
            .status(ExecutionStatus.SUCCESS)
            .build();

    when(jobExecutionRepository.findById(jobExecutionId)).thenReturn(Optional.of(completedJobEntity));

    // Act
    jobExecutionService.cancelJobExecution(jobExecutionId);

    // Assert
    verify(jobExecutionRepository).findById(jobExecutionId);
    // Verify that save is not called for a completed job
    verify(jobExecutionRepository, never()).save(any());
    // Status should remain COMPLETED
    assertEquals(ExecutionStatus.SUCCESS, completedJobEntity.getStatus());
  }

  @Test
  public void testCancelJobExecution_NotFound() {
    // Arrange
    when(jobExecutionRepository.findById(jobExecutionId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      jobExecutionService.cancelJobExecution(jobExecutionId);
    });
    assertEquals("Job Execution not found", exception.getMessage());
    verify(jobExecutionRepository).findById(jobExecutionId);
    verify(jobExecutionRepository, never()).save(any());
  }

  @Test
  public void testGetJobsByStageExecution() {
    // Arrange
    List<JobExecutionEntity> jobExecutions = Arrays.asList(
            jobExecutionEntity,
            JobExecutionEntity.builder()
                    .id(UUID.randomUUID())
                    .stageExecutionId(stageExecutionId)
                    .jobId(UUID.randomUUID())
                    .status(ExecutionStatus.RUNNING)
                    .build()
    );
    when(jobExecutionRepository.findByStageExecutionId(stageExecutionId)).thenReturn(jobExecutions);

    // Act
    List<JobExecutionEntity> result = jobExecutionService.getJobsByStageExecution(stageExecutionId);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(jobExecutions, result);
    verify(jobExecutionRepository).findByStageExecutionId(stageExecutionId);
  }
}