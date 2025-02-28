package edu.neu.cs6510.sp25.t1.service;

import edu.neu.cs6510.sp25.t1.model.execution.PipelineExecution;
import edu.neu.cs6510.sp25.t1.model.execution.StageExecution;
import edu.neu.cs6510.sp25.t1.model.execution.JobExecution;
import edu.neu.cs6510.sp25.t1.model.PipelineState;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PipelineExecutionService {
    private final Map<String, PipelineExecution> executionStore = new ConcurrentHashMap<>();

    public PipelineExecution startPipeline(String pipelineId, String pipelineName) {
        PipelineExecution execution = new PipelineExecution(pipelineId, List.of(), List.of());
        executionStore.put(execution.getPipelineId(), execution);
        return execution;
    }

    public PipelineExecution getPipelineExecution(String pipelineId) {
        return executionStore.get(pipelineId);
    }

    public void updatePipelineStatus(String pipelineId, PipelineState state) {
        PipelineExecution execution = executionStore.get(pipelineId);
        if (execution != null) {
            execution.updateState();
        }
    }
}
