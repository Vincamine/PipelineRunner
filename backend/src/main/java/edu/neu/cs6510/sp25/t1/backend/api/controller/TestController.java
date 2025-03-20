package edu.neu.cs6510.sp25.t1.backend.api.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import edu.neu.cs6510.sp25.t1.common.dto.JobDTO;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.backend.service.JobExecutionService;
import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.JobEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Test Controller - For manually sending jobs to the queue for testing
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final RabbitTemplate rabbitTemplate;
    private final JobRepository jobRepository;
    private final JobExecutionRepository jobExecutionRepository;

    @Value("${cicd.rabbitmq.job-queue}")
    private String jobQueueName;

    /**
     * Creates and sends a test job to the RabbitMQ queue
     * Now saves the job to the database first and then sends only the UUID to the queue
     *
     * @param request Request containing working directory and Docker image information
     * @return Response entity
     */
    @PostMapping("/send-job")
    public ResponseEntity<?> sendTestJob(@RequestBody TestJobRequest request) {
        log.info("Creating test job with working directory: {} and docker image: {}",
                request.getWorkingDir(), request.getDockerImage());

        try {
//            // Create and save the test job to the database
//            UUID jobExecutionId = createAndSaveTestJob(request);
            UUID jobExecutionId = UUID.fromString("99e1fb05-914e-4200-b27c-278ab076043d");
            // Send only the UUID to the RabbitMQ queue
            rabbitTemplate.convertAndSend(jobQueueName, jobExecutionId.toString());

            return ResponseEntity.ok()
                    .body(String.format("Test job sent to queue '%s' with ID: %s",
                            jobQueueName, jobExecutionId));
        } catch (Exception e) {
            log.error("Failed to send test job to queue: {}", e.getMessage(), e);
            throw e;
//            return ResponseEntity.internalServerError()
//                    .body("Failed to send test job: " + e.getMessage());
        }
    }

    /**
     * Creates a test job and saves it to the database
     *
     * @param request Test job request
     * @return The UUID of the created job execution
     */
    private UUID createAndSaveTestJob(TestJobRequest request) {
        // Create and save the Job entity first
        JobEntity jobEntity = new JobEntity();
        jobEntity.setId(UUID.randomUUID());
        jobEntity.setName("test-job");
        jobEntity.setDockerImage(request.getDockerImage());
        jobEntity.setScript(Arrays.asList("echo 'Hello from test job'", "ls -la", "echo 'Test job complete'"));
        jobEntity.setWorkingDir(request.getWorkingDir());
        jobEntity.setAllowFailure(false);
        jobEntity.setCreatedAt(Instant.now());
        jobEntity.setUpdatedAt(Instant.now());

        jobRepository.save(jobEntity);

        // Create and save the JobExecution entity
        JobExecutionEntity jobExecutionEntity = new JobExecutionEntity();
        jobExecutionEntity.setId(UUID.randomUUID());
        jobExecutionEntity.setJobId(jobEntity.getId());
        jobExecutionEntity.setStatus(ExecutionStatus.PENDING);
        jobExecutionEntity.setStartTime(Instant.now());
        jobExecutionEntity.setLocal(true);
        jobExecutionEntity.setAllowFailure(false);

        jobExecutionRepository.save(jobExecutionEntity);

        log.info("Created test job execution with ID: {}", jobExecutionEntity.getId());

        return jobExecutionEntity.getId();
    }

    /**
     * Test job request class
     */
    public static class TestJobRequest {
        private String workingDir;
        private String dockerImage;

        public String getWorkingDir() {
            return workingDir;
        }

        public void setWorkingDir(String workingDir) {
            this.workingDir = workingDir;
        }

        public String getDockerImage() {
            return dockerImage;
        }

        public void setDockerImage(String dockerImage) {
            this.dockerImage = dockerImage;
        }
    }
}