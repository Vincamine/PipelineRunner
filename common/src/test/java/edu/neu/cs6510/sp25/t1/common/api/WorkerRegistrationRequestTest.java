package edu.neu.cs6510.sp25.t1.common.api;

import org.junit.jupiter.api.Test;

import edu.neu.cs6510.sp25.t1.common.api.request.WorkerRegistrationRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class WorkerRegistrationRequestTest {

  @Test
  void testWorkerRegistrationRequestConstructorAndGetters() {
    WorkerRegistrationRequest request = new WorkerRegistrationRequest("worker123", "192.168.1.1", "docker,linux");

    assertEquals("worker123", request.getWorkerId());
    assertEquals("192.168.1.1", request.getIpAddress());
    assertEquals("docker,linux", request.getCapabilities());
  }

  @Test
  void testWorkerRegistrationRequestDefaultConstructor() {
    WorkerRegistrationRequest request = new WorkerRegistrationRequest();

    assertNull(request.getWorkerId());
    assertNull(request.getIpAddress());
    assertNull(request.getCapabilities());
  }

  @Test
  void testWorkerRegistrationRequestSetters() {
    WorkerRegistrationRequest request = new WorkerRegistrationRequest();
    request.setWorkerId("worker123");
    request.setIpAddress("192.168.1.1");
    request.setCapabilities("docker,linux");

    assertEquals("worker123", request.getWorkerId());
    assertEquals("192.168.1.1", request.getIpAddress());
    assertEquals("docker,linux", request.getCapabilities());
  }
}
