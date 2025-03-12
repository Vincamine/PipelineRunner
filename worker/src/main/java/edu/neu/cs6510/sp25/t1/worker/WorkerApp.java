package edu.neu.cs6510.sp25.t1.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import edu.neu.cs6510.sp25.t1.worker.config.ArtifactProperties;
import edu.neu.cs6510.sp25.t1.worker.config.DockerProperties;
import edu.neu.cs6510.sp25.t1.worker.config.WorkerApiProperties;
import edu.neu.cs6510.sp25.t1.worker.config.WorkerProperties;

@SpringBootApplication
@EnableConfigurationProperties({DockerProperties.class, ArtifactProperties.class, WorkerProperties.class, WorkerApiProperties.class})
public class WorkerApp {
  public static void main(String[] args) {
    SpringApplication.run(WorkerApp.class, args);
  }
}
