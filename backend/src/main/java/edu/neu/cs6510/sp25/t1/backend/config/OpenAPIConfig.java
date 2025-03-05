package edu.neu.cs6510.sp25.t1.backend.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
            .info(new Info()
                    .title("CI/CD System API")
                    .version("1.0")
                    .description("API Documentation for CI/CD system - pipelines, stages, jobs, and executions.")
            );
  }
}
