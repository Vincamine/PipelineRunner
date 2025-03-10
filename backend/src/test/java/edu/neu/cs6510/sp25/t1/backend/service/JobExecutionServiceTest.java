package edu.neu.cs6510.sp25.t1.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.JobExecutionMapper;
import edu.neu.cs6510.sp25.t1.common.api.request.JobExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.response.JobExecutionResponse;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JobExecutionServiceTest {

  @Mock
  private JobExecutionRepository jobExecutionRepository;

  @Mock
  private JobExecutionMapper jobExecutionMapper;

  @InjectMocks
  private JobExecutionService jobExecutionService;

  private UUID testJobExecutionId;
  private UUID testStageExecutionId;
  private UUID testJobId;
  private JobExecutionEntity testJobExecutionEntity;
  private JobExecutionDTO testJobExecutionDTO;
  private JobExecutionRequest testJobExecutionRequest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Initialize test data
    testJobExecutionId = UUID.randomUUID();
    testStageExecutionId = UUID.randomUUID();
    testJobId = UUID.randomUUID();

    // Create test entity
    testJobExecutionEntity = new JobExecutionEntity();
    testJobExecutionEntity.setId(testJobExecutionId);
    testJobExecutionEntity.setStageExecutionId(testStageExecutionId);
    testJobExecutionEntity.setJobId(testJobId);
    testJobExecutionEntity.setCommitHash("abc123");
    testJobExecutionEntity.setLocal(true);
    testJobExecutionEntity.setStatus(ExecutionStatus.PENDING);
    testJobExecutionEntity.setStartTime(Instant.now());

    // Create test DTO
    testJobExecutionDTO = new JobExecutionDTO();
    testJobExecutionDTO.setId(testJobExecutionId);
    testJobExecutionDTO.setStageExecutionId(testStageExecutionId);
    testJobExecutionDTO.setJobId(testJobId);
    testJobExecutionDTO.setCommitHash("abc123");
    testJobExecutionDTO.setLocal(true);
    testJobExecutionDTO.setStatus(ExecutionStatus.PENDING);

    // Create test request - using mock since we don't know the constructor signature
    testJobExecutionRequest = mock(JobExecutionRequest.class);
    when(testJobExecutionRequest.getJobId()).thenReturn(testJobId);
    when(testJobExecutionRequest.getStageExecutionId()).thenReturn(testStageExecutionId);
    when(testJobExecutionRequest.getCommitHash()).thenReturn("abc123");
    when(testJobExecutionRequest.isLocal()).thenReturn(true);
  }

  @Test
  void testGetJobExecution_Success() {
    // Arrange
    when(jobExecutionRepository.findById(testJobExecutionId)).thenReturn(Optional.of(testJobExecutionEntity));
    when(jobExecutionMapper.toDTO(testJobExecutionEntity)).thenReturn(testJobExecutionDTO);

    // Act
    JobExecutionDTO result = jobExecutionService.getJobExecution(testJobExecutionId);

    // Assert
    assertNotNull(result);
    assertEquals(testJobExecutionId, result.getId());
    verify(jobExecutionRepository, times(1)).findById(testJobExecutionId);
    verify(jobExecutionMapper, times(1)).toDTO(testJobExecutionEntity);
  }

  @Test
  void testGetJobExecution_NotFound() {
    // Arrange
    when(jobExecutionRepository.findById(testJobExecutionId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      jobExecutionService.getJobExecution(testJobExecutionId);
    });

    assertEquals("Job Execution not found", exception.getMessage());
    verify(jobExecutionRepository, times(1)).findById(testJobExecutionId);
    verify(jobExecutionMapper, never()).toDTO(any());
  }

  @Test
  void testStartJobExecution() {
    // Arrange
    JobExecutionEntity savedEntity = JobExecutionEntity.builder()
            .id(testJobExecutionId)
            .jobId(testJobId)
            .stageExecutionId(testStageExecutionId)
            .commitHash("abc123")
            .isLocal(true)
            .status(ExecutionStatus.PENDING)
            .build();

    when(jobExecutionRepository.save(any(JobExecutionEntity.class))).thenReturn(savedEntity);

    // Act
    JobExecutionResponse response = jobExecutionService.startJobExecution(testJobExecutionRequest);

    // Assert
    assertNotNull(response);
    assertEquals(testJobExecutionId.toString(), response.getJobExecutionId());
    assertEquals("QUEUED", response.getStatus());
    verify(jobExecutionRepository, times(1)).save(any(JobExecutionEntity.class));
  }

  @Test
  void testUpdateJobExecutionStatus_Success() {
    // Arrange
    when(jobExecutionRepository.findById(testJobExecutionId)).thenReturn(Optional.of(testJobExecutionEntity));

    // Act
    jobExecutionService.updateJobExecutionStatus(testJobExecutionId, ExecutionStatus.RUNNING);

    // Assert
    verify(jobExecutionRepository, times(1)).findById(testJobExecutionId);
    verify(jobExecutionRepository, times(1)).save(testJobExecutionEntity);
    assertEquals(ExecutionStatus.RUNNING, testJobExecutionEntity.getStatus());
  }

  @Test
  void testUpdateJobExecutionStatus_JobNotFound() {
    // Arrange
    when(jobExecutionRepository.findById(testJobExecutionId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      jobExecutionService.updateJobExecutionStatus(testJobExecutionId, ExecutionStatus.RUNNING);
    });

    assertEquals("Job Execution not found", exception.getMessage());
    verify(jobExecutionRepository, times(1)).findById(testJobExecutionId);
    verify(jobExecutionRepository, never()).save(any(JobExecutionEntity.class));
  }

  @Test
  void testCancelJobExecution_Pending() {
    // Arrange
    testJobExecutionEntity.setStatus(ExecutionStatus.PENDING);
    when(jobExecutionRepository.findById(testJobExecutionId)).thenReturn(Optional.of(testJobExecutionEntity));

    // Act
    jobExecutionService.cancelJobExecution(testJobExecutionId);

    // Assert
    verify(jobExecutionRepository, times(1)).findById(testJobExecutionId);
    verify(jobExecutionRepository, times(1)).save(testJobExecutionEntity);
    assertEquals(ExecutionStatus.CANCELED, testJobExecutionEntity.getStatus());
  }

  @Test
  void testCancelJobExecution_Running() {
    // Arrange
    testJobExecutionEntity.setStatus(ExecutionStatus.RUNNING);
    when(jobExecutionRepository.findById(testJobExecutionId)).thenReturn(Optional.of(testJobExecutionEntity));

    // Act
    jobExecutionService.cancelJobExecution(testJobExecutionId);

    // Assert
    verify(jobExecutionRepository, times(1)).findById(testJobExecutionId);
    verify(jobExecutionRepository, times(1)).save(testJobExecutionEntity);
    assertEquals(ExecutionStatus.CANCELED, testJobExecutionEntity.getStatus());
  }

  @Test
  void testCancelJobExecution_Completed() {
    // Arrange
    testJobExecutionEntity.setStatus(ExecutionStatus.SUCCESS);
    when(jobExecutionRepository.findById(testJobExecutionId)).thenReturn(Optional.of(testJobExecutionEntity));

    // Act
    jobExecutionService.cancelJobExecution(testJobExecutionId);

    // Assert
    verify(jobExecutionRepository, times(1)).findById(testJobExecutionId);
    verify(jobExecutionRepository, never()).save(any(JobExecutionEntity.class));
    assertEquals(ExecutionStatus.SUCCESS, testJobExecutionEntity.getStatus());
  }

  @Test
  void testCancelJobExecution_NotFound() {
    // Arrange
    when(jobExecutionRepository.findById(testJobExecutionId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      jobExecutionService.cancelJobExecution(testJobExecutionId);
    });

    assertEquals("Job Execution not found", exception.getMessage());
    verify(jobExecutionRepository, times(1)).findById(testJobExecutionId);
    verify(jobExecutionRepository, never()).save(any(JobExecutionEntity.class));
  }

  @Test
  void testGetJobsByStageExecution() {
    // Arrange
    List<JobExecutionEntity> expectedJobs = Arrays.asList(
            testJobExecutionEntity,
            JobExecutionEntity.builder().id(UUID.randomUUID()).build()
    );
    when(jobExecutionRepository.findByStageExecutionId(testStageExecutionId)).thenReturn(expectedJobs);

    // Act
    List<JobExecutionEntity> result = jobExecutionService.getJobsByStageExecution(testStageExecutionId);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(expectedJobs, result);
    verify(jobExecutionRepository, times(1)).findByStageExecutionId(testStageExecutionId);
  }

  @Test
  void testGetJobDependencies() {
    // Arrange
    List<UUID> expectedDependencies = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
    when(jobExecutionRepository.findDependenciesByJobId(testJobId)).thenReturn(expectedDependencies);

    // Act
    List<UUID> result = jobExecutionService.getJobDependencies(testJobId);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(expectedDependencies, result);
    verify(jobExecutionRepository, times(1)).findDependenciesByJobId(testJobId);
  }
}