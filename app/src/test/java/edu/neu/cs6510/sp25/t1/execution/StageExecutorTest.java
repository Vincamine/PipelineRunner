package edu.neu.cs6510.sp25.t1.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StageExecutorTest {

  @Mock
  private JobExecutor job1;

  @Mock
  private JobExecutor job2;

  @Mock
  private JobExecutor job3;

  @InjectMocks
  private StageExecutor stageExecutor;

  @BeforeEach
  void setup() {
    List<JobExecutor> jobs = Arrays.asList(job1, job2, job3);
    stageExecutor = new StageExecutor("Build Stage", jobs);
  }

  @Test
  void testExecute_Success() {
    when(job1.getStatus()).thenReturn(ExecutionStatus.SUCCESSFUL);
    when(job2.getStatus()).thenReturn(ExecutionStatus.SUCCESSFUL);
    when(job3.getStatus()).thenReturn(ExecutionStatus.SUCCESSFUL);

    stageExecutor.execute();

    assertEquals(ExecutionStatus.SUCCESSFUL, stageExecutor.getStatus());
    verify(job1).execute();
    verify(job2).execute();
    verify(job3).execute();
  }

  @Test
  void testExecute_Failure() {
    when(job1.getStatus()).thenReturn(ExecutionStatus.SUCCESSFUL);
    when(job2.getStatus()).thenReturn(ExecutionStatus.FAILED); // Should not be executed

    stageExecutor.execute();

    assertEquals(ExecutionStatus.FAILED, stageExecutor.getStatus());
    verify(job1).execute();
    verify(job2).execute();
    verify(job3, never()).execute(); // Should not execute after failure
  }

  @Test
  void testExecute_MixedJobs() {
    when(job1.getStatus()).thenReturn(ExecutionStatus.SUCCESSFUL);
    when(job2.getStatus()).thenReturn(ExecutionStatus.SUCCESSFUL);
    when(job3.getStatus()).thenReturn(ExecutionStatus.FAILED);

    stageExecutor.execute();

    assertEquals(ExecutionStatus.FAILED, stageExecutor.getStatus());
    verify(job1).execute();
    verify(job2).execute();
    verify(job3).execute();
  }
}
