package edu.neu.cs6510.sp25.t1.executor;

import edu.neu.cs6510.sp25.t1.client.BackendClient;
import edu.neu.cs6510.sp25.t1.model.execution.JobExecution;

import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JobExecutor {

    private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);

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

        logExecution(jobExecution);
    }

    private void logExecution(JobExecution jobExecution) {
        try (FileWriter file = new FileWriter("job-executions.log", true)) {
            String logEntry = String.format(
                    "{ \"jobName\": \"%s\", \"status\": \"%s\", \"startTime\": \"%s\", \"endTime\": \"%s\" }\n",
                    jobExecution.getJobName(),
                    jobExecution.getStatus(),
                    jobExecution.getStartTime(),
                    jobExecution.getCompletionTime()
            );
            file.write(logEntry);
            logger.info("Job execution logged: {}", logEntry);
        } catch (IOException e) {
            logger.error("Failed to write job execution log", e);
        }
    }

    public String getImage(JobExecution jobExecution) {
        return jobExecution.getJobDefinition().getImage();
    }
}
