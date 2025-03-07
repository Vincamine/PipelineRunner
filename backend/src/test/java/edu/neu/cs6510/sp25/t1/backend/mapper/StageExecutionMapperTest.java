package edu.neu.cs6510.sp25.t1.backend.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.dto.StageExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StageExecutionMapperTest {

  private StageExecutionMapper mapper;

  private UUID id;
  private UUID stageId;
  private UUID pipelineExecutionId;
  private int executionOrder;
  private String commitHash;
  private boolean isLocal;
  private ExecutionStatus status;
  private Instant startTime;
  private Instant completionTime;

  @BeforeEach
  public void setup() {
    mapper = new StageExecutionMapper();

    // Initialize test data
    id = UUID.randomUUID();
    stageId = UUID.randomUUID();
    pipelineExecutionId = UUID.randomUUID();
    executionOrder = 3;
    commitHash = "abc123def456";
    isLocal = true;
    status = ExecutionStatus.RUNNING;
    startTime = Instant.now();
    completionTime = startTime.plusSeconds(300);
  }

  @Test
  public void testToDTO_AllFieldsPopulated() {
    // Arrange
    StageExecutionEntity entity = StageExecutionEntity.builder()
            .id(id)
            .stageId(stageId)
            .pipelineExecutionId(pipelineExecutionId)
            .executionOrder(executionOrder)
            .commitHash(commitHash)
            .isLocal(isLocal)
            .status(status)
            .startTime(startTime)
            .completionTime(completionTime)
            .build();

    // Act
    StageExecutionDTO dto = mapper.toDTO(entity);

    // Assert
    assertNotNull(dto);
    assertEquals(id, dto.getId());
    assertEquals(stageId, dto.getStageId());
    assertEquals(pipelineExecutionId, dto.getPipelineExecutionId());
    assertEquals(executionOrder, dto.getExecutionOrder());
    assertEquals(commitHash, dto.getCommitHash());
    assertEquals(isLocal, dto.isLocal());
    assertEquals(status, dto.getStatus());
    assertEquals(startTime, dto.getStartTime());
    assertEquals(completionTime, dto.getCompletionTime());
  }

  @Test
  public void testToDTO_NullFields() {
    // Arrange
    StageExecutionEntity entity = StageExecutionEntity.builder()
            .id(id)
            .stageId(null)
            .pipelineExecutionId(pipelineExecutionId)
            .executionOrder(executionOrder)
            .commitHash(null)
            .isLocal(isLocal)
            .status(null)
            .startTime(null)
            .completionTime(null)
            .build();

    // Act
    StageExecutionDTO dto = mapper.toDTO(entity);

    // Assert
    assertNotNull(dto);
    assertEquals(id, dto.getId());
    assertNull(dto.getStageId());
    assertEquals(pipelineExecutionId, dto.getPipelineExecutionId());
    assertEquals(executionOrder, dto.getExecutionOrder());
    assertNull(dto.getCommitHash());
    assertEquals(isLocal, dto.isLocal());
    assertNull(dto.getStatus());
    assertNull(dto.getStartTime());
    assertNull(dto.getCompletionTime());
  }

  @Test
  public void testToDTO_NullEntity() {
    // Act
    StageExecutionDTO dto = mapper.toDTO(null);

    // Assert
    assertNull(dto);
  }

  @Test
  public void testToEntity_AllFieldsPopulated() {
    // Arrange
    StageExecutionDTO dto = StageExecutionDTO.builder()
            .id(id)
            .stageId(stageId)
            .pipelineExecutionId(pipelineExecutionId)
            .executionOrder(executionOrder)
            .commitHash(commitHash)
            .isLocal(isLocal)
            .status(status)
            .startTime(startTime)
            .completionTime(completionTime)
            .build();

    // Act
    StageExecutionEntity entity = mapper.toEntity(dto);

    // Assert
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(stageId, entity.getStageId());
    assertEquals(pipelineExecutionId, entity.getPipelineExecutionId());
    assertEquals(executionOrder, entity.getExecutionOrder());
    assertEquals(commitHash, entity.getCommitHash());
    assertEquals(isLocal, entity.isLocal());
    assertEquals(status, entity.getStatus());
    assertEquals(startTime, entity.getStartTime());
    assertEquals(completionTime, entity.getCompletionTime());
  }

  @Test
  public void testToEntity_NullFields() {
    // Arrange
    StageExecutionDTO dto = StageExecutionDTO.builder()
            .id(id)
            .stageId(null)
            .pipelineExecutionId(pipelineExecutionId)
            .executionOrder(executionOrder)
            .commitHash(null)
            .isLocal(isLocal)
            .status(null)
            .startTime(null)
            .completionTime(null)
            .build();

    // Act
    StageExecutionEntity entity = mapper.toEntity(dto);

    // Assert
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertNull(entity.getStageId());
    assertEquals(pipelineExecutionId, entity.getPipelineExecutionId());
    assertEquals(executionOrder, entity.getExecutionOrder());
    assertNull(entity.getCommitHash());
    assertEquals(isLocal, entity.isLocal());
    assertNull(entity.getStatus());
    assertNull(entity.getStartTime());
    assertNull(entity.getCompletionTime());
  }

  @Test
  public void testToEntity_NullDTO() {
    // Act
    StageExecutionEntity entity = mapper.toEntity(null);

    // Assert
    assertNull(entity);
  }
}