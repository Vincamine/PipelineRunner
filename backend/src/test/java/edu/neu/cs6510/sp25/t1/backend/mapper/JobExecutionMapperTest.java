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

class JobExecutionMapperTest {

  private JobExecutionMapper jobExecutionMapper;

  private UUID testId;
  private UUID testStageExecutionId;
  private UUID testJobId;
  private String testCommitHash;
  private boolean testIsLocal;
  private ExecutionStatus testStatus;  // Use enum type
  private Instant testStartTime;
  private Instant testCompletionTime;
  private boolean testAllowFailure;

  @BeforeEach
  void setUp() {
    jobExecutionMapper = new JobExecutionMapper();

    // Initialize test data
    testId = UUID.randomUUID();
    testStageExecutionId = UUID.randomUUID();
    testJobId = UUID.randomUUID();
    testCommitHash = "abc123";
    testIsLocal = true;
    testStatus = ExecutionStatus.SUCCESS;  // Use enum value
    testStartTime = Instant.now().minusSeconds(3600);
    testCompletionTime = Instant.now();
    testAllowFailure = false;
  }

  @Test
  void testToDTOWithValidEntity() {
    // Arrange
    JobExecutionEntity entity = new JobExecutionEntity();
    entity.setId(testId);
    entity.setStageExecutionId(testStageExecutionId);
    entity.setJobId(testJobId);
    entity.setCommitHash(testCommitHash);
    entity.setLocal(testIsLocal);
    entity.setStatus(testStatus);
    entity.setStartTime(testStartTime);
    entity.setCompletionTime(testCompletionTime);
    entity.setAllowFailure(testAllowFailure);

    // Act
    JobExecutionDTO dto = jobExecutionMapper.toDTO(entity);

    // Assert
    assertNotNull(dto);
    assertEquals(testId, dto.getId());
    assertEquals(testStageExecutionId, dto.getStageExecutionId());
    assertEquals(testJobId, dto.getJobId());
    assertEquals(testCommitHash, dto.getCommitHash());
    assertEquals(testIsLocal, dto.isLocal());
    assertEquals(testStatus, dto.getStatus());
    assertEquals(testStartTime, dto.getStartTime());
    assertEquals(testCompletionTime, dto.getCompletionTime());
    assertEquals(testAllowFailure, dto.isAllowFailure());
  }

  @Test
  void testToDTOWithNullEntity() {
    // Act
    JobExecutionDTO dto = jobExecutionMapper.toDTO(null);

    // Assert
    assertNull(dto);
  }

  @Test
  void testToEntityWithValidDTO() {
    // Arrange
    JobExecutionDTO dto = new JobExecutionDTO();
    dto.setId(testId);
    dto.setStageExecutionId(testStageExecutionId);
    dto.setJobId(testJobId);
    dto.setCommitHash(testCommitHash);
    dto.setLocal(testIsLocal);
    dto.setStatus(testStatus);
    dto.setStartTime(testStartTime);
    dto.setCompletionTime(testCompletionTime);
    dto.setAllowFailure(testAllowFailure);

    // Act
    JobExecutionEntity entity = jobExecutionMapper.toEntity(dto);

    // Assert
    assertNotNull(entity);
    assertEquals(testId, entity.getId());
    assertEquals(testStageExecutionId, entity.getStageExecutionId());
    assertEquals(testJobId, entity.getJobId());
    assertEquals(testCommitHash, entity.getCommitHash());
    assertEquals(testIsLocal, entity.isLocal());
    assertEquals(testStatus, entity.getStatus());
    assertEquals(testStartTime, entity.getStartTime());
    assertEquals(testCompletionTime, entity.getCompletionTime());
    assertEquals(testAllowFailure, entity.isAllowFailure());
  }

  @Test
  void testToEntityWithNullDTO() {
    // Act
    JobExecutionEntity entity = jobExecutionMapper.toEntity(null);

    // Assert
    assertNull(entity);
  }

