package edu.neu.cs6510.sp25.t1.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.PipelineExecutionMapper;
import edu.neu.cs6510.sp25.t1.common.api.request.PipelineExecutionRequest;
import edu.neu.cs6510.sp25.t1.common.api.response.PipelineExecutionResponse;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PipelineExecutionServiceTest {

  @Mock
  private PipelineExecutionRepository pipelineExecutionRepository;

  @Mock
  private PipelineExecutionMapper pipelineExecutionMapper;

  @Mock
  private StageExecutionService stageExecutionService;

  @InjectMocks
  private PipelineExecutionService pipelineExecutionService;

  private UUID pipelineExecutionId;
  private UUID pipelineId;
  private PipelineExecutionEntity pipelineExecutionEntity;
  private PipelineExecutionDTO pipelineExecutionDTO;

  @BeforeEach
  public void setup() {
    pipelineExecutionId = UUID.randomUUID();
    pipelineId = UUID.randomUUID();

    // Setup pipeline execution entity
    pipelineExecutionEntity = PipelineExecutionEntity.builder()
            .id(pipelineExecutionId)
            .pipelineId(pipelineId)
            .runNumber(1)
            .commitHash("abc123")
            .isLocal(true)
            .status(ExecutionStatus.PENDING)
            .startTime(Instant.now())
            .build();

    // Setup pipeline execution DTO
    pipelineExecutionDTO = PipelineExecutionDTO.builder()
            .id(pipelineExecutionId)
            .pipelineId(pipelineId)
            .runNumber(1)
            .commitHash("abc123")
            .isLocal(true)
            .status(ExecutionStatus.PENDING)
            .startTime(pipelineExecutionEntity.getStartTime())
            .build();
  }

  @Test
  public void testGetPipelineExecution_Success() {
    // Arrange
    when(pipelineExecutionRepository.findById(pipelineExecutionId)).thenReturn(Optional.of(pipelineExecutionEntity));
    when(pipelineExecutionMapper.toDTO(pipelineExecutionEntity)).thenReturn(pipelineExecutionDTO);

    // Act
    PipelineExecutionResponse response = pipelineExecutionService.getPipelineExecution(pipelineExecutionId);

    // Assert
    assertNotNull(response);
    assertEquals(pipelineExecutionId.toString(), response.getExecutionId());
    assertEquals(ExecutionStatus.PENDING.toString(), response.getStatus());
    verify(pipelineExecutionRepository).findById(pipelineExecutionId);
    verify(pipelineExecutionMapper).toDTO(pipelineExecutionEntity);
  }

  @Test
  public void testGetPipelineExecution_NotFound() {
    // Arrange
    when(pipelineExecutionRepository.findById(pipelineExecutionId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      pipelineExecutionService.getPipelineExecution(pipelineExecutionId);
    });
    assertEquals("Pipeline Execution not found", exception.getMessage());
    verify(pipelineExecutionRepository).findById(pipelineExecutionId);
    verify(pipelineExecutionMapper, never()).toDTO(any());
  }

  @Test
  public void testStartPipelineExecution() {
    // Arrange
    PipelineExecutionRequest mockRequest = mock(PipelineExecutionRequest.class);
    when(mockRequest.getPipelineId()).thenReturn(pipelineId);
    when(mockRequest.getCommitHash()).thenReturn("abc123");
    when(mockRequest.isLocal()).thenReturn(true);

    when(pipelineExecutionRepository.save(any(PipelineExecutionEntity.class))).thenAnswer(invocation -> {
      PipelineExecutionEntity entity = invocation.getArgument(0);
      // Simulate ID generation by the database
      if (entity.getId() == null) {
        entity = PipelineExecutionEntity.builder()
                .id(pipelineExecutionId)
                .pipelineId(entity.getPipelineId())
                .commitHash(entity.getCommitHash())
                .isLocal(entity.isLocal())
                .status(entity.getStatus())
                .startTime(entity.getStartTime())
                .build();
      }
      return entity;
    });

    // Act
    PipelineExecutionResponse response = pipelineExecutionService.startPipelineExecution(mockRequest);

    // Assert
    assertNotNull(response);
    assertEquals(pipelineExecutionId.toString(), response.getExecutionId());
    assertEquals("PENDING", response.getStatus());
    verify(pipelineExecutionRepository).save(any(PipelineExecutionEntity.class));
  }

  @Test
  public void testIsDuplicateExecution_Duplicate() {
    // Arrange
    PipelineExecutionRequest mockRequest = mock(PipelineExecutionRequest.class);
    when(mockRequest.getPipelineId()).thenReturn(pipelineId);
    when(mockRequest.getRunNumber()).thenReturn(1);

    when(pipelineExecutionRepository.findByPipelineIdAndRunNumber(pipelineId, 1))
            .thenReturn(Optional.of(pipelineExecutionEntity));

    // Act
    boolean isDuplicate = pipelineExecutionService.isDuplicateExecution(mockRequest);

    // Assert
    assertTrue(isDuplicate);
    verify(pipelineExecutionRepository).findByPipelineIdAndRunNumber(pipelineId, 1);
  }

  @Test
  public void testIsDuplicateExecution_NotDuplicate() {
    // Arrange
    PipelineExecutionRequest mockRequest = mock(PipelineExecutionRequest.class);
    when(mockRequest.getPipelineId()).thenReturn(pipelineId);
    when(mockRequest.getRunNumber()).thenReturn(2); // Different run number

    when(pipelineExecutionRepository.findByPipelineIdAndRunNumber(pipelineId, 2))
            .thenReturn(Optional.empty());

    // Act
    boolean isDuplicate = pipelineExecutionService.isDuplicateExecution(mockRequest);

    // Assert
    assertFalse(isDuplicate);
    verify(pipelineExecutionRepository).findByPipelineIdAndRunNumber(pipelineId, 2);
  }

  @Test
  public void testFinalizePipelineExecution_Success() {
    // Arrange
    when(pipelineExecutionRepository.findById(pipelineExecutionId)).thenReturn(Optional.of(pipelineExecutionEntity));
    when(pipelineExecutionRepository.save(pipelineExecutionEntity)).thenReturn(pipelineExecutionEntity);

    // Act
    pipelineExecutionService.finalizePipelineExecution(pipelineExecutionId);

    // Assert
    verify(pipelineExecutionRepository).findById(pipelineExecutionId);
    verify(pipelineExecutionRepository).save(pipelineExecutionEntity);
    assertEquals(ExecutionStatus.SUCCESS, pipelineExecutionEntity.getStatus());
  }

  @Test
  public void testFinalizePipelineExecution_NotFound() {
    // Arrange
    when(pipelineExecutionRepository.findById(pipelineExecutionId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      pipelineExecutionService.finalizePipelineExecution(pipelineExecutionId);
    });
    assertEquals("Pipeline Execution not found", exception.getMessage());
    verify(pipelineExecutionRepository).findById(pipelineExecutionId);
    verify(pipelineExecutionRepository, never()).save(any());
  }
}