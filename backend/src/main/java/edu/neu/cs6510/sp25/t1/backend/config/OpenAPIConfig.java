package edu.neu.cs6510.sp25.t1.backend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Configuration class for customizing the OpenAPI documentation.
 */
@Configuration
public class OpenAPIConfig {

  /**
   * Customizes the OpenAPI documentation.
   *
   * @return An instance of OpenAPI with custom information.
   */
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
