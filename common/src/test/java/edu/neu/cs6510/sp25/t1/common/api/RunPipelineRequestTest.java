package edu.neu.cs6510.sp25.t1.common.api;

import org.junit.jupiter.api.Test;

import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;

import java.util.Collections;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class RunPipelineRequestTest {

    @Test
    void testDefaultConstructor() {
        RunPipelineRequest request = new RunPipelineRequest();
        
        assertNull(request.getRepo(), "Default constructor should set repo to null.");
        assertNull(request.getBranch(), "Default constructor should set branch to null.");
        assertNull(request.getCommit(), "Default constructor should set commit to null.");
        assertNull(request.getPipeline(), "Default constructor should set pipeline to null.");
        assertFalse(request.isLocal(), "Default constructor should set local to false.");
        assertEquals(Collections.emptyMap(), request.getOverrides(), "Default constructor should set overrides to an empty map.");
    }

    @Test
    void testParameterizedConstructor() {
        RunPipelineRequest request = new RunPipelineRequest(
                "https://github.com/example/repo",
                "main",
                "abc123",
                "pipeline.yaml",
                true
        );

        assertEquals("https://github.com/example/repo", request.getRepo(), "Repo should match constructor value.");
        assertEquals("main", request.getBranch(), "Branch should match constructor value.");
        assertEquals("abc123", request.getCommit(), "Commit should match constructor value.");
        assertEquals("pipeline.yaml", request.getPipeline(), "Pipeline should match constructor value.");
        assertTrue(request.isLocal(), "Local should match constructor value.");
    }

    @Test
    void testSetRepo() {
        RunPipelineRequest request = new RunPipelineRequest();
        request.setRepo("https://github.com/example/repo");

        assertEquals("https://github.com/example/repo", request.getRepo(), "setRepo() should update the repo.");
    }

    @Test
    void testSetBranch() {
        RunPipelineRequest request = new RunPipelineRequest();
        request.setBranch("develop");

        assertEquals("develop", request.getBranch(), "setBranch() should update the branch.");
    }

    @Test
    void testSetCommit() {
        RunPipelineRequest request = new RunPipelineRequest();
        request.setCommit("xyz789");

        assertEquals("xyz789", request.getCommit(), "setCommit() should update the commit.");
    }

    @Test
    void testSetPipeline() {
        RunPipelineRequest request = new RunPipelineRequest();
        request.setPipeline("ci-pipeline.yaml");

        assertEquals("ci-pipeline.yaml", request.getPipeline(), "setPipeline() should update the pipeline.");
    }

    @Test
    void testSetLocal() {
        RunPipelineRequest request = new RunPipelineRequest();
        request.setLocal(true);

        assertTrue(request.isLocal(), "setLocal(true) should update the local flag to true.");
    }

    @Test
    void testSetOverrides() {
        RunPipelineRequest request = new RunPipelineRequest();
        Map<String, String> overrides = Map.of("key1", "value1", "key2", "value2");
        request.setOverrides(overrides);

        assertEquals(overrides, request.getOverrides(), "setOverrides() should update the overrides map.");
    }
}
