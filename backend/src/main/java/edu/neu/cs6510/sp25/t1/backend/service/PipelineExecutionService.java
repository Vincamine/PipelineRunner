package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;
import edu.neu.cs6510.sp25.t1.backend.dto.PipelineDTO;
import edu.neu.cs6510.sp25.t1.backend.repository.PipelineRepository;
import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.model.execution.PipelineExecution;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;


/**
 * Service for managing pipeline execution.
 */
@Service
public class PipelineExecutionService {
    private final PipelineRepository pipelineRepository;
    private final Map<String, PipelineExecution> executionStore = new ConcurrentHashMap<>();

    public PipelineExecutionService(PipelineRepository pipelineRepository) {
        this.pipelineRepository = pipelineRepository;
    }

    /**
     * Starts a pipeline execution and returns a DTO.
     *
     * @param pipelineName The name of the pipeline.
     * @return A DTO representing the started pipeline.
     */
    public Optional<PipelineDTO> startPipeline(String pipelineName) {
        return pipelineRepository.findById(pipelineName).map(PipelineDTO::fromEntity);
    }

    /**
     * Gets the status of a pipeline execution as a DTO.
     *
     * @param pipelineName The pipeline name.
     * @return A PipelineDTO representing the execution status.
     */
    public Optional<PipelineDTO> getPipelineExecution(String pipelineName) {
        return Optional.ofNullable(executionStore.get(pipelineName))
                .map(execution -> new PipelineDTO(execution.getPipelineName(), List.of()));
    }

    /**
     * Updates the execution state of a pipeline.
     *
     * @param pipelineName The pipeline name.
     * @param state        The new execution state.
     */
    public void updatePipelineStatus(String pipelineName, ExecutionState state) {
        executionStore.computeIfPresent(pipelineName, (key, execution) -> {
            execution.updateState();  // Update the execution state
            return execution;
        });
    }
}
