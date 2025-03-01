package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.stereotype.Service;

import edu.neu.cs6510.sp25.t1.common.model.PipelineState;
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

    public PipelineExecution startPipeline(String pipelineId, String pipelineName) {
        // 🚀 Initialize job definitions
        List<JobDefinition> jobDefinitions = List.of(
            new JobDefinition("job1", pipelineName, "default-image", List.of(), List.of(), false)
        ); 

        // 🚀 Convert job definitions into job executions
        List<JobExecution> jobs = jobDefinitions.stream()
            .map(def -> new JobExecution(def, "PENDING", false, List.of()))
            .collect(Collectors.toList());

        // 🚀 Initialize stage execution (✅ FIX: Pass `StageDefinition` with `JobDefinitions`)
        List<StageExecution> stages = List.of(
            new StageExecution(new StageDefinition(pipelineName, jobDefinitions), jobs)
        ); 

        PipelineExecution execution = new PipelineExecution(pipelineId, stages, jobs);
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
