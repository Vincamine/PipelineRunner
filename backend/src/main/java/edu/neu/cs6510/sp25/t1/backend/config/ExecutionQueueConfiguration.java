package edu.neu.cs6510.sp25.t1.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for the execution queue system.
 * These properties control the behavior of the execution queues.
 */
@Configuration
@ConfigurationProperties(prefix = "execution.queue")
@Data
public class ExecutionQueueConfiguration {
    
    /**
     * Maximum number of pipeline executions that can run concurrently.
     */
    private int maxConcurrentPipelines = 5;
    
    /**
     * Maximum number of stage executions that can run concurrently.
     */
    private int maxConcurrentStages = 10;
    
    /**
     * Maximum number of job executions that can run concurrently.
     */
    private int maxConcurrentJobs = 20;
}