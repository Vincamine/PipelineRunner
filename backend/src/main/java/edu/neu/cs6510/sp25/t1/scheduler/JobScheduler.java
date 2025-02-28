package edu.neu.cs6510.sp25.t1.scheduler;


import edu.neu.cs6510.sp25.t1.model.execution.JobExecution;
import edu.neu.cs6510.sp25.t1.client.WorkerClient;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class JobScheduler {
    private final WorkerClient workerClient;
    private final Queue<JobExecution> jobQueue = new ConcurrentLinkedQueue<>();

    public JobScheduler(WorkerClient workerClient) {
        this.workerClient = workerClient;
    }

    public void addJob(JobExecution job) {
        jobQueue.offer(job);
    }

    public void processJobs() {
        while (!jobQueue.isEmpty()) {
            JobExecution job = jobQueue.poll();
            workerClient.sendJob(job);
        }
    }
}
