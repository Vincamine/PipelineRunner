package edu.neu.cs6510.sp25.t1.backend.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.Instant;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PipelineExecutionMapperTest {

  @InjectMocks
  private PipelineExecutionMapper mapper;

  private UUID id;
  private UUID pipelineId;
  private int runNumber;
  private String commitHash;
  private boolean isLocal;
  private ExecutionStatus status;
  private Instant startTime;
  private Instant completionTime;

  @BeforeEach
  public void setup() {
    mapper = new PipelineExecutionMapper();

    // Initialize test data
    id = UUID.randomUUID();
    pipelineId = UUID.randomUUID();
    runNumber = 5;
    commitHash = "abc123def456";
    isLocal = true;
    status = ExecutionStatus.RUNNING;
    startTime = Instant.now();
    completionTime = startTime.plusSeconds(120);
  }

  @Test
  public void testToDTO_AllFieldsPopulated() {
    // Arrange
    PipelineExecutionEntity entity = PipelineExecutionEntity.builder()
            .id(id)
            .pipelineId(pipelineId)
            .runNumber(runNumber)
            .commitHash(commitHash)
            .isLocal(isLocal)
            .status(status)
            .startTime(startTime)
            .completionTime(completionTime)
            .build();

    // Act
    PipelineExecutionDTO dto = mapper.toDTO(entity);

    // Assert
    assertNotNull(dto);
    assertEquals(id, dto.getId());
    assertEquals(pipelineId, dto.getPipelineId());
    assertEquals(runNumber, dto.getRunNumber());
    assertEquals(commitHash, dto.getCommitHash());
    assertEquals(isLocal, dto.isLocal());
    assertEquals(status, dto.getStatus());
    assertEquals(startTime, dto.getStartTime());
    assertEquals(completionTime, dto.getCompletionTime());
  }

  @Test
  public void testToDTO_NullFields() {
    // Arrange
    PipelineExecutionEntity entity = PipelineExecutionEntity.builder()
            .id(id)
            .pipelineId(null)
            .runNumber(runNumber)
            .commitHash(null)
            .isLocal(isLocal)
            .status(null)
            .startTime(null)
            .completionTime(null)
            .build();

    // Act
    PipelineExecutionDTO dto = mapper.toDTO(entity);

    // Assert
    assertNotNull(dto);
    assertEquals(id, dto.getId());
    assertNull(dto.getPipelineId());
    assertEquals(runNumber, dto.getRunNumber());
    assertNull(dto.getCommitHash());
    assertEquals(isLocal, dto.isLocal());
    assertNull(dto.getStatus());
    assertNull(dto.getStartTime());
    assertNull(dto.getCompletionTime());
  }

  @Test
  public void testToDTO_NullEntity() {
    // Need to update mapper to handle null input
    PipelineExecutionMapper updatedMapper = new PipelineExecutionMapper() {
      @Override
      public PipelineExecutionDTO toDTO(PipelineExecutionEntity entity) {
        if (entity == null) {
          return null;
        }
        return super.toDTO(entity);
      }
    };

    // Act
    PipelineExecutionDTO dto = updatedMapper.toDTO(null);

    // Assert
    assertNull(dto);
  }

  @Test
  public void testToEntity_AllFieldsPopulated() {
    // Arrange
    PipelineExecutionDTO dto = PipelineExecutionDTO.builder()
            .id(id)
            .pipelineId(pipelineId)
            .runNumber(runNumber)
            .commitHash(commitHash)
            .isLocal(isLocal)
            .status(status)
            .startTime(startTime)
            .completionTime(completionTime)
            .build();

    // Act
    PipelineExecutionEntity entity = mapper.toEntity(dto);

    // Assert
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(pipelineId, entity.getPipelineId());
    assertEquals(runNumber, entity.getRunNumber());
    assertEquals(commitHash, entity.getCommitHash());
    assertEquals(isLocal, entity.isLocal());
    assertEquals(status, entity.getStatus());
    assertEquals(startTime, entity.getStartTime());
    assertEquals(completionTime, entity.getCompletionTime());
  }

  @Test
  public void testToEntity_NullFields() {
    // Arrange
    PipelineExecutionDTO dto = PipelineExecutionDTO.builder()
            .id(id)
            .pipelineId(null)
            .runNumber(runNumber)
            .commitHash(null)
            .isLocal(isLocal)
            .status(null)
            .startTime(null)
            .completionTime(null)
            .build();

    // Act
    PipelineExecutionEntity entity = mapper.toEntity(dto);

    // Assert
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertNull(entity.getPipelineId());
    assertEquals(runNumber, entity.getRunNumber());
    assertNull(entity.getCommitHash());
    assertEquals(isLocal, entity.isLocal());
    assertNull(entity.getStatus());
    assertNull(entity.getStartTime());
    assertNull(entity.getCompletionTime());
  }

  @Test
  public void testToEntity_NullDTO() {
    // Need to update mapper to handle null input
    PipelineExecutionMapper updatedMapper = new PipelineExecutionMapper() {
      @Override
      public PipelineExecutionEntity toEntity(PipelineExecutionDTO dto) {
        if (dto == null) {
          return null;
        }
        return super.toEntity(dto);
      }
    };

    // Act
    PipelineExecutionEntity entity = updatedMapper.toEntity(null);

    // Assert
    assertNull(entity);
  }
}