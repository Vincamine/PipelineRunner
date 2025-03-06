package edu.neu.cs6510.sp25.t1.backend.service;

import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for generating execution reports.
 */
@Service
@RequiredArgsConstructor
public class ReportService {
  private final PipelineExecutionRepository pipelineExecutionRepository;

  /**
   * Retrieves a summary of all past pipeline executions.
   */
  public List<ExecutionReportDTO> getAllPipelineExecutions() {
    return pipelineExecutionRepository.findAll()
            .stream()
            .map(pipeline -> new ExecutionReportDTO(
                    pipeline.getId(),
                    pipeline.getRunNumber(),
                    pipeline.getCommitHash(),
                    pipeline.getStatus(),
                    pipeline.getStartTime(),
                    pipeline.getCompletionTime()))
            .toList();
  }
}
