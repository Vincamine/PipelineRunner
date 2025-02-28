package edu.neu.cs6510.sp25.t1.api;

import edu.neu.cs6510.sp25.t1.model.execution.JobExecution;
import edu.neu.cs6510.sp25.t1.executor.JobExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class WorkerController {

    private final JobExecutor jobExecutor;

    @Autowired
    public WorkerController(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    @PostMapping("/execute")
    public ResponseEntity<String> executeJob(@RequestBody JobExecution jobExecution) {
        if (jobExecution == null || jobExecution.getJobName() == null) {
            return ResponseEntity.badRequest().body("Error: Job name cannot be null");
        }

        try {
            jobExecutor.executeJob(jobExecution);
            return ResponseEntity.ok("Job executed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error executing job: " + e.getMessage());
        }
    }
}
