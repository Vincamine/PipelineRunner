package edu.neu.cs6510.sp25.t1.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PipelineStatusTest {

    @Test
    void testConstructorWithStagesAndJobs() {
        // Prepare mock data for stages and jobs
        List<StageInfo> stages = List.of(
                new StageInfo("Build", "SUCCESS", Instant.now().minusSeconds(600).toEpochMilli(), Instant.now().minusSeconds(500).toEpochMilli(), List.of("Compile", "Package")),
                new StageInfo("Test", "RUNNING", Instant.now().minusSeconds(300).toEpochMilli(), 0, List.of("Unit Test", "Integration Test")) // Still running
        );

        List<JobInfo> jobs = List.of(
                new JobInfo("Compile", "SUCCESS", false),
                new JobInfo("Package", "SUCCESS", false),
                new JobInfo("Unit Test", "RUNNING", false),
                new JobInfo("Integration Test", "PENDING", false)
        );

        final PipelineStatus status = new PipelineStatus("pipeline-123", stages, jobs);

        assertEquals("pipeline-123", status.getPipelineId());
        assertNotNull(status.getStartTime());
        assertNotNull(status.getLastUpdated());
        assertEquals(stages, status.getStages());
        assertEquals(jobs, status.getJobs());
    }

    @Test
    void testConstructorWithManualState() {
        List<StageInfo> stages = List.of(
                new StageInfo("Deploy", "FAILED", Instant.now().minusSeconds(600).toEpochMilli(), Instant.now().minusSeconds(500).toEpochMilli(), List.of("Deployment"))
        );

        List<JobInfo> jobs = List.of(
                new JobInfo("Deployment", "FAILED", false)
        );

        final PipelineStatus status = new PipelineStatus("pipeline-456", PipelineState.FAILED, 30, "Deployment failed", stages, jobs);

        assertEquals("pipeline-456", status.getPipelineId());
        assertEquals(PipelineState.FAILED, status.getState());
        assertEquals(30, status.getProgress());
        assertEquals("Deployment failed", status.getMessage());
        assertEquals(stages, status.getStages());
        assertEquals(jobs, status.getJobs());
    }

    @Test
    void testProgressComputation() {
        List<StageInfo> stages = List.of(
                new StageInfo("Build", "SUCCESS", Instant.now().minusSeconds(600).toEpochMilli(), Instant.now().minusSeconds(500).toEpochMilli(), List.of("Compile")),
                new StageInfo("Test", "SUCCESS", Instant.now().minusSeconds(400).toEpochMilli(), Instant.now().minusSeconds(300).toEpochMilli(), List.of("Unit Test")),
                new StageInfo("Deploy", "FAILED", Instant.now().minusSeconds(200).toEpochMilli(), Instant.now().minusSeconds(100).toEpochMilli(), List.of("Deploy App"))
        );

        List<JobInfo> jobs = List.of(
                new JobInfo("Compile", "SUCCESS", false),
                new JobInfo("Unit Test", "SUCCESS", false),
                new JobInfo("Deploy App", "FAILED", false)
        );

        final PipelineStatus status = new PipelineStatus("pipeline-789", stages, jobs);

        assertEquals(66, status.getProgress()); // 2/3 stages succeeded → 66% progress
    }

    @Test
    void testConstructorWithBasicInfo() {
        final PipelineStatus status = new PipelineStatus("pipeline-123", PipelineState.RUNNING, 50, "Pipeline running...");

        assertEquals("pipeline-123", status.getPipelineId());
        assertEquals(PipelineState.RUNNING, status.getState());
        assertEquals(50, status.getProgress());
        assertEquals("Pipeline running...", status.getMessage());
        assertNotNull(status.getStartTime());
        assertNotNull(status.getLastUpdated());
        assertTrue(status.getStages().isEmpty()); // No stages
        assertTrue(status.getJobs().isEmpty()); // No jobs
    }

    @Test
    void testStateComputation() {
        List<StageInfo> stages = List.of(
                new StageInfo("Build", "SUCCESS", Instant.now().minusSeconds(600).toEpochMilli(), Instant.now().minusSeconds(500).toEpochMilli(), List.of("Compile")),
                new StageInfo("Deploy", "FAILED", Instant.now().minusSeconds(400).toEpochMilli(), Instant.now().minusSeconds(300).toEpochMilli(), List.of("Deploy App"))
        );

        List<JobInfo> jobs = List.of(
                new JobInfo("Compile", "SUCCESS", false),
                new JobInfo("Deploy App", "FAILED", false)
        );

        final PipelineStatus status = new PipelineStatus("pipeline-999", stages, jobs);

        assertEquals(PipelineState.FAILED, status.getState()); // Should be failed due to one failed stage & job
    }

    @Test
    void testConstructorWithNoStagesOrJobs() {
        final PipelineStatus status = new PipelineStatus("pipeline-000", List.of(), List.of());

        assertEquals("pipeline-000", status.getPipelineId());
        assertEquals(PipelineState.SUCCESS, status.getState()); // No failures, default to success
        assertEquals(0, status.getProgress()); // No stages, progress = 0%
        assertTrue(status.getStages().isEmpty());
        assertTrue(status.getJobs().isEmpty());
    }
}
