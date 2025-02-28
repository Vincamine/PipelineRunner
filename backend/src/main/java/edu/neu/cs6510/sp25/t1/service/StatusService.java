// package edu.neu.cs6510.sp25.t1.service;

// import java.time.Instant;
// import java.util.List;

// import edu.neu.cs6510.sp25.t1.model.PipelineState;
// import edu.neu.cs6510.sp25.t1.model.execution.JobExecution;
// import edu.neu.cs6510.sp25.t1.model.execution.PipelineExecution;
// import edu.neu.cs6510.sp25.t1.model.execution.StageExecution;

// /**
//  * Service class responsible for retrieving pipeline execution status.
//  * 
//  * Mock Implementation for demo purposes.
//  * - This service simulates retrieving the pipeline status from an API.
//  * - Once the backend API is available, replace the mock logic with an actual
//  * API call.
//  */
// public class StatusService {

//         /**
//          * Retrieves the current status of a pipeline.
//          *
//          * @param pipelineId The ID of the pipeline to check.
//          * @return The current status of the pipeline (mock response).
//          * @throws RuntimeException if there's an error retrieving the status.
//          */
//         public PipelineExecution getPipelineStatus(String pipelineId) {
//                 if (pipelineId == null || pipelineId.trim().isEmpty()) {
//                         throw new IllegalArgumentException("Pipeline ID cannot be null or empty.");
//                 }

//                 List<JobExecution> buildJobs = List.of(
//                                 new JobExecution("Compile Code", "RUNNING", false),
//                                 new JobExecution("Run Tests", "PENDING", false));

//                 List<StageExecution> stages = List.of(
//                                 new StageExecution("Build", "RUNNING", Instant.now().minusSeconds(200).toEpochMilli(),
//                                                 Instant.now().toEpochMilli(), List.of("Compile Code", "Run Tests")),
//                                 new StageExecution("Test", "IN_PROGRESS", Instant.now().minusSeconds(100).toEpochMilli(),
//                                                 Instant.now().toEpochMilli(),
//                                                 List.of("Unit Tests", "Integration Tests")));

//                 System.out.println("🚀 Pipeline Status: " + pipelineId + " is currently RUNNING");

//                 return new PipelineExecution(
//                                 pipelineId,
//                                 PipelineState.RUNNING,
//                                 50,
//                                 "Deploy to Staging",
//                                 stages,
//                                 buildJobs);
//         }

// }
