package edu.neu.cs6510.sp25.t1.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for RestTemplate.
 */
@Configuration
public class RestTemplateConfig {
  /**
   * Create a RestTemplate bean.
   *
   * @return RestTemplate
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
