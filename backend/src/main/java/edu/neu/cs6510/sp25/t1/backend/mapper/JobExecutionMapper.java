package edu.neu.cs6510.sp25.t1.backend.mapper;

import org.springframework.stereotype.Component;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.StageExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;

/**
 * Mapper for converting between JobExecutionEntity and JobExecutionDTO.
 */
@Component
public class JobExecutionMapper {

  private StageExecutionEntity stageExecutionEntity;

  /**
   * Converts a JobExecutionEntity to JobExecutionDTO.
   *
   * @param entity the entity to convert
   * @return the corresponding DTO, or null if entity is null
   */
  public JobExecutionDTO toDTO(JobExecutionEntity entity) {
    if (entity == null) {
      return null;
    }

    return JobExecutionDTO.builder()
            .id(entity.getId())
            .stageExecutionId(entity.getStageExecution().getId())
            .jobId(entity.getJobId())
            .commitHash(entity.getCommitHash())
            .isLocal(entity.isLocal())
            .status(entity.getStatus())
            .startTime(entity.getStartTime())
            .completionTime(entity.getCompletionTime())
            .allowFailure(entity.isAllowFailure())
            .build();
  }

  /**
   * Converts a JobExecutionDTO to JobExecutionEntity.
   *
   * @param dto the DTO to convert
   * @return the corresponding entity, or null if dto is null
   */
  public JobExecutionEntity toEntity(JobExecutionDTO dto) {
    if (dto == null) {
      return null;
    }

    return JobExecutionEntity.builder()
            .id(dto.getId())
            .stageExecution(StageExecutionEntity.builder().id(dto.getStageExecutionId()).build())
            .jobId(dto.getJobId())
            .commitHash(dto.getCommitHash())
            .isLocal(dto.isLocal())
            .status(dto.getStatus())
            .startTime(dto.getStartTime())
            .completionTime(dto.getCompletionTime())
            .allowFailure(dto.isAllowFailure())
            .build();
  }
}