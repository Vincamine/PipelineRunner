package edu.neu.cs6510.sp25.t1.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for the REST template bean.
 */
@Configuration
public class RestTemplateConfig {
  /**
   * Creates a REST template bean.
   *
   * @return The REST template bean.
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