  @Test
  void testBidirectionalMapping() {
    // Arrange
    JobExecutionEntity originalEntity = new JobExecutionEntity();
    originalEntity.setId(testId);
    originalEntity.setStageExecutionId(testStageExecutionId);
    originalEntity.setJobId(testJobId);
    originalEntity.setCommitHash(testCommitHash);
    originalEntity.setLocal(testIsLocal);
    originalEntity.setStatus(testStatus);
    originalEntity.setStartTime(testStartTime);
    originalEntity.setCompletionTime(testCompletionTime);
    originalEntity.setAllowFailure(testAllowFailure);

    // Act
    JobExecutionDTO dto = jobExecutionMapper.toDTO(originalEntity);
    JobExecutionEntity convertedEntity = jobExecutionMapper.toEntity(dto);

    // Assert
    assertNotNull(convertedEntity);
    assertEquals(originalEntity.getId(), convertedEntity.getId());
    assertEquals(originalEntity.getStageExecutionId(), convertedEntity.getStageExecutionId());
    assertEquals(originalEntity.getJobId(), convertedEntity.getJobId());
    assertEquals(originalEntity.getCommitHash(), convertedEntity.getCommitHash());
    assertEquals(originalEntity.isLocal(), convertedEntity.isLocal());
    assertEquals(originalEntity.getStatus(), convertedEntity.getStatus());
    assertEquals(originalEntity.getStartTime(), convertedEntity.getStartTime());
    assertEquals(originalEntity.getCompletionTime(), convertedEntity.getCompletionTime());
    assertEquals(originalEntity.isAllowFailure(), convertedEntity.isAllowFailure());
  }

  @Test
  void testToDTOWithPartialEntity() {
    // Arrange - Entity with some null fields
    JobExecutionEntity entity = new JobExecutionEntity();
    entity.setId(testId);
    entity.setStageExecutionId(testStageExecutionId);
    entity.setJobId(testJobId);
    // commitHash is null
    entity.setLocal(testIsLocal);
    entity.setStatus(testStatus);
    // startTime is null
    entity.setCompletionTime(testCompletionTime);
    entity.setAllowFailure(testAllowFailure);

    // Act
    JobExecutionDTO dto = jobExecutionMapper.toDTO(entity);

    // Assert
    assertNotNull(dto);
    assertEquals(testId, dto.getId());
    assertEquals(testStageExecutionId, dto.getStageExecutionId());
    assertEquals(testJobId, dto.getJobId());
    assertNull(dto.getCommitHash());
    assertEquals(testIsLocal, dto.isLocal());
    assertEquals(testStatus, dto.getStatus());
    assertNull(dto.getStartTime());
    assertEquals(testCompletionTime, dto.getCompletionTime());
    assertEquals(testAllowFailure, dto.isAllowFailure());
  }

  @Test
  void testToEntityWithPartialDTO() {
    // Arrange - DTO with some null fields
    JobExecutionDTO dto = new JobExecutionDTO();
    dto.setId(testId);
    dto.setStageExecutionId(testStageExecutionId);
    dto.setJobId(testJobId);
    // commitHash is null
    dto.setLocal(testIsLocal);
    // status is null
    dto.setStartTime(testStartTime);
    // completionTime is null
    dto.setAllowFailure(testAllowFailure);

    // Act
    JobExecutionEntity entity = jobExecutionMapper.toEntity(dto);

    // Assert
    assertNotNull(entity);
    assertEquals(testId, entity.getId());
    assertEquals(testStageExecutionId, entity.getStageExecutionId());
    assertEquals(testJobId, entity.getJobId());
    assertNull(entity.getCommitHash());
    assertEquals(testIsLocal, entity.isLocal());
    assertNull(entity.getStatus());
    assertEquals(testStartTime, entity.getStartTime());
    assertNull(entity.getCompletionTime());
    assertEquals(testAllowFailure, entity.isAllowFailure());
  }
}