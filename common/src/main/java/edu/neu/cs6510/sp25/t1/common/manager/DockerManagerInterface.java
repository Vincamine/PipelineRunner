package edu.neu.cs6510.sp25.t1.common.manager;

import edu.neu.cs6510.sp25.t1.common.execution.JobExecution;

public interface DockerManagerInterface {
  String runContainer(JobExecution jobExecution);
  boolean waitForContainer(String containerId);
  void cleanupContainer(String containerId);
}