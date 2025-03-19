package edu.neu.cs6510.sp25.t1.backend.mapper;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobEntity;
import edu.neu.cs6510.sp25.t1.common.dto.JobDTO;
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
  /**
   * Converts a JobExecutionEntity and JobEntity to a JobExecutionDTO.
   * This method creates a DTO that includes both execution data and job details.
   *
   * @param executionEntity the job execution entity
   * @param jobEntity the job entity with details
   * @return the combined job execution DTO with job details
   */
  public JobExecutionDTO toJobExecutionDto(JobExecutionEntity executionEntity, JobEntity jobEntity) {
    if (executionEntity == null) {
      return null;
    }

    JobExecutionDTO dto = toDTO(executionEntity);

    // Add job details if available
    if (jobEntity != null) {
      dto.setJob(toJobDto(jobEntity));
    }

    return dto;
  }

  /**
   * Converts a JobEntity to a JobDTO.
   *
   * @param entity the job entity to convert
   * @return the corresponding job DTO
   */
  public JobDTO toJobDto(JobEntity entity) {
    if (entity == null) {
      return null;
    }

    return JobDTO.builder()
            .id(entity.getId())
            .stageId(entity.getStageId())
            .name(entity.getName())
            .dockerImage(entity.getDockerImage())
            .script(entity.getScript())
            .dependencies(entity.getDependencies())
            .allowFailure(entity.isAllowFailure())
            .artifacts(entity.getArtifacts())
            // Handle LocalDateTime to Instant conversion if needed
            .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : null)
            .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : null)
            .build();
  }
}