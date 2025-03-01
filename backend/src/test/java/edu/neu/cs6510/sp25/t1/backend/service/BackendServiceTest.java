package edu.neu.cs6510.sp25.t1.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import edu.neu.cs6510.sp25.t1.common.model.ExecutionState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class BackendServiceTest {

  private StringRedisTemplate redisTemplate;
  private ValueOperations<String, String> valueOperations;
  private BackendService backendService;

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
  void testCacheJobStatus_Success() {
    backendService.cacheJobStatus("123", ExecutionState.SUCCESS);

    verify(valueOperations, times(1)).set(eq("job_status:123"), eq("SUCCESS"), any());
  }
}
