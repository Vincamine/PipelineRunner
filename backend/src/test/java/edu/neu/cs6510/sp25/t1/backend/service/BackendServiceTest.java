package edu.neu.cs6510.sp25.t1.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BackendServiceTest {

  private StringRedisTemplate redisTemplate;
  private ValueOperations<String, String> valueOperations;
  private BackendService backendService;
  private static final Logger logger = LoggerFactory.getLogger(BackendService.class);

  @BeforeEach
  void setUp() {
    redisTemplate = mock(StringRedisTemplate.class);
    valueOperations = mock(ValueOperations.class);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    backendService = new BackendService(redisTemplate);
  }

  @Test
  void testGetCachedJobStatus_Found() {
    when(valueOperations.get("job_status:123")).thenReturn("RUNNING");

    Optional<ExecutionState> result = backendService.getCachedJobStatus("123");

    assertTrue(result.isPresent());
    assertEquals(ExecutionState.RUNNING, result.get());
  }

  @Test
  void testGetCachedJobStatus_NotFound() {
    when(valueOperations.get("job_status:123")).thenReturn(null);

    Optional<ExecutionState> result = backendService.getCachedJobStatus("123");

    assertTrue(result.isEmpty());
  }

  @Test
  void testGetCachedJobStatus_ExceptionHandling() {
    when(valueOperations.get(any())).thenThrow(new RuntimeException("Redis error"));

    Optional<ExecutionState> result = backendService.getCachedJobStatus("123");

    assertTrue(result.isEmpty()); // Should return Optional.empty()
  }

  @Test
  void testCacheJobStatus_Success() {
    when(valueOperations.get("job_status:123")).thenReturn(null);

    backendService.cacheJobStatus("123", ExecutionState.SUCCESS);

    verify(valueOperations, times(1)).set(eq("job_status:123"), eq("SUCCESS"), any());
  }

  @Test
  void testCacheJobStatus_AlreadyExists_NoUpdate() {
    when(valueOperations.get("job_status:123")).thenReturn("SUCCESS");

    backendService.cacheJobStatus("123", ExecutionState.SUCCESS);

    // Should not update if status is unchanged
    verify(valueOperations, never()).set(any(), any(), any());
  }

  @Test
  void testCacheJobStatus_ExceptionHandling() {
    doThrow(new RuntimeException("Redis failure"))
            .when(valueOperations)
            .set(any(), any(), any());


    assertDoesNotThrow(() -> backendService.cacheJobStatus("123", ExecutionState.FAILED));

    // Even on failure, it should not crash
  }
}
