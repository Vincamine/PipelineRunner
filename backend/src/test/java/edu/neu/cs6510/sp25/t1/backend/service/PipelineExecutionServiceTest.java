package edu.neu.cs6510.sp25.t1.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PipelineExecutionServiceTest {

  @Mock
  private PipelineExecutionRepository pipelineExecutionRepository;

  @Mock
  private PipelineExecutionMapper pipelineExecutionMapper;

  @InjectMocks
  private PipelineExecutionService pipelineExecutionService;

  private UUID testPipelineExecutionId;
  private UUID testPipelineId;
  private PipelineExecutionEntity testPipelineExecutionEntity;
  private PipelineExecutionDTO testPipelineExecutionDTO;
  private PipelineExecutionRequest testPipelineExecutionRequest;
  private int testRunNumber;
  private String testRepo;
  private String testBranch;
  private String testCommitHash;
  private boolean testIsLocal;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Initialize test data
    testPipelineExecutionId = UUID.randomUUID();
    testPipelineId = UUID.randomUUID();
    testRunNumber = 42;
    testRepo = "https://github.com/test/repo";
    testBranch = "main";
    testCommitHash = "abc123";
    testIsLocal = true;

    // Create test entity
    testPipelineExecutionEntity = PipelineExecutionEntity.builder()
            .id(testPipelineExecutionId)
            .pipelineId(testPipelineId)
            .runNumber(testRunNumber)
            .commitHash(testCommitHash)
            .isLocal(testIsLocal)
            .status(ExecutionStatus.PENDING)
            .startTime(Instant.now())
            .build();

    // Create test DTO
    testPipelineExecutionDTO = PipelineExecutionDTO.builder()
            .id(testPipelineExecutionId)
            .pipelineId(testPipelineId)
            .runNumber(testRunNumber)
            .commitHash(testCommitHash)
            .isLocal(testIsLocal)
            .status(ExecutionStatus.PENDING)
            .startTime(Instant.now())
            .build();

    // Create test request - using real constructor
    testPipelineExecutionRequest = new PipelineExecutionRequest(
            testPipelineId,
            testRepo,
            testBranch,
            testCommitHash,
            testIsLocal,
            testRunNumber
    );
  }

  @Test
  void testGetPipelineExecution_Success() {
    // Arrange
    when(pipelineExecutionRepository.findById(testPipelineExecutionId))
            .thenReturn(Optional.of(testPipelineExecutionEntity));
    when(pipelineExecutionMapper.toDTO(testPipelineExecutionEntity))
            .thenReturn(testPipelineExecutionDTO);

    // Act
    PipelineExecutionResponse response = pipelineExecutionService.getPipelineExecution(testPipelineExecutionId);

    // Assert
    assertNotNull(response);
    // Since we can't access fields directly, we need to use a different approach
    // Indirectly validate that the service called the repository and mapper
    verify(pipelineExecutionRepository, times(1)).findById(testPipelineExecutionId);
    verify(pipelineExecutionMapper, times(1)).toDTO(testPipelineExecutionEntity);
  }

  @Test
  void testGetPipelineExecution_NotFound() {
    // Arrange
    when(pipelineExecutionRepository.findById(testPipelineExecutionId))
            .thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      pipelineExecutionService.getPipelineExecution(testPipelineExecutionId);
    });

    assertEquals("Pipeline Execution not found", exception.getMessage());
    verify(pipelineExecutionRepository, times(1)).findById(testPipelineExecutionId);
    verify(pipelineExecutionMapper, never()).toDTO(any());
  }

  @Test
  void testStartPipelineExecution() {
    // Arrange
    when(pipelineExecutionRepository.save(any(PipelineExecutionEntity.class)))
            .thenReturn(testPipelineExecutionEntity);

    // Act
    PipelineExecutionResponse response = pipelineExecutionService.startPipelineExecution(testPipelineExecutionRequest);

    // Assert
    assertNotNull(response);
    // Verify that the repository was called to save the entity
    verify(pipelineExecutionRepository, times(1)).save(any(PipelineExecutionEntity.class));
  }

  @Test
  void testIsDuplicateExecution_WhenDuplicate() {
    // Arrange
    when(pipelineExecutionRepository.findByPipelineIdAndRunNumber(testPipelineId, testRunNumber))
            .thenReturn(Optional.of(testPipelineExecutionEntity));

    // Act
    boolean result = pipelineExecutionService.isDuplicateExecution(testPipelineExecutionRequest);

    // Assert
    assertTrue(result);
    verify(pipelineExecutionRepository, times(1)).findByPipelineIdAndRunNumber(testPipelineId, testRunNumber);
  }

  @Test
  void testIsDuplicateExecution_WhenNotDuplicate() {
    // Arrange
    when(pipelineExecutionRepository.findByPipelineIdAndRunNumber(testPipelineId, testRunNumber))
            .thenReturn(Optional.empty());

    // Act
    boolean result = pipelineExecutionService.isDuplicateExecution(testPipelineExecutionRequest);

    // Assert
    assertFalse(result);
    verify(pipelineExecutionRepository, times(1)).findByPipelineIdAndRunNumber(testPipelineId, testRunNumber);
  }

  @Test
  void testFinalizePipelineExecution_Success() {
    // Arrange
    when(pipelineExecutionRepository.findById(testPipelineExecutionId))
            .thenReturn(Optional.of(testPipelineExecutionEntity));

    // Act
    pipelineExecutionService.finalizePipelineExecution(testPipelineExecutionId);

    // Assert
    verify(pipelineExecutionRepository, times(1)).findById(testPipelineExecutionId);
    verify(pipelineExecutionRepository, times(1)).save(testPipelineExecutionEntity);
    assertEquals(ExecutionStatus.SUCCESS, testPipelineExecutionEntity.getStatus());
  }

  @Test
  void testFinalizePipelineExecution_NotFound() {
    // Arrange
    when(pipelineExecutionRepository.findById(testPipelineExecutionId))
            .thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      pipelineExecutionService.finalizePipelineExecution(testPipelineExecutionId);
    });

    assertEquals("Pipeline Execution not found", exception.getMessage());
    verify(pipelineExecutionRepository, times(1)).findById(testPipelineExecutionId);
    verify(pipelineExecutionRepository, never()).save(any());
  }
}