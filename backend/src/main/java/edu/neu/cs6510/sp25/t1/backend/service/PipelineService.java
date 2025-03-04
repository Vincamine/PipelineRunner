package edu.neu.cs6510.sp25.t1.backend.service;

import edu.neu.cs6510.sp25.t1.backend.database.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing pipelines.
 */
@Service
public class PipelineService {

  private final PipelineRepository pipelineRepository;

  public PipelineService(PipelineRepository pipelineRepository) {
    this.pipelineRepository = pipelineRepository;
  }

  /**
   * Retrieves all pipelines.
   *
   * @return List of all pipelines as DTOs.
   */
  public List<PipelineDTO> getAllPipelines() {
    return pipelineRepository.findAll().stream()
            .map(PipelineDTO::fromEntity)
            .collect(Collectors.toList());
  }

  /**
   * Retrieves a pipeline by its name.
   *
   * @param pipelineName The pipeline name.
   * @return The pipeline DTO.
   */
  public PipelineDTO getPipelineByName(String pipelineName) {
    PipelineEntity pipelineEntity = pipelineRepository.findByName(pipelineName)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline not found: " + pipelineName));

    return PipelineDTO.fromEntity(pipelineEntity);
  }

  /**
   * Creates a new pipeline.
   *
   * @param pipelineDTO The pipeline DTO containing pipeline details.
   * @return The saved pipeline DTO.
   */
  @Transactional
  public PipelineDTO createPipeline(PipelineDTO pipelineDTO) {
    PipelineEntity pipeline = new PipelineEntity(pipelineDTO.getName());
    pipeline.setRepositoryUrl(pipelineDTO.getRepositoryUrl());

    return PipelineDTO.fromEntity(pipelineRepository.save(pipeline));
  }

  /**
   * Updates an existing pipeline.
   *
   * @param pipelineName The name of the pipeline.
   * @param updatedPipelineDTO The updated pipeline DTO.
   * @return The updated pipeline DTO.
   */
  @Transactional
  public PipelineDTO updatePipeline(String pipelineName, PipelineDTO updatedPipelineDTO) {
    PipelineEntity existingPipeline = pipelineRepository.findByName(pipelineName)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline not found: " + pipelineName));

    existingPipeline.setRepositoryUrl(updatedPipelineDTO.getRepositoryUrl());

    return PipelineDTO.fromEntity(pipelineRepository.save(existingPipeline));
  }

  /**
   * Deletes a pipeline by its name.
   *
   * @param pipelineName The pipeline name.
   */
  @Transactional
  public void deletePipeline(String pipelineName) {
    pipelineRepository.deleteById(pipelineName);
  }
}
