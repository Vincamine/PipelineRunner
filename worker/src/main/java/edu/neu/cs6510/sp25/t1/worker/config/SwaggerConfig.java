package edu.neu.cs6510.sp25.t1.worker.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuration class for OpenAPI documentation.
 * <p>
 * This class configures Swagger UI for API documentation and testing using springdoc-openapi.
 */
@Configuration
public class SwaggerConfig {

  /**
   * Creates the GroupedOpenApi bean for the worker API.
   *
   * @return GroupedOpenApi bean configured for the worker API
   */
  @Bean
  public GroupedOpenApi workerApi() {
    return GroupedOpenApi.builder()
            .group("worker-api")
            .packagesToScan("edu.neu.cs6510.sp25.t1.worker.api.controller")
            .build();
  }

  /**
   * Constructs OpenAPI information for documentation.
   *
   * @return OpenAPI object with API information details
   */
  @Bean
  public OpenAPI workerOpenAPI() {
    return new OpenAPI()
            .info(new Info()
                    .title("Worker Service API")
                    .description("API documentation for the Worker Service component of the Pipeline Execution System")
                    .version("1.0.0")
                    .contact(new Contact()
                            .name("CS6510 Team")
                            .url("https://github.com/edu-neu-cs6510-sp25-t1")
                            .email("contact@example.com"))
                    .license(new License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")));
  }
}