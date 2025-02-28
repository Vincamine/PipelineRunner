package edu.neu.cs6510.sp25.t1.executor;


// package edu.neu.cs6510.sp25.t1.execution;

// import java.util.List;

// /**
//  * Executes a full CI/CD pipeline by running all stages sequentially inside a dedicated Docker container.
//  */
// public class PipelineExecutor {
//   private final String pipelineName;
//   private final List<StageExecutor> stages;
//   private ExecutionStatus status;
//   private final DockerRunner dockerRunner;
//   private String containerId;

//   /**
//    * Constructor to initialize PipelineExecutor with the specified parameters.
//    * @param pipelineName The name of the pipeline.
//    * @param stages The list of stages to execute.
//    * @param dockerRunner The DockerRunner instance to manage Docker containers.
//    * 
//    */
//   public PipelineExecutor(String pipelineName, List<StageExecutor> stages, DockerRunner dockerRunner) {
//     this.pipelineName = pipelineName;
//     this.stages = stages;
//     this.dockerRunner = dockerRunner;
//     this.status = ExecutionStatus.PENDING;
//   }

//   /**
//    * Executes all stages in the pipeline sequentially inside a Docker container.
//    * If any stage fails, the pipeline stops and is marked as FAILED.
//    */
//   public void execute() {
//     System.out.println("Starting pipeline: " + pipelineName);
//     status = ExecutionStatus.RUNNING;

//     // Start pipeline execution inside a dedicated container
//     containerId = dockerRunner.startContainer("/bin/sh", "-c", "echo Running pipeline inside container");

//     for (StageExecutor stage : stages) {
//       stage.execute();
//       if (stage.getStatus() == ExecutionStatus.FAILED) {
//         status = ExecutionStatus.FAILED;
//         System.out.println("Pipeline " + pipelineName + " failed due to stage failure.");
//         return;
//       }
//     }

//     // Mark pipeline as successful after all stages complete
//     status = ExecutionStatus.SUCCESSFUL;
//     System.out.println("Pipeline " + pipelineName + " completed successfully inside container: " + containerId);
//   }

//   /**
//    * Getter: Returns the status of the pipeline execution.
//    * @return The status of the pipeline
//    */
//   public ExecutionStatus getStatus() {
//     return status;
//   }
// }
