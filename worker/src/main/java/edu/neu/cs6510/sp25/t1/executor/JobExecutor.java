package edu.neu.cs6510.sp25.t1.executor;

import edu.neu.cs6510.sp25.t1.client.BackendClient;
import edu.neu.cs6510.sp25.t1.model.execution.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobExecutor {
    
    private final DockerManager dockerManager;
    private final BackendClient backendClient;

    @Autowired
    public JobExecutor(DockerManager dockerManager, BackendClient backendClient) {
        this.dockerManager = dockerManager;
        this.backendClient = backendClient;
    }

    public void executeJob(JobExecution jobExecution) {
        String containerId = dockerManager.runContainer(jobExecution);
        
        if (containerId != null) {
            backendClient.sendJobStatus(jobExecution.getJobName(), "RUNNING");
            
            boolean success = dockerManager.waitForContainer(containerId);
            
            if (success) {
                backendClient.sendJobStatus(jobExecution.getJobName(), "SUCCESS");
            } else {
                backendClient.sendJobStatus(jobExecution.getJobName(), "FAILED");
            }
            
            dockerManager.cleanupContainer(containerId);
        } else {
            backendClient.sendJobStatus(jobExecution.getJobName(), "FAILED");
        }
    }

    // ✅ Fix: Get image from `jobExecution.getJobDefinition()`
    public String getImage(JobExecution jobExecution) {
        return jobExecution.getJobDefinition().getImage();
    }
}
