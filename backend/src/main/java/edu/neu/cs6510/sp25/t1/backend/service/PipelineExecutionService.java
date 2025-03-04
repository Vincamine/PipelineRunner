package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.backend.database.dto.PipelineExecutionDTO;
import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.PipelineExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.PipelineRepository;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

@Service
public class PipelineExecutionService {

  private final PipelineExecutionRepository pipelineExecutionRepository;
  private final PipelineRepository pipelineRepository;

  @Autowired
  public PipelineExecutionService(
          PipelineExecutionRepository pipelineExecutionRepository,
          PipelineRepository pipelineRepository) {
    this.pipelineExecutionRepository = pipelineExecutionRepository;
    this.pipelineRepository = pipelineRepository;
  }

  @Transactional
  public PipelineExecutionDTO startPipelineExecution(String pipelineName, String commitHash) {
    String runId = UUID.randomUUID().toString();

    // ✅ Retrieve PipelineEntity before creating PipelineExecutionEntity
    PipelineEntity pipeline = pipelineRepository.findByName(pipelineName)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline not found: " + pipelineName));

    // ✅ Create PipelineExecutionEntity using retrieved PipelineEntity
    PipelineExecutionEntity pipelineExecution = new PipelineExecutionEntity(
            pipeline,
            runId,
            commitHash,
            ExecutionStatus.RUNNING,
            Instant.now()
    );

    pipelineExecutionRepository.save(pipelineExecution);

    return PipelineExecutionDTO.fromEntity(pipelineExecution);
  }



  public List<PipelineExecutionEntity> getPipelineExecutions(String pipelineName) {
    return pipelineExecutionRepository.findByPipelineName(pipelineName);
  }

  // Fetch a specific pipeline execution
  public PipelineExecutionEntity getPipelineExecution(String pipelineName, String runId) {
    return pipelineExecutionRepository.findByPipelineNameAndRunId(pipelineName, runId)
            .orElseThrow(() -> new IllegalArgumentException("Pipeline execution not found."));
  }

  public PipelineExecutionEntity getLatestPipelineExecution(String pipelineName) {
    return pipelineExecutionRepository.findTopByPipelineNameOrderByStartTimeDesc(pipelineName)
            .orElseThrow(() -> new IllegalArgumentException("No executions found for pipeline: " + pipelineName));
  }
}
