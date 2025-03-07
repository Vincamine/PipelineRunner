package edu.neu.cs6510.sp25.t1.backend.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
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
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.StageExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.StageExecutionMapper;
import edu.neu.cs6510.sp25.t1.common.dto.StageExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

@ExtendWith(MockitoExtension.class)
public class StageExecutionServiceTest {

  @Mock
  private StageExecutionRepository stageExecutionRepository;

  @Mock
  private StageExecutionMapper stageExecutionMapper;

  @Mock
  private JobExecutionService jobExecutionService;

  @InjectMocks
  private StageExecutionService stageExecutionService;

  private UUID stageExecutionId;
  private UUID pipelineExecutionId;
  private UUID stageId;
  private StageExecutionEntity stageExecutionEntity;
  private StageExecutionDTO stageExecutionDTO;

  @BeforeEach
  public void setup() {
    stageExecutionId = UUID.randomUUID();
    pipelineExecutionId = UUID.randomUUID();
    stageId = UUID.randomUUID();

    // Setup stage execution entity
    stageExecutionEntity = new StageExecutionEntity();
    stageExecutionEntity.setId(stageExecutionId);
    stageExecutionEntity.setPipelineExecutionId(pipelineExecutionId);
    stageExecutionEntity.setStageId(stageId);
    stageExecutionEntity.setStatus(ExecutionStatus.PENDING);
    stageExecutionEntity.setStartTime(Instant.now());

    // Setup stage execution DTO
    stageExecutionDTO = new StageExecutionDTO();
    // Populate stageExecutionDTO fields as needed
  }

  @Test
  public void testGetStageExecution_Success() {
    // Arrange
    when(stageExecutionRepository.findById(stageExecutionId)).thenReturn(Optional.of(stageExecutionEntity));
    when(stageExecutionMapper.toDTO(stageExecutionEntity)).thenReturn(stageExecutionDTO);

    // Act
    StageExecutionDTO result = stageExecutionService.getStageExecution(stageExecutionId);

    // Assert
    assertNotNull(result);
    assertEquals(stageExecutionDTO, result);
    verify(stageExecutionRepository).findById(stageExecutionId);
    verify(stageExecutionMapper).toDTO(stageExecutionEntity);
  }

  @Test
  public void testGetStageExecution_NotFound() {
    // Arrange
    when(stageExecutionRepository.findById(stageExecutionId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      stageExecutionService.getStageExecution(stageExecutionId);
    });
    assertEquals("Stage Execution not found", exception.getMessage());
    verify(stageExecutionRepository).findById(stageExecutionId);
    verify(stageExecutionMapper, never()).toDTO(any());
  }

  @Test
  public void testGetStageStatus_Found() {
    // Arrange
    when(stageExecutionRepository.findByPipelineExecutionIdAndStageId(pipelineExecutionId, stageId))
            .thenReturn(Optional.of(stageExecutionEntity));

    // Act
    String status = stageExecutionService.getStageStatus(pipelineExecutionId, stageId);

    // Assert
    assertEquals(ExecutionStatus.PENDING.toString(), status);
    verify(stageExecutionRepository).findByPipelineExecutionIdAndStageId(pipelineExecutionId, stageId);
  }

  @Test
  public void testGetStageStatus_NotFound() {
    // Arrange
    when(stageExecutionRepository.findByPipelineExecutionIdAndStageId(pipelineExecutionId, stageId))
            .thenReturn(Optional.empty());

    // Act
    String status = stageExecutionService.getStageStatus(pipelineExecutionId, stageId);

    // Assert
    assertEquals("Stage not found", status);
    verify(stageExecutionRepository).findByPipelineExecutionIdAndStageId(pipelineExecutionId, stageId);
  }

  @Test
  public void testFinalizeStageExecution_AllJobsSuccess() {
    // Arrange
    List<JobExecutionEntity> successfulJobs = Arrays.asList(
            createJobExecution(UUID.randomUUID(), ExecutionStatus.SUCCESS),
            createJobExecution(UUID.randomUUID(), ExecutionStatus.SUCCESS)
    );

    when(jobExecutionService.getJobsByStageExecution(stageExecutionId)).thenReturn(successfulJobs);
    when(stageExecutionRepository.findById(stageExecutionId)).thenReturn(Optional.of(stageExecutionEntity));
    when(stageExecutionRepository.save(stageExecutionEntity)).thenReturn(stageExecutionEntity);

    // Act
    stageExecutionService.finalizeStageExecution(stageExecutionId);

    // Assert
    assertEquals(ExecutionStatus.SUCCESS, stageExecutionEntity.getStatus());
    verify(jobExecutionService).getJobsByStageExecution(stageExecutionId);
    verify(stageExecutionRepository).findById(stageExecutionId);
    verify(stageExecutionRepository).save(stageExecutionEntity);
  }

  @Test
  public void testFinalizeStageExecution_SomeJobsFailed() {
    // Arrange
    List<JobExecutionEntity> mixedStatusJobs = Arrays.asList(
            createJobExecution(UUID.randomUUID(), ExecutionStatus.SUCCESS),
            createJobExecution(UUID.randomUUID(), ExecutionStatus.FAILED)
    );

    when(jobExecutionService.getJobsByStageExecution(stageExecutionId)).thenReturn(mixedStatusJobs);

    // Act
    stageExecutionService.finalizeStageExecution(stageExecutionId);

    // Assert
    verify(jobExecutionService).getJobsByStageExecution(stageExecutionId);
    // Verify that repository findById and save were never called
    verify(stageExecutionRepository, never()).findById(any());
    verify(stageExecutionRepository, never()).save(any());
  }

  @Test
  public void testFinalizeStageExecution_NoJobs() {
    // Arrange
    when(jobExecutionService.getJobsByStageExecution(stageExecutionId)).thenReturn(Collections.emptyList());
    when(stageExecutionRepository.findById(stageExecutionId)).thenReturn(Optional.of(stageExecutionEntity));
    when(stageExecutionRepository.save(stageExecutionEntity)).thenReturn(stageExecutionEntity);

    // Act
    stageExecutionService.finalizeStageExecution(stageExecutionId);

    // Assert
    // An empty list means "all" jobs are successful (vacuously true)
    assertEquals(ExecutionStatus.SUCCESS, stageExecutionEntity.getStatus());
    verify(jobExecutionService).getJobsByStageExecution(stageExecutionId);
    verify(stageExecutionRepository).findById(stageExecutionId);
    verify(stageExecutionRepository).save(stageExecutionEntity);
  }

  @Test
  public void testFinalizeStageExecution_StageNotFound() {
    // Arrange
    List<JobExecutionEntity> successfulJobs = Arrays.asList(
            createJobExecution(UUID.randomUUID(), ExecutionStatus.SUCCESS)
    );

    when(jobExecutionService.getJobsByStageExecution(stageExecutionId)).thenReturn(successfulJobs);
    when(stageExecutionRepository.findById(stageExecutionId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      stageExecutionService.finalizeStageExecution(stageExecutionId);
    });
    assertEquals("Stage Execution not found", exception.getMessage());
    verify(jobExecutionService).getJobsByStageExecution(stageExecutionId);
    verify(stageExecutionRepository).findById(stageExecutionId);
    verify(stageExecutionRepository, never()).save(any());
  }

  // Helper method to create test data
  private JobExecutionEntity createJobExecution(UUID id, ExecutionStatus status) {
    JobExecutionEntity job = new JobExecutionEntity();
    job.setId(id);
    job.setStageExecutionId(stageExecutionId);
    job.setStatus(status);
    return job;
  }
}