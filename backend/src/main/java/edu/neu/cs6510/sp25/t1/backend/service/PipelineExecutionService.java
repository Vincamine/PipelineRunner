package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;

import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;
import edu.neu.cs6510.sp25.t1.common.model.definition.JobDefinition;
import edu.neu.cs6510.sp25.t1.common.model.definition.StageDefinition;
import edu.neu.cs6510.sp25.t1.common.model.execution.JobExecution;
import edu.neu.cs6510.sp25.t1.common.model.execution.PipelineExecution;
import edu.neu.cs6510.sp25.t1.common.model.execution.StageExecution;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PipelineExecutionService {
    private final Map<String, PipelineExecution> executionStore = new ConcurrentHashMap<>();

    public PipelineExecution startPipeline(String pipelineName) { // Removed pipelineId, only need name
        List<JobDefinition> jobDefinitions = List.of(
                new JobDefinition("job1", pipelineName, "default-image", List.of(), List.of(), false));

        List<JobExecution> jobs = jobDefinitions.stream()
                .map(def -> new JobExecution(def, "PENDING", false, List.of()))
                .collect(Collectors.toList());

        List<StageExecution> stages = List.of(
                new StageExecution(new StageDefinition(pipelineName, jobDefinitions), jobs));

        PipelineExecution execution = new PipelineExecution(pipelineName, stages, jobs);
        executionStore.put(execution.getPipelineName(), execution);
        return execution;
    }

    public PipelineExecution getPipelineExecution(String pipelineName) { // Use name instead of ID
        return executionStore.get(pipelineName);
    }

    public void updatePipelineStatus(String pipelineName, ExecutionState state) {
        PipelineExecution execution = executionStore.get(pipelineName);
        if (execution != null) {
            execution.updateState();
        }
    }
}