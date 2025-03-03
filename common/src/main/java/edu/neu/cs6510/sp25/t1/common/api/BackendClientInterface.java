package edu.neu.cs6510.sp25.t1.common.api;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;

public interface BackendClientInterface {
  void sendJobStatus(String jobName, ExecutionStatus status);
  ExecutionStatus getJobStatus(String jobName);
}