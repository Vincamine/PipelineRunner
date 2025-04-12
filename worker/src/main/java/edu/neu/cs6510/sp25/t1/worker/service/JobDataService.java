package edu.neu.cs6510.sp25.t1.worker.service;

import edu.neu.cs6510.sp25.t1.backend.database.entity.JobEntity;
import edu.neu.cs6510.sp25.t1.backend.database.entity.JobExecutionEntity;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobExecutionRepository;
import edu.neu.cs6510.sp25.t1.backend.database.repository.JobRepository;
import edu.neu.cs6510.sp25.t1.backend.mapper.JobExecutionMapper;
import edu.neu.cs6510.sp25.t1.common.dto.JobExecutionDTO;
import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing job data directly from the database.
 * Provides methods to retrieve and update job execution information.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobDataService {
    private final JobExecutionRepository jobExecutionRepository;
    private final JobRepository jobRepository;
    private final JobExecutionMapper mapper;

//    /**
//     * Retrieves the next pending job from the database and updates its status to RUNNING.
//     *
//     * @return Optional containing the next job to execute, or empty if none found
//     */
//    @Transactional
//    public Optional<JobExecutionDTO> getNextPendingJob() {
//        // Get all pending jobs
//        List<JobExecutionEntity> pendingJobs = jobExecutionRepository.findByStatus(ExecutionStatus.PENDING);
//
//        if (pendingJobs.isEmpty()) {
//            return Optional.empty();
//        }
//
//        // Get the first pending job (oldest by default)
//        JobExecutionEntity nextJob = pendingJobs.get(0);
//
//        // Update status to RUNNING to prevent other workers from picking it up
//        nextJob.setStatus(ExecutionStatus.RUNNING);
//        nextJob = jobExecutionRepository.save(nextJob);
//
//        // Get the associated job details
//        Optional<JobEntity> jobEntity = jobRepository.findById(nextJob.getJobId());
//
//        if (jobEntity.isEmpty()) {
//            log.error("Could not find job details for execution {}", nextJob.getId());
//            return Optional.empty();
//        }
//
//        // Convert to DTO and return
//        return Optional.of(mapper.toJobExecutionDto(nextJob, jobEntity.get()));
//    }

    /**
     * Updates the job execution status and logs in the database.
     *
     * @param jobExecutionId The job execution ID
     * @param status The new execution status
     * @param logs The execution logs
     */
    @Transactional
    public void updateJobStatus(UUID jobExecutionId, ExecutionStatus status, String logs) {
        Optional<JobExecutionEntity> jobExecutionOpt = jobExecutionRepository.findById(jobExecutionId);

        if (jobExecutionOpt.isPresent()) {
            JobExecutionEntity entity = jobExecutionOpt.get();
            entity.setStatus(status);


            // Update completion time for terminal statuses
            if (status == ExecutionStatus.SUCCESS || status == ExecutionStatus.FAILED || status == ExecutionStatus.CANCELED) {
                entity.setCompletionTime(Instant.now());
            }

            jobExecutionRepository.save(entity);
            log.info("Updated job execution {} status to {}", jobExecutionId, status);
        } else {
            log.error("Could not find job execution with ID {}", jobExecutionId);
        }
    }

    /**
     * Retrieves job execution details by ID.
     *
     * @param jobExecutionId The job execution ID
     * @return Optional containing the job execution details, or empty if not found
     */
    @Transactional
    public Optional<JobExecutionDTO> getJobExecutionById(UUID jobExecutionId) {
        Optional<JobExecutionEntity> jobExecution = jobExecutionRepository.findById(jobExecutionId);

        if (jobExecution.isEmpty()) {
            return Optional.empty();
        }

        JobExecutionEntity entity = jobExecution.get();
        Optional<JobEntity> jobEntity = jobRepository.findById(entity.getJobId());

        jobEntity.ifPresent(job -> {
            // Force initialization of the script collection
            job.getScript().size(); // forces Hibernate to load it
            job.getArtifacts().size();
        });

        return Optional.of(mapper.toJobExecutionDto(entity, jobEntity.orElse(null)));
    }

//    /**
//     * Retrieves job details by ID.
//     *
//     * @param jobId The job ID
//     * @return Optional containing the job details, or empty if not found
//     */
//    public Optional<JobDTO> getJobById(UUID jobId) {
//        return jobRepository.findById(jobId)
//                .map(mapper::toJobDto);
//    }
}