package edu.neu.cs6510.sp25.t1.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.neu.cs6510.sp25.t1.cli.api.CliBackendClient;
import edu.neu.cs6510.sp25.t1.cli.commands.RunCommand;
import edu.neu.cs6510.sp25.t1.common.api.RunPipelineRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class CliBackendIntegrationTest {
    private CliBackendClient backendClient;
    private CommandLine cmd;

    @BeforeEach
    void setUp() {
        backendClient = mock(CliBackendClient.class);
        cmd = new CommandLine(new RunCommand(backendClient));
    }

    @Test
    void testCliBackendRunPipeline() throws Exception {
        when(backendClient.runPipeline(any(RunPipelineRequest.class)))
                .thenReturn("Pipeline execution started");
    
        int exitCode = cmd.execute("--pipeline", "pipeline.yaml");
    
        assertEquals(0, exitCode);
    
        verify(backendClient).runPipeline(argThat(request -> {
            System.out.println("Mock verification: " + request.getPipeline());
            return request.getPipeline().equals("pipeline.yaml");
        }));
    }    
}
