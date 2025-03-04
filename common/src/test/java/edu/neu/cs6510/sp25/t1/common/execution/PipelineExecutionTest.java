package edu.neu.cs6510.sp25.t1.common.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import edu.neu.cs6510.sp25.t1.common.enums.ExecutionStatus;
import edu.neu.cs6510.sp25.t1.common.enums.PipelineExecutionState;
import edu.neu.cs6510.sp25.t1.common.model.Stage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PipelineExecutionTest {

  private PipelineExecution pipelineExecution;

  @BeforeEach
  void setUp() {
    // Mock the Stage object
    Stage mockStage1 = mock(Stage.class);
    when(mockStage1.getName()).thenReturn("stage-1");

    Stage mockStage2 = mock(Stage.class);
    when(mockStage2.getName()).thenReturn("stage-2");

    // Create StageExecution instances using the mocked Stage objects
    List<StageExecution> stages = new ArrayList<>();
    stages.add(new StageExecution(mockStage1, new ArrayList<>())); // Empty job executions
    stages.add(new StageExecution(mockStage2, new ArrayList<>()));

    pipelineExecution = new PipelineExecution("CI-Pipeline", stages);
  }

  @Test
  void testConstructorInitialization() {
    assertEquals("CI-Pipeline", pipelineExecution.getPipelineName());
    assertEquals(PipelineExecutionState.PENDING, pipelineExecution.getState());
    assertNotNull(pipelineExecution.getStartTime());
    assertNotNull(pipelineExecution.getLastUpdated());
    assertNotNull(pipelineExecution.getStages());
    assertEquals(2, pipelineExecution.getStages().size());
  }

  @Test
  void testConstructorHandlesNullStages() {
    PipelineExecution execution = new PipelineExecution("Build-Pipeline", null);

    assertEquals("Build-Pipeline", execution.getPipelineName());
    assertNotNull(execution.getStages());
    assertTrue(execution.getStages().isEmpty());
  }

  @Test
  void testUpdateStateToRunning() {
    pipelineExecution.updateState();
    assertEquals(PipelineExecutionState.RUNNING, pipelineExecution.getState());
  }

  @Test
  void testUpdateStateToSuccess() {
    pipelineExecution.getStages().forEach(stage -> stage.setStatus(ExecutionStatus.SUCCESS));
    pipelineExecution.updateState();
    assertEquals(PipelineExecutionState.SUCCESSFUL, pipelineExecution.getState());
  }

  @Test
  void testUpdateStateToFailed() {
    pipelineExecution.getStages().getFirst().setStatus(ExecutionStatus.FAILED);
    pipelineExecution.updateState();
    assertEquals(PipelineExecutionState.FAILED, pipelineExecution.getState());
  }

  @Test
  void testSetState() {
    pipelineExecution.setState(PipelineExecutionState.SUCCESSFUL);
    assertEquals(PipelineExecutionState.SUCCESSFUL, pipelineExecution.getState());
  }
}
