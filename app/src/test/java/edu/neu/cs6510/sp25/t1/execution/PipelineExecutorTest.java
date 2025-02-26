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
public class PipelineExecutorTest {

  @Mock
  private StageExecutor stage1;

  @Mock
  private StageExecutor stage2;

  @Mock
  private StageExecutor stage3;

  @InjectMocks
  private PipelineExecutor pipelineExecutor;

  @BeforeEach
  void setup() {
    List<StageExecutor> stages = Arrays.asList(stage1, stage2, stage3);
    pipelineExecutor = new PipelineExecutor("CI/CD Pipeline", stages);
  }

  @Test
  void testExecute_Success() {
    when(stage1.getStatus()).thenReturn(ExecutionStatus.SUCCESSFUL);
    when(stage2.getStatus()).thenReturn(ExecutionStatus.SUCCESSFUL);
    when(stage3.getStatus()).thenReturn(ExecutionStatus.SUCCESSFUL);

    pipelineExecutor.execute();

    assertEquals(ExecutionStatus.SUCCESSFUL, pipelineExecutor.getStatus());
    verify(stage1).execute();
    verify(stage2).execute();
    verify(stage3).execute();
  }

  @Test
  void testExecute_Failure() {
    when(stage1.getStatus()).thenReturn(ExecutionStatus.SUCCESSFUL);
    when(stage2.getStatus()).thenReturn(ExecutionStatus.FAILED); // Should not be executed

    pipelineExecutor.execute();

    assertEquals(ExecutionStatus.FAILED, pipelineExecutor.getStatus());
    verify(stage1).execute();
    verify(stage2).execute();
    verify(stage3, never()).execute(); // Should not execute after failure
  }

  @Test
  void testExecute_MixedStages() {
    when(stage1.getStatus()).thenReturn(ExecutionStatus.SUCCESSFUL);
    when(stage2.getStatus()).thenReturn(ExecutionStatus.SUCCESSFUL);
    when(stage3.getStatus()).thenReturn(ExecutionStatus.FAILED);

    pipelineExecutor.execute();

    assertEquals(ExecutionStatus.FAILED, pipelineExecutor.getStatus());
    verify(stage1).execute();
    verify(stage2).execute();
    verify(stage3).execute();
  }
}
