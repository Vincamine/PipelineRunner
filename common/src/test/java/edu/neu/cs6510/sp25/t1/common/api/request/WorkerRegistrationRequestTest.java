package edu.neu.cs6510.sp25.t1.common.api.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WorkerRegistrationRequestTest {

  @Test
  void testDefaultConstructor() {
    WorkerRegistrationRequest request = new WorkerRegistrationRequest();
    assertNull(request.getWorkerId());
    assertNull(request.getIpAddress());
    assertNull(request.getCapabilities());
  }

  @Test
  void testParameterizedConstructor() {
    WorkerRegistrationRequest request = new WorkerRegistrationRequest("worker-123", "192.168.1.1", "CPU,GPU");
    assertEquals("worker-123", request.getWorkerId());
    assertEquals("192.168.1.1", request.getIpAddress());
    assertEquals("CPU,GPU", request.getCapabilities());
  }

  @Test
  void testSetAndGetWorkerId() {
    WorkerRegistrationRequest request = new WorkerRegistrationRequest();
    request.setWorkerId("worker-456");
    assertEquals("worker-456", request.getWorkerId());
  }

  @Test
  void testSetAndGetIpAddress() {
    WorkerRegistrationRequest request = new WorkerRegistrationRequest();
    request.setIpAddress("10.0.0.2");
    assertEquals("10.0.0.2", request.getIpAddress());
  }

  @Test
  void testSetAndGetCapabilities() {
    WorkerRegistrationRequest request = new WorkerRegistrationRequest();
    request.setCapabilities("Memory,Docker");
    assertEquals("Memory,Docker", request.getCapabilities());
  }

  @Test
  void testSetNullValues() {
    WorkerRegistrationRequest request = new WorkerRegistrationRequest();
    request.setWorkerId(null);
    request.setIpAddress(null);
    request.setCapabilities(null);

    assertNull(request.getWorkerId());
    assertNull(request.getIpAddress());
    assertNull(request.getCapabilities());
  }
}
