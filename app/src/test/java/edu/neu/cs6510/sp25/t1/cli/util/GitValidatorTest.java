package edu.neu.cs6510.sp25.t1.cli.util;

import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GitValidatorTest {

    private File gitDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary .git directory before each test
        final File gitDir = new File(".git"); 
        if (!gitDir.exists()) {
            Files.createDirectory(gitDir.toPath());
        }
    }

    @AfterEach
    void tearDown() {
        // Delete the temporary .git directory after each test
        if (gitDir.exists()) {
            gitDir.delete();
        }
    }

    @Test
    @DisplayName("✅ Should return true when inside a Git repository")
    void testIsGitRepository_whenGitDirExists_shouldReturnTrue() {
        assertTrue(GitValidator.isGitRepository(), "Expected CLI to detect a Git repository.");
    }

    @Test
    @DisplayName("❌ Should return false when .git directory is missing")
    void testIsGitRepository_whenGitDirDoesNotExist_shouldReturnFalse() {
        gitDir.delete();
        assertFalse(GitValidator.isGitRepository(), "Expected CLI to detect missing Git repository.");
    }

    @Test
    @DisplayName("🚨 Should throw exception when not in a Git repository")
    void testValidateGitRepo_shouldThrowExceptionIfNotInGitRepo() {
        gitDir.delete(); // Ensure .git is missing

        final Exception exception = assertThrows(IllegalStateException.class, GitValidator::validateGitRepo);

        assertTrue(exception.getMessage().contains("Error: This CLI must be run from the root of a Git repository."),
            "Expected exception message when not inside a Git repository.");
    }
}
