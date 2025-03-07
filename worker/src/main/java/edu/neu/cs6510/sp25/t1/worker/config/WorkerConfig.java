package edu.neu.cs6510.sp25.t1.worker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for Worker module.
 */
@Configuration
public class WorkerConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
