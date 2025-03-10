package edu.neu.cs6510.sp25.t1.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.StageExecutionMapper;
import edu.neu.cs6510.sp25.t1.common.dto.StageExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StageExecutionServiceTest {

  @Mock
  private StageExecutionRepository stageExecutionRepository;

  @Mock
  private StageExecutionMapper stageExecutionMapper;

  @Mock
  private JobExecutionService jobExecutionService;

  @InjectMocks
  private StageExecutionService stageExecutionService;

  private UUID testStageExecutionId;
  private UUID testPipelineExecutionId;
  private UUID testStageId;
  private StageExecutionEntity testStageExecutionEntity;
  private StageExecutionDTO testStageExecutionDTO;
  private List<JobExecutionEntity> testSuccessfulJobs;
  private List<JobExecutionEntity> testMixedStatusJobs;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Initialize test data
    testStageExecutionId = UUID.randomUUID();
    testPipelineExecutionId = UUID.randomUUID();
    testStageId = UUID.randomUUID();

    // Create test stage execution entity
    testStageExecutionEntity = new StageExecutionEntity();
    testStageExecutionEntity.setId(testStageExecutionId);
    testStageExecutionEntity.setPipelineExecutionId(testPipelineExecutionId);
    testStageExecutionEntity.setStageId(testStageId);
    testStageExecutionEntity.setStatus(ExecutionStatus.RUNNING);
    testStageExecutionEntity.setStartTime(Instant.now().minusSeconds(3600));

    // Create test stage execution DTO
    testStageExecutionDTO = new StageExecutionDTO();
    testStageExecutionDTO.setId(testStageExecutionId);
    testStageExecutionDTO.setPipelineExecutionId(testPipelineExecutionId);
    testStageExecutionDTO.setStageId(testStageId);
    testStageExecutionDTO.setStatus(ExecutionStatus.RUNNING);
    testStageExecutionDTO.setStartTime(Instant.now().minusSeconds(3600));

    // Create test successful jobs
    JobExecutionEntity successfulJob1 = new JobExecutionEntity();
    successfulJob1.setId(UUID.randomUUID());
    successfulJob1.setStageExecutionId(testStageExecutionId);
    successfulJob1.setStatus(ExecutionStatus.SUCCESS);

    JobExecutionEntity successfulJob2 = new JobExecutionEntity();
    successfulJob2.setId(UUID.randomUUID());
    successfulJob2.setStageExecutionId(testStageExecutionId);
    successfulJob2.setStatus(ExecutionStatus.SUCCESS);

    testSuccessfulJobs = Arrays.asList(successfulJob1, successfulJob2);

    // Create test mixed status jobs (some successful, some failed)
    JobExecutionEntity mixedJob1 = new JobExecutionEntity();
    mixedJob1.setId(UUID.randomUUID());
    mixedJob1.setStageExecutionId(testStageExecutionId);
    mixedJob1.setStatus(ExecutionStatus.SUCCESS);

    JobExecutionEntity mixedJob2 = new JobExecutionEntity();
    mixedJob2.setId(UUID.randomUUID());
    mixedJob2.setStageExecutionId(testStageExecutionId);
    mixedJob2.setStatus(ExecutionStatus.FAILED);

    testMixedStatusJobs = Arrays.asList(mixedJob1, mixedJob2);
  }

  @Test
  void testGetStageExecution_Success() {
    // Arrange
    when(stageExecutionRepository.findById(testStageExecutionId))
            .thenReturn(Optional.of(testStageExecutionEntity));
    when(stageExecutionMapper.toDTO(testStageExecutionEntity))
            .thenReturn(testStageExecutionDTO);

    // Act
    StageExecutionDTO result = stageExecutionService.getStageExecution(testStageExecutionId);

    // Assert
    assertNotNull(result);
    assertEquals(testStageExecutionId, result.getId());
    verify(stageExecutionRepository, times(1)).findById(testStageExecutionId);
    verify(stageExecutionMapper, times(1)).toDTO(testStageExecutionEntity);
  }

  @Test
  void testGetStageExecution_NotFound() {
    // Arrange
    when(stageExecutionRepository.findById(testStageExecutionId))
            .thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      stageExecutionService.getStageExecution(testStageExecutionId);
    });

    assertEquals("Stage Execution not found", exception.getMessage());
    verify(stageExecutionRepository, times(1)).findById(testStageExecutionId);
    verify(stageExecutionMapper, never()).toDTO(any());
  }

  @Test
  void testGetStageStatus_Found() {
    // Arrange
    when(stageExecutionRepository.findByPipelineExecutionIdAndStageId(testPipelineExecutionId, testStageId))
            .thenReturn(Optional.of(testStageExecutionEntity));

    // Act
    String status = stageExecutionService.getStageStatus(testPipelineExecutionId, testStageId);

    // Assert
    assertEquals("RUNNING", status);
    verify(stageExecutionRepository, times(1)).findByPipelineExecutionIdAndStageId(testPipelineExecutionId, testStageId);
  }

  @Test
  void testGetStageStatus_NotFound() {
    // Arrange
    when(stageExecutionRepository.findByPipelineExecutionIdAndStageId(testPipelineExecutionId, testStageId))
            .thenReturn(Optional.empty());

    // Act
    String status = stageExecutionService.getStageStatus(testPipelineExecutionId, testStageId);

    // Assert
    assertEquals("Stage not found", status);
    verify(stageExecutionRepository, times(1)).findByPipelineExecutionIdAndStageId(testPipelineExecutionId, testStageId);
  }

  @Test
  void testFinalizeStageExecution_AllJobsSuccess() {
    // Arrange
    when(jobExecutionService.getJobsByStageExecution(testStageExecutionId))
            .thenReturn(testSuccessfulJobs);
    when(stageExecutionRepository.findById(testStageExecutionId))
            .thenReturn(Optional.of(testStageExecutionEntity));

    // Act
    stageExecutionService.finalizeStageExecution(testStageExecutionId);

    // Assert
    verify(jobExecutionService, times(1)).getJobsByStageExecution(testStageExecutionId);
    verify(stageExecutionRepository, times(1)).findById(testStageExecutionId);
    verify(stageExecutionRepository, times(1)).save(testStageExecutionEntity);
    assertEquals(ExecutionStatus.SUCCESS, testStageExecutionEntity.getStatus());
  }

  @Test
  void testFinalizeStageExecution_SomeJobsFailed() {
    // Arrange
    when(jobExecutionService.getJobsByStageExecution(testStageExecutionId))
            .thenReturn(testMixedStatusJobs);

    // Act
    stageExecutionService.finalizeStageExecution(testStageExecutionId);

    // Assert
    verify(jobExecutionService, times(1)).getJobsByStageExecution(testStageExecutionId);
    verify(stageExecutionRepository, never()).findById(any());
    verify(stageExecutionRepository, never()).save(any());
  }

  @Test
  void testFinalizeStageExecution_NoJobs() {
    // Arrange
    when(jobExecutionService.getJobsByStageExecution(testStageExecutionId))
            .thenReturn(Collections.emptyList());
    when(stageExecutionRepository.findById(testStageExecutionId))
            .thenReturn(Optional.of(testStageExecutionEntity));

    // Act
    stageExecutionService.finalizeStageExecution(testStageExecutionId);

    // Assert
    verify(jobExecutionService, times(1)).getJobsByStageExecution(testStageExecutionId);
    verify(stageExecutionRepository, times(1)).findById(testStageExecutionId);
    verify(stageExecutionRepository, times(1)).save(testStageExecutionEntity);
    assertEquals(ExecutionStatus.SUCCESS, testStageExecutionEntity.getStatus());
  }

  @Test
  void testFinalizeStageExecution_StageNotFound() {
    // Arrange
    when(jobExecutionService.getJobsByStageExecution(testStageExecutionId))
            .thenReturn(testSuccessfulJobs);
    when(stageExecutionRepository.findById(testStageExecutionId))
            .thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      stageExecutionService.finalizeStageExecution(testStageExecutionId);
    });

    assertEquals("Stage Execution not found", exception.getMessage());
    verify(jobExecutionService, times(1)).getJobsByStageExecution(testStageExecutionId);
    verify(stageExecutionRepository, times(1)).findById(testStageExecutionId);
    verify(stageExecutionRepository, never()).save(any());
  }
}