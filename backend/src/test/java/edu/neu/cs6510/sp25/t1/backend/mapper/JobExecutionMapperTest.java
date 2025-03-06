package edu.neu.cs6510.sp25.t1.backend.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JobExecutionMapperTest {

  private JobExecutionMapper mapper;

  private UUID id;
  private UUID stageExecutionId;
  private UUID jobId;
  private String commitHash;
  private boolean isLocal;
  private ExecutionStatus status;
  private Instant startTime;
  private Instant completionTime;
  private boolean allowFailure;

  @BeforeEach
  public void setup() {
    mapper = new JobExecutionMapper();

    // Initialize test data
    id = UUID.randomUUID();
    stageExecutionId = UUID.randomUUID();
    jobId = UUID.randomUUID();
    commitHash = "abc123";
    isLocal = true;
    status = ExecutionStatus.SUCCESS;
    startTime = Instant.now();
    completionTime = Instant.now().plusSeconds(60);
    allowFailure = false;
  }

  @Test
  public void testToDTO_AllFieldsPopulated() {
    // Arrange
    JobExecutionEntity entity = JobExecutionEntity.builder()
            .id(id)
            .stageExecutionId(stageExecutionId)
            .jobId(jobId)
            .commitHash(commitHash)
            .isLocal(isLocal)
            .status(status)
            .startTime(startTime)
            .completionTime(completionTime)
            .allowFailure(allowFailure)
            .build();

    // Act
    JobExecutionDTO dto = mapper.toDTO(entity);

    // Assert
    assertNotNull(dto);
    assertEquals(id, dto.getId());
    assertEquals(stageExecutionId, dto.getStageExecutionId());
    assertEquals(jobId, dto.getJobId());
    assertEquals(commitHash, dto.getCommitHash());
    assertEquals(isLocal, dto.isLocal());
    assertEquals(status, dto.getStatus());
    assertEquals(startTime, dto.getStartTime());
    assertEquals(completionTime, dto.getCompletionTime());
    assertEquals(allowFailure, dto.isAllowFailure());
  }

  @Test
  public void testToDTO_NullEntity() {
    // Act
    JobExecutionDTO dto = mapper.toDTO(null);

    // Assert
    assertNull(dto);
  }

  @Test
  public void testToDTO_NullFieldsInEntity() {
    // Arrange
    JobExecutionEntity entity = JobExecutionEntity.builder()
            .id(id)
            .stageExecutionId(null)
            .jobId(jobId)
            .commitHash(null)
            .isLocal(isLocal)
            .status(null)
            .startTime(null)
            .completionTime(null)
            .allowFailure(allowFailure)
            .build();

    // Act
    JobExecutionDTO dto = mapper.toDTO(entity);

    // Assert
    assertNotNull(dto);
    assertEquals(id, dto.getId());
    assertNull(dto.getStageExecutionId());
    assertEquals(jobId, dto.getJobId());
    assertNull(dto.getCommitHash());
    assertEquals(isLocal, dto.isLocal());
    assertNull(dto.getStatus());
    assertNull(dto.getStartTime());
    assertNull(dto.getCompletionTime());
    assertEquals(allowFailure, dto.isAllowFailure());
  }

  @Test
  public void testToEntity_AllFieldsPopulated() {
    // Arrange
    JobExecutionDTO dto = JobExecutionDTO.builder()
            .id(id)
            .stageExecutionId(stageExecutionId)
            .jobId(jobId)
            .commitHash(commitHash)
            .isLocal(isLocal)
            .status(status)
            .startTime(startTime)
            .completionTime(completionTime)
            .allowFailure(allowFailure)
            .build();

    // Act
    JobExecutionEntity entity = mapper.toEntity(dto);

    // Assert
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(stageExecutionId, entity.getStageExecutionId());
    assertEquals(jobId, entity.getJobId());
    assertEquals(commitHash, entity.getCommitHash());
    assertEquals(isLocal, entity.isLocal());
    assertEquals(status, entity.getStatus());
    assertEquals(startTime, entity.getStartTime());
    assertEquals(completionTime, entity.getCompletionTime());
    assertEquals(allowFailure, entity.isAllowFailure());
  }

  @Test
  public void testToEntity_NullDTO() {
    // Act
    JobExecutionEntity entity = mapper.toEntity(null);

    // Assert
    assertNull(entity);
  }

  @Test
  public void testToEntity_NullFieldsInDTO() {
    // Arrange
    JobExecutionDTO dto = JobExecutionDTO.builder()
            .id(id)
            .stageExecutionId(null)
            .jobId(jobId)
            .commitHash(null)
            .isLocal(isLocal)
            .status(null)
            .startTime(null)
            .completionTime(null)
            .allowFailure(allowFailure)
            .build();

    // Act
    JobExecutionEntity entity = mapper.toEntity(dto);

    // Assert
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertNull(entity.getStageExecutionId());
    assertEquals(jobId, entity.getJobId());
    assertNull(entity.getCommitHash());
    assertEquals(isLocal, entity.isLocal());
    assertNull(entity.getStatus());
    assertNull(entity.getStartTime());
    assertNull(entity.getCompletionTime());
    assertEquals(allowFailure, entity.isAllowFailure());
  }
}