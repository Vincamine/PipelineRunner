package edu.neu.cs6510.sp25.t1.common.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTest {

    private UUID pipelineId;
    private Job job1;
    private Job job2;
    private Stage stage1;
    private Stage stage2;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @BeforeEach
    void setUp() {
        pipelineId = UUID.randomUUID();
        createdAt = LocalDateTime.now().minusDays(1);
        updatedAt = LocalDateTime.now();

        job1 = new Job(
                UUID.randomUUID(),
                pipelineId,
                "Job1",
                "Build",
                "echo Build",
                "ubuntu:latest",
                null,
                null,
                true,
                false,
                List.of(),
                "success",
                "Job completed",
                createdAt,
                updatedAt
        );

        job2 = new Job(
                UUID.randomUUID(),
                pipelineId,
                "Job2",
                "Test",
                "echo Test",
                "ubuntu:latest",
                null,
                null,
                true,
                false,
                List.of("Job1"),
                "success",
                "Job completed",
                createdAt,
                updatedAt
        );

        stage1 = new Stage(
                UUID.randomUUID(),
                "Build",
                pipelineId,
                1,
                List.of(job1),
                createdAt,
                updatedAt
        );

        stage2 = new Stage(
                UUID.randomUUID(),
                "Test",
                pipelineId,
                2,
                List.of(job2),
                createdAt,
                updatedAt
        );
    }

    @Test
    void testGetAllJobsFromStages() {
        Pipeline pipeline = new Pipeline(
                pipelineId,
                "Pipeline A",
                "https://github.com/example/repo",
                "main",
                null,
                List.of(stage1, stage2),
                null,
                createdAt,
                updatedAt
        );

        List<Job> allJobs = pipeline.getAllJobs();

        assertEquals(2, allJobs.size());
        assertTrue(allJobs.contains(job1));
        assertTrue(allJobs.contains(job2));
    }

    @Test
    void testGetAllJobsFromTopLevelJobs() {
        Pipeline pipeline = new Pipeline(
                pipelineId,
                "Pipeline B",
                "https://github.com/example/repo",
                "main",
                null,
                null,
                List.of(job1, job2),
                createdAt,
                updatedAt
        );

        List<Job> allJobs = pipeline.getAllJobs();

        assertEquals(2, allJobs.size());
        assertTrue(allJobs.contains(job1));
        assertTrue(allJobs.contains(job2));
    }

    @Test
    void testGetJobsByStageFromStages() {
        Pipeline pipeline = new Pipeline(
                pipelineId,
                "Pipeline C",
                "https://github.com/example/repo",
                "main",
                null,
                List.of(stage1, stage2),
                null,
                createdAt,
                updatedAt
        );

        Map<String, List<Job>> jobsByStage = pipeline.getJobsByStage();

        assertEquals(2, jobsByStage.size());
        assertEquals(List.of(job1), jobsByStage.get("Build"));
        assertEquals(List.of(job2), jobsByStage.get("Test"));
    }

}
