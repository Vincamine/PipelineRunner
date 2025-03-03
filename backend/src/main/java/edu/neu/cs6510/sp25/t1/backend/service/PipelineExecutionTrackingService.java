//package edu.neu.cs6510.sp25.t1.backend.service;
//
//import edu.neu.cs6510.sp25.t1.backend.data.entity.PipelineExecutionEntity;
//import edu.neu.cs6510.sp25.t1.backend.data.repository.PipelineExecutionRepository;
//import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
//import edu.neu.cs6510.sp25.t1.worker.execution.PipelineExecution;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.Instant;
//import java.util.Optional;
//
///**
// * Service for handling pipeline execution logic.
// */
//@Service
//public class PipelineExecutionTrackingService {
//  private static final Logger logger = LoggerFactory.getLogger(PipelineExecutionTrackingService.class);
//  private final PipelineExecutionRepository pipelineExecutionRepository;
//
//  public PipelineExecutionTrackingService(PipelineExecutionRepository pipelineExecutionRepository) {
//    this.pipelineExecutionRepository = pipelineExecutionRepository;
//  }
//
//  /**
//   * Starts execution for a given pipeline.
//   *
//   * @param pipelineName The pipeline name.
//   * @return The execution summary if successful.
//   */
//  @Transactional
//  public Optional<PipelineExecution> startPipeline(String pipelineName) {
//    logger.info("Starting pipeline execution: {}", pipelineName);
//
//    PipelineExecutionEntity pipelineExecutionEntity = new PipelineExecutionEntity(pipelineName, ExecutionStatus.RUNNING, Instant.now());
//    pipelineExecutionRepository.save(pipelineExecutionEntity);
//
//    return Optional.of(convertToPipelineRunState(pipelineExecutionEntity));
//  }
//
//  /**
//   * Retrieves the execution details of a pipeline.
//   *
//   * @param pipelineName The pipeline name.
//   * @return Optional containing pipeline execution state.
//   */
//  @Transactional(readOnly = true)
//  public Optional<PipelineExecution> getPipelineExecution(String pipelineName) {
//    return pipelineExecutionRepository.findFirstByPipelineNameOrderByCreatedAtDesc(pipelineName)
//            .map(this::convertToPipelineRunState);
//  }
//
//  /**
//   * Logs pipeline execution state updates.
//   *
//   * @param pipelineName The pipeline name.
//   * @param state        The new execution state.
//   */
//  @Transactional
//  public void logPipelineExecution(String pipelineName, String state) {
//    Optional<PipelineExecutionEntity> pipelineExecutionOpt = pipelineExecutionRepository.findFirstByPipelineNameOrderByCreatedAtDesc(pipelineName);
//
//    if (pipelineExecutionOpt.isPresent()) {
//      PipelineExecutionEntity pipelineExecutionEntity = pipelineExecutionOpt.get();
//      pipelineExecutionEntity.setState(ExecutionStatus.valueOf(state));
//      pipelineExecutionRepository.save(pipelineExecutionEntity);
//      logger.info("Updated pipeline execution state: {} -> {}", pipelineName, state);
//    } else {
//      logger.warn("Pipeline execution not found: {}", pipelineName);
//    }
//  }
//
//  /**
//   * Converts a PipelineExecution entity to a PipelineRunState.
//   */
//  private PipelineExecution convertToPipelineRunState(PipelineExecutionEntity execution) {
//    return new PipelineExecution(
//            execution.getPipelineName()
//    );
//  }
//}
