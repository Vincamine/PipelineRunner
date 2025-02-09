package edu.neu.cs6510.sp25.t1.cli.util;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for validating if the CLI is running inside a Git repository.
 * 
 * This class ensures that commands requiring a Git environment are executed 
 * from within a valid Git repository.
 */
public class GitValidatorTest {

    private static final Logger LOGGER = Logger.getLogger(GitValidatorTest.class.getName());

    /**
     * Checks if the current directory or any parent directory is a Git repository.
     * 
     * This method starts from the current working directory and moves up the directory
     * tree until it finds a `.git` folder.
     *
     * @return {@code true} if a `.git` directory is found in the current or any parent directory,
     *         {@code false} otherwise.
     */
    public static boolean isGitRepository() {
        File currentDir = new File(System.getProperty("user.dir"));

        while (currentDir != null) {
            final File gitDir = new File(currentDir, ".git");

            LOGGER.log(Level.INFO, "DEBUG: Checking {0}", gitDir.getAbsolutePath());

            if (gitDir.exists() && gitDir.isDirectory()) {
                LOGGER.log(Level.INFO, "✅ Git repository detected at {0}", currentDir.getAbsolutePath());
                return true;
            }

            currentDir = currentDir.getParentFile(); // Move up one level
        }

        LOGGER.log(Level.WARNING, "❌ No Git repository detected.");
        return false;
    }

    /**
     * Validates whether the current working directory is inside a Git repository.
     * 
     * If a `.git` directory is not found, this method throws an exception.
     * 
     * @throws IllegalStateException if the CLI is not executed from a Git repository.
     */
    public static void validateGitRepo() {
        if (!isGitRepository()) {
            LOGGER.log(Level.SEVERE, "❌ Error: This CLI must be run from the root of a Git repository.");
            throw new IllegalStateException("Error: This CLI must be run from the root of a Git repository.");
        }
    }
}
