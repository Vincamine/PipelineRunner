package edu.neu.cs6510.sp25.t1.backend.mapper;



import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;

import org.springframework.stereotype.Component;

/**
 * Mapper for converting between JobExecutionEntity and JobExecutionDTO.
 */
@Component
public class JobExecutionMapper {

  /**
   * Converts a JobExecutionEntity to JobExecutionDTO.
   *
   * @param entity the entity to convert
   * @return the corresponding DTO
   */
  public JobExecutionDTO toDTO(JobExecutionEntity entity) {
    return JobExecutionDTO.builder()
            .id(entity.getId())
            .stageExecutionId(entity.getStageExecutionId())
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
   * @return the corresponding entity
   */
  public JobExecutionEntity toEntity(JobExecutionDTO dto) {
    return JobExecutionEntity.builder()
            .id(dto.getId())
            .stageExecutionId(dto.getStageExecutionId())
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
