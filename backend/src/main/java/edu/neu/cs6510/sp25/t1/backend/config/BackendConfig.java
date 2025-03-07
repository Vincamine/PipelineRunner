package edu.neu.cs6510.sp25.t1.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BackendConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}