package edu.neu.cs6510.sp25.t1.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class BackendService {
    
    private static final String JOB_STATUS_PREFIX = "job_status:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1); // Set expiration to 1 hour

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public BackendService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Retrieves the cached job status from Redis.
     * @param jobId the job ID
     * @return the cached job status or "UNKNOWN" if not found
     */
    public String getCachedJobStatus(String jobId) {
        String status = redisTemplate.opsForValue().get(JOB_STATUS_PREFIX + jobId);
        return (status != null) ? status : "UNKNOWN"; // Return a default status if not found
    }

    /**
     * Caches the job status in Redis with a default expiration time.
     * @param jobId the job ID
     * @param status the job status
     */
    public void cacheJobStatus(String jobId, String status) {
        redisTemplate.opsForValue().set(JOB_STATUS_PREFIX + jobId, status, DEFAULT_TTL);
    }
}
