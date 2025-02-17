package edu.neu.cs6510.sp25.t1.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class GitValidatorTest {

    @Test
    void testIsGitRepositoryTrue(@TempDir Path tempDir) {
        final File gitDir = new File(tempDir.toFile(), ".git");
        assertTrue(gitDir.mkdir());

        assertTrue(GitValidator.isGitRepository());
    }

    @Test
    void testIsGitRepositoryFalse(@TempDir Path tempDir) {
        Path originalWorkingDir = Paths.get(System.getProperty("user.dir")); // Store current working directory
        try {
            // Change working directory to the temporary non-git directory
            System.setProperty("user.dir", tempDir.toString());

            // Verify that isGitRepository() returns false in this clean temp directory
            assertFalse(GitValidator.isGitRepository(), "Expected false because this is not a Git repository");
        } finally {
            // Restore original working directory after test completes
            System.setProperty("user.dir", originalWorkingDir.toString());
        }
    }

    @Test
    void testValidateGitRepoThrowsException(@TempDir Path tempDir) {
        Path originalWorkingDir = Paths.get(System.getProperty("user.dir")); // Store current working directory

        try {
            // Change working directory to the temporary directory (which has no .git)
            System.setProperty("user.dir", tempDir.toString());

            // Now, GitValidator.validateGitRepo() should throw an IllegalStateException
            final Exception exception = assertThrows(IllegalStateException.class, GitValidator::validateGitRepo);
            assertTrue(exception.getMessage().contains("This CLI must be run from the root of a Git repository."));
        } finally {
            // Restore the original working directory
            System.setProperty("user.dir", originalWorkingDir.toString());
        }
    }

}
