package edu.neu.cs6510.sp25.t1.backend.service;

import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineEntity;
import edu.neu.cs6510.sp25.t1.backend.data.repository.PipelineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
   * @return List of all pipelines.
   */
  public List<PipelineEntity> getAllPipelines() {
    return pipelineRepository.findAll();
  }

  /**
   * Retrieves a pipeline by its name.
   *
   * @param pipelineName The pipeline name.
   * @return The pipeline entity.
   */
  public PipelineEntity getPipelineByName(String pipelineName) {
    return pipelineRepository.findByName(pipelineName);
  }

  /**
   * Creates a new pipeline.
   *
   * @param pipeline The pipeline entity to create.
   * @return The saved pipeline entity.
   */
  @Transactional
  public PipelineEntity createPipeline(PipelineEntity pipeline) {
    return pipelineRepository.save(pipeline);
  }

  /**
   * Updates an existing pipeline.
   *
   * @param pipelineName The name of the pipeline.
   * @param updatedPipeline The updated pipeline entity.
   * @return The updated pipeline entity.
   */
  @Transactional
  public PipelineEntity updatePipeline(String pipelineName, PipelineEntity updatedPipeline) {
    PipelineEntity existingPipeline = getPipelineByName(pipelineName);
    existingPipeline.setName(updatedPipeline.getName());
    return pipelineRepository.save(existingPipeline);
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
