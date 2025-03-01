package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;

import java.time.Duration;
import java.util.Optional;

/**
 * Service for managing job status caching in Redis.
 * Caches job statuses to avoid redundant queries to the database.
 */
@Service
public class BackendService {

    private static final Logger logger = LoggerFactory.getLogger(BackendService.class);

    private static final String JOB_STATUS_PREFIX = "job_status:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1); // Expiration time of 1 hour

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public BackendService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Retrieves the cached job status from Redis.
     *
     * @param jobId The job ID.
     * @return An {@link Optional} containing the cached job status, or empty if not found.
     */
    public Optional<ExecutionState> getCachedJobStatus(String jobId) {
        try {
            String status = redisTemplate.opsForValue().get(JOB_STATUS_PREFIX + jobId);
            return Optional.ofNullable(status).map(ExecutionState::valueOf);
        } catch (Exception e) {
            logger.error("Failed to retrieve job status from Redis for jobId={}", jobId, e);
            return Optional.empty();
        }
    }

    /**
     * Caches the job status in Redis with a default expiration time.
     * Avoids redundant writes by checking if the value has changed.
     *
     * @param jobId  The job ID.
     * @param status The job execution status.
     */
    public void cacheJobStatus(String jobId, ExecutionState status) {
        try {
            String existingStatus = redisTemplate.opsForValue().get(JOB_STATUS_PREFIX + jobId);
            if (existingStatus == null || !existingStatus.equals(status.name())) {
                redisTemplate.opsForValue().set(JOB_STATUS_PREFIX + jobId, status.name(), DEFAULT_TTL);
                logger.info("Cached job status: jobId={} status={}", jobId, status);
            } else {
                logger.info("Job status unchanged, skipping cache update: jobId={} status={}", jobId, status);
            }
        } catch (Exception e) {
            logger.error("Failed to cache job status for jobId={}", jobId, e);
        }
    }
}
