package edu.neu.cs6510.sp25.t1.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main class to start the Spring Boot application.
 * keep this file in currrent folder under src/main/java/edu/neu/cs6510/sp25/t1/backend for the application to run
 */
@SpringBootApplication
@ComponentScan("edu.neu.cs6510.sp25.t1") // Scan all packages for components
public class BackendApp {
    public static void main(String[] args) {
        SpringApplication.run(BackendApp.class, args);
    }
}
