package edu.neu.cs6510.sp25.t1.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GitValidatorTest {

    @Test
    void testIsGitRepositoryTrue(@TempDir Path tempDir) {
        File gitDir = new File(tempDir.toFile(), ".git");
        assertTrue(gitDir.mkdir());

        assertTrue(GitValidator.isGitRepository());
    }

    @Test
    void testIsGitRepositoryFalse(@TempDir Path tempDir) {
        assertFalse(GitValidator.isGitRepository());
    }

    @Test
    void testValidateGitRepoThrowsException() {
        Exception exception = assertThrows(IllegalStateException.class, GitValidator::validateGitRepo);
        assertTrue(exception.getMessage().contains("This CLI must be run from the root of a Git repository."));
    }
}
