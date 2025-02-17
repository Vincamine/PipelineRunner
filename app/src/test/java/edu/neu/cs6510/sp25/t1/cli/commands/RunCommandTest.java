package edu.neu.cs6510.sp25.t1.cli.commands;


import edu.neu.cs6510.sp25.t1.validation.YamlPipelineValidator;

import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.*;

class RunCommandTest {

    @SuppressWarnings("unused")
    private RunCommand runCommand;
    @SuppressWarnings("unused")
    private YamlPipelineValidator validator;

    @BeforeEach
    void setUp() {
        validator = mock(YamlPipelineValidator.class);
        runCommand = spy(new RunCommand());
    }
    

    // @Test
    // void testRun_InvalidPipeline_Failure() {
    //     doReturn("dummy config").when(runCommand).readPipelineConfig(anyString());
    //     doReturn(false).when(validator).validatePipeline(anyString());

    //     final int exitCode = new CommandLine(runCommand).execute();
    //     assertNotEquals(0, exitCode);
    // }

    // @Test
    // void testRun_ApiFailure() {
    //     doReturn("dummy config").when(runCommand).readPipelineConfig(anyString());
    //     doReturn(true).when(validator).validatePipeline(anyString());
    //     doReturn(new ApiResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, "API failure")).when(runCommand).sendRequestToApi(any());

    //     final int exitCode = new CommandLine(runCommand).execute();
    //     assertNotEquals(0, exitCode);
    // }
}
