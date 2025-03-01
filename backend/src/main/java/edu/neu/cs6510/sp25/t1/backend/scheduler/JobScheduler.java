package edu.neu.cs6510.sp25.t1.backend.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.neu.cs6510.sp25.t1.backend.client.WorkerClient;
import edu.neu.cs6510.sp25.t1.common.model.execution.JobExecution;

/**
 * Scheduler for processing job executions.
 */
@Service
public class JobScheduler {
  private final WorkerClient workerClient;
  private final Queue<JobExecution> jobQueue = new ConcurrentLinkedQueue<>();

  /**
   * Constructor for dependency injection.
   *
   * @param workerClient The Worker client.
   */
  public JobScheduler(WorkerClient workerClient) {
    this.workerClient = workerClient;
  }

  /**
   * Adds a job to the queue.
   *
   * @param job
   */
  public void addJob(JobExecution job) {
    jobQueue.offer(job);
  }

  /**
   * Processes jobs in the queue.
   * This method is scheduled to run every 5 seconds.
   */
  @Scheduled(fixedRate = 5000) // Process jobs every 5 seconds
  public void processJobs() {
    while (!jobQueue.isEmpty()) {
      JobExecution job = jobQueue.poll();
      workerClient.sendJob(job);
    }
  }
}
