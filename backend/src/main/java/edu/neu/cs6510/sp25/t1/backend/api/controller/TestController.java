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

    @Value("${cicd.rabbitmq.job-queue}")
    private String jobQueueName;

    /**
     * Creates and sends a test job to the RabbitMQ queue
     *
     * @param request Request containing working directory and Docker image information
     * @return Response entity
     */
    @PostMapping("/send-job")
    public ResponseEntity<?> sendTestJob(@RequestBody TestJobRequest request) {
        log.info("Creating test job with working directory: {} and docker image: {}",
                request.getWorkingDir(), request.getDockerImage());

        try {
            // Create a test job
            JobExecutionDTO jobExecution = createTestJob(request);

            // Send to RabbitMQ queue
            rabbitTemplate.convertAndSend(jobQueueName, jobExecution);

            return ResponseEntity.ok()
                    .body(String.format("Test job sent to queue '%s' with ID: %s",
                            jobQueueName, jobExecution.getId()));
        } catch (Exception e) {
            log.error("Failed to send test job to queue: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Failed to send test job: " + e.getMessage());
        }
    }

    /**
     * Creates a test job
     *
     * @param request Test job request
     * @return Test job DTO
     */
    private JobExecutionDTO createTestJob(TestJobRequest request) {
        // Create a JobDTO
        JobDTO jobDto = JobDTO.builder()
                .id(UUID.randomUUID())
                .name("test-job")
                .dockerImage(request.getDockerImage())
                .script(Arrays.asList("echo 'Hello from test job'", "ls -la", "echo 'Test job complete'"))
                .workingDir(request.getWorkingDir())
                .allowFailure(false)
                .build();

        // Create a JobExecutionDTO
        return JobExecutionDTO.builder()
                .id(UUID.randomUUID())
                .jobId(jobDto.getId())
                .status(ExecutionStatus.PENDING)
                .startTime(Instant.now())
                .isLocal(true)
                .job(jobDto)
                .build();
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