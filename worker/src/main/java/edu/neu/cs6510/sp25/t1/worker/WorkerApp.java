package edu.neu.cs6510.sp25.t1.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"edu.neu.cs6510.sp25.t1"})
@SpringBootApplication
public class WorkerApp {
  public static void main(String[] args) {
    SpringApplication.run(WorkerApp.class, args);
  }
}
