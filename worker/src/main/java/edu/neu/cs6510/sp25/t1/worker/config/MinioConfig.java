package edu.neu.cs6510.sp25.t1.worker.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

  @Bean
  public MinioClient minioClient(MinioProperties properties) {
    return MinioClient.builder()
        .endpoint(properties.getEndpoint())
        .credentials(properties.getAccessKey(), properties.getSecretKey())
        .build();
  }
}
