package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.neu.cs6510.sp25.t1.backend.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.repository.PipelineRepository;
import edu.neu.cs6510.sp25.t1.common.runtime.PipelineRunState;

/**
 * Service for managing pipeline execution reports.
 */
@Service
public class ReportService {
  private final PipelineRepository pipelineRepository;
  private final Map<String, List<PipelineRunState>> pipelineExecutions = new HashMap<>();

  public ReportService(PipelineRepository pipelineRepository) {
    this.pipelineRepository = pipelineRepository;
  }

  /**
   * Retrieves all pipelines.
   *
   * @return A list of pipeline DTOs.
   */
  public List<PipelineDTO> getAllPipelines() {
    return pipelineRepository.findAll().stream()
            .map(PipelineDTO::fromEntity)
            .collect(Collectors.toList());
  }

  /**
   * Retrieves executions of a specific pipeline.
   *
   * @param pipelineName The pipeline name.
   * @return A list of pipeline executions converted to DTOs.
   */
  public List<PipelineDTO> getPipelineExecutions(String pipelineName) {
    return pipelineExecutions.getOrDefault(pipelineName, Collections.emptyList())
            .stream()
            .map(exec -> new PipelineDTO(exec.getPipelineName(), List.of()))
            .collect(Collectors.toList());
  }

  /**
   * Retrieves the latest execution of a pipeline.
   *
   * @param pipelineName The pipeline name.
   * @return The latest pipeline execution converted to a DTO.
   */
  public PipelineDTO getLatestPipelineRun(String pipelineName) {
    return pipelineExecutions.getOrDefault(pipelineName, Collections.emptyList())
            .stream()
            .findFirst()
            .map(exec -> new PipelineDTO(exec.getPipelineName(), List.of()))
            .orElse(null);
  }
}
