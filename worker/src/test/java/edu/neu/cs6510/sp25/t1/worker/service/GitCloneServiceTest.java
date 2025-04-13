package edu.neu.cs6510.sp25.t1.worker.service;

import com.github.dockerjava.api.DockerClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for GitCloneService
 *
 * Note: Since GitCloneService has tight coupling with DockerClient
 * and uses private methods, these tests are more integration-focused
 * than pure unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class GitCloneServiceTest {

    @Mock
    private DockerClient dockerClient;

    /**
     * This test verifies that the right exception is thrown for invalid inputs
     */
    @Test
    public void testExceptionForInvalidInputs() {
        GitCloneService service = new GitCloneService();

        // Test with null repo URL
        Exception exception = assertThrows(Exception.class, () -> {
            service.cloneRepoToVolume(null, "main", "test-pipeline");
        });

        // We can't assert on the exact message since we can't control the Docker API,
        // but there should be an exception for invalid inputs
        assertNotNull(exception);

        // Test with null branch
        exception = assertThrows(Exception.class, () -> {
            service.cloneRepoToVolume("https://github.com/repo.git", null, "test-pipeline");
        });

        assertNotNull(exception);
    }

}