package edu.neu.cs6510.sp25.t1.cli.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import edu.neu.cs6510.sp25.t1.cli.CliApp;
import edu.neu.cs6510.sp25.t1.common.logging.PipelineLogger;
import edu.neu.cs6510.sp25.t1.common.utils.GitCloneUtil;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import picocli.CommandLine;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RunCommandTest {

    @Mock
    private CliApp parentMock;

    // Create a testable version that doesn't try to override methods
    static class TestableRunCommand extends RunCommand {
        // Fields to store arguments
        public String repoValue;
        public String branchValue;
        public String commitValue;
        public String pipelineValue;
        public String filePathValue;
        public boolean localRunValue;

        public TestableRunCommand(CliApp parent) {
            super();
            try {
                Field parentField = RunCommand.class.getDeclaredField("parent");
                parentField.setAccessible(true);
                parentField.set(this, parent);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set parent", e);
            }
        }

        // Helper method to set up the object for testing
        public void setupForTest() throws Exception {
            // Use reflection to set the private fields in the parent class
            setPrivateField(this, "repo", repoValue);
            setPrivateField(this, "branch", branchValue);
            setPrivateField(this, "commit", commitValue);
            setPrivateField(this, "pipeline", pipelineValue);
            setPrivateField(this, "filePath", filePathValue);
            setPrivateField(this, "localRun", localRunValue);
        }

        private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
            Field field = RunCommand.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        }

        // Helper method to create a request directly (since we can't override the private method)
        public Request createTestRequest(String url, String payload) {
            RequestBody body = RequestBody.create(payload, MediaType.get("application/json; charset=utf-8"));
            return new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
        }
    }

    @Mock
    private OkHttpClient httpClientMock;

    @Mock
    private Call callMock;

    @Mock
    private Response responseMock;

    @Mock
    private ResponseBody responseBodyMock;

    private TestableRunCommand testableRunCommand;

    @BeforeEach
    public void setUp() {
        testableRunCommand = new TestableRunCommand(parentMock);
    }

//    @Test
//    public void testMissingFilePathForLocalRun() throws Exception {
//        // Arrange
//        testableRunCommand.localRunValue = true;
//        testableRunCommand.filePathValue = null;
//        testableRunCommand.repoValue = null;
//        testableRunCommand.setupForTest();
//
//        try (MockedStatic<PipelineLogger> loggerMock = mockStatic(PipelineLogger.class)) {
//            // Execute - call method directly
//            int result = testableRunCommand.call();
//
//            // Assert
//            assertEquals(1, result);
//
//            // Verify
//            loggerMock.verify(() ->
//                    PipelineLogger.error("Pipeline configuration file must be specified when running locally (-f)."));
//        }
//    }

    @Test
    public void testExtractRepoName_ValidGithubUrl() throws Exception {
        // Use reflection to access private method
        Method method = RunCommand.class.getDeclaredMethod("extractRepoName", String.class);
        method.setAccessible(true);

        // Test https URL format
        String repoName1 = (String) method.invoke(testableRunCommand, "https://github.com/username/repo-name");
        assertEquals("repo-name", repoName1);

        // Test SSH URL format
        String repoName2 = (String) method.invoke(testableRunCommand, "git@github.com:username/repo-name.git");
        assertEquals("repo-name", repoName2);
    }

    @Test
    public void testExtractRepoName_InvalidUrl() throws Exception {
        // Use reflection to access private method
        Method method = RunCommand.class.getDeclaredMethod("extractRepoName", String.class);
        method.setAccessible(true);

        // Test with invalid URL - will throw IllegalArgumentException wrapped in an InvocationTargetException
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            method.invoke(testableRunCommand, "invalid-url");
        });

        // Assert that the cause is IllegalArgumentException
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertTrue(exception.getCause().getMessage().contains("Invalid GitHub repository URL"));
    }

    @Test
    public void testCreatePostRequest() throws Exception {
        // Use reflection to access private method
        Method method = RunCommand.class.getDeclaredMethod("createPostRequest", String.class, String.class);
        method.setAccessible(true);

        // Arrange
        String testUrl = "http://example.com";
        String testPayload = "{\"key\":\"value\"}";

        // Act
        Request request = (Request) method.invoke(testableRunCommand, testUrl, testPayload);

        // Assert
        assertNotNull(request);
        assertEquals(testUrl+"/", request.url().toString());
        assertEquals("POST", request.method());
        assertTrue(request.header("Content-Type").contains("application/json"));
        assertTrue(request.header("Accept").contains("application/json"));

        // Verify the request has a body
        assertNotNull(request.body());

        // We can indirectly verify content length if needed
        try {
            assertEquals(testPayload.length(), request.body().contentLength());
        } catch (IOException e) {
            fail("Failed to get content length: " + e.getMessage());
        }
    }

    @Test
    public void testCreatePostRequestWithComplexPayload() throws Exception {
        // Use reflection to access private method
        Method method = RunCommand.class.getDeclaredMethod("createPostRequest", String.class, String.class);
        method.setAccessible(true);

        // Arrange
        String testUrl = "http://localhost:8080/api/pipeline/run";
        String jsonPayload = String.format(
                "{\"repo\": \"%s\", \"branch\": \"%s\", \"commit\": \"%s\", \"pipeline\": \"%s\", \"filePath\": \"%s\", \"local\": %s}",
                "https://github.com/test/repo", "main", "abc123", "test-pipeline", "/path/to/file", true);

        // Act
        Request request = (Request) method.invoke(testableRunCommand, testUrl, jsonPayload);

        // Assert
        assertNotNull(request);
        assertEquals(testUrl, request.url().toString());
        assertEquals("POST", request.method());
        assertEquals("application/json", request.header("Content-Type"));
        assertEquals("application/json", request.header("Accept"));

        // Verify content-type and length
        MediaType contentType = request.body().contentType();
        assertNotNull(contentType);
        assertEquals("application", contentType.type());
        assertEquals("json", contentType.subtype());

        try {
            assertEquals(jsonPayload.length(), request.body().contentLength());
        } catch (IOException e) {
            fail("Failed to get content length: " + e.getMessage());
        }
    }

    @Test
    public void testErrorHandling() throws Exception {
        // Set up the command with valid values
        testableRunCommand.repoValue = "https://github.com/org/repo";
        testableRunCommand.branchValue = "main";
        testableRunCommand.setupForTest();

        try (MockedStatic<PipelineLogger> loggerMock = mockStatic(PipelineLogger.class)) {
            // Set up to throw an exception
            loggerMock.when(() -> PipelineLogger.info(anyString())).thenThrow(new RuntimeException("Test exception"));

            // Call the method
            int result = testableRunCommand.call();

            // Verify the error is logged
            assertEquals(1, result);
            loggerMock.verify(() -> PipelineLogger.error(contains("Execution Error:")));
        }
    }
}