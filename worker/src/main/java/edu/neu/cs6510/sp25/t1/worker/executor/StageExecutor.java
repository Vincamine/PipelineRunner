package edu.neu.cs6510.sp25.t1.worker.executor;

import edu.neu.cs6510.sp25.t1.common.api.request.JobRequest;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.worker.execution.JobExecution;
import edu.neu.cs6510.sp25.t1.worker.execution.StageExecution;
import edu.neu.cs6510.sp25.t1.worker.api.client.WorkerBackendClient;
import edu.neu.cs6510.sp25.t1.worker.manager.DockerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * StageExecutor is responsible for executing a stage within a pipeline.
 */
@Service
public class StageExecutor {
  private final JobExecutor jobExecutor;

  /**
   * Constructor for StageExecutor.
   *
   * @param dockerManager    The DockerManager for managing job execution.
   * @param backendClient    The backend client to send execution updates.
   */
  @Autowired
  public StageExecutor(DockerManager dockerManager, WorkerBackendClient backendClient) {
    this.jobExecutor = new JobExecutor(dockerManager, backendClient);
  }

  /**
   * Executes a stage and its jobs sequentially.
   *
   * @param stageExecution The stage execution context.
   * @return ExecutionStatus representing the final stage status.
   */
  public ExecutionStatus execute(StageExecution stageExecution) {
    stageExecution.updateStatus();

    for (JobExecution job : stageExecution.getJobs()) {
      // Convert JobExecution into JobRequest before calling executeJob()
      JobRequest jobRequest = new JobRequest(
              job.getName(),           // Job name
              stageExecution.getName(), // Pipeline name (or stage name)
              job.getName(),            // Job name
              "latest",                 // Commit hash (assuming latest)
              Map.of("IMAGE", job.getImage()), // Environment variables (image)
              List.of()                  // Artifact paths (empty for now)
      );

      ExecutionStatus jobState = jobExecutor.executeJob(jobRequest);

      if (jobState == ExecutionStatus.FAILED) { // Allow failure will be implemented later
        stageExecution.updateStatus();
        return ExecutionStatus.FAILED;
      }
    }

    stageExecution.updateStatus();
    return stageExecution.getStatus();
  }
}
