package edu.neu.cs6510.sp25.t1.worker.api.controller;

import edu.neu.cs6510.sp25.t1.worker.service.WorkerJobQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for worker status and control endpoints.
 */
@RestController
@RequestMapping("/api/worker")
@RequiredArgsConstructor
@Slf4j
public class WorkerStatusController {
    private final WorkerJobQueue jobQueue;

    /**
     * Gets the current status of the worker.
     *
     * @return Response with worker status information
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getWorkerStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("active_jobs", jobQueue.getActiveJobCount());
        status.put("status", "running");
        return ResponseEntity.ok(status);
    }

    /**
     * Manually triggers the job polling process.
     * Useful for testing or immediate job execution.
     *
     * @return Response indicating the polling has been initiated
     */
    @PostMapping("/poll")
    public ResponseEntity<Map<String, String>> forcePoll() {
        jobQueue.pollForJobs();
        Map<String, String> response = new HashMap<>();
        response.put("status", "polling_initiated");
        return ResponseEntity.ok(response);
    }
}