package edu.neu.cs6510.sp25.t1.scheduler;

import edu.neu.cs6510.sp25.t1.client.WorkerClient;
import edu.neu.cs6510.sp25.t1.model.execution.JobExecution;
import edu.neu.cs6510.sp25.t1.model.definition.JobDefinition; // Import JobDefinition
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.*;

public class JobSchedulerTest {
    private JobScheduler scheduler;
    private WorkerClient workerClient;

    @BeforeEach
    void setUp() {
        workerClient = Mockito.mock(WorkerClient.class);
        scheduler = new JobScheduler(workerClient);
    }

    @Test
    void testAddJob() {
        JobDefinition jobDef = new JobDefinition(
            "job-123",                     // Job Name
            "build",                        // Stage Name
            "gradle:8.12-jdk21",            // Docker Image
            List.of("./gradlew build"),     // Script
            List.of(),                      // Needs (dependencies)
            false                           // Allow Failure
        );

        JobExecution job = new JobExecution(jobDef, "RUNNING", false, List.of());
        scheduler.addJob(job);
        scheduler.processJobs();

        verify(workerClient, times(1)).sendJob(job);
    }

    @Test
    void testProcessJobs_Multiple() {
        JobDefinition jobDef1 = new JobDefinition(
            "job-001", "build", "gradle:8.12-jdk21",
            List.of("./gradlew compile"), List.of(), false
        );

        JobDefinition jobDef2 = new JobDefinition(
            "job-002", "test", "gradle:8.12-jdk21",
            List.of("./gradlew test"), List.of("job-001"), false
        );

        JobExecution job1 = new JobExecution(jobDef1, "PENDING", false, List.of());
        JobExecution job2 = new JobExecution(jobDef2, "PENDING", false, List.of());

        scheduler.addJob(job1);
        scheduler.addJob(job2);
        scheduler.processJobs();

        verify(workerClient, times(1)).sendJob(job1);
        verify(workerClient, times(1)).sendJob(job2);
    }
}
